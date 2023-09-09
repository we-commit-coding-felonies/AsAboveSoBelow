package com.quartzshard.aasb.common.entity.projectile;

import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.quartzshard.aasb.common.damage.source.AASBDmgSrc;
import com.quartzshard.aasb.common.entity.ai.ArrowPathNavigation;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.AmuletItem;
import com.quartzshard.aasb.common.network.AASBNet;
import com.quartzshard.aasb.common.network.client.DrawParticleLinePacket;
import com.quartzshard.aasb.common.network.client.DrawParticleLinePacket.LineParticlePreset;
import com.quartzshard.aasb.config.DebugCfg;
import com.quartzshard.aasb.data.AASBTags.BlockTP;
import com.quartzshard.aasb.data.AASBTags.EntityTP;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.EntityHelper;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * An arrow that will automatically search for, chase, and kill entities <br>
 * Intelligently navigates around obstacles, and can be manually redirected by its owner
 * @author solunareclipse1, quartzshard
 */
public class SentientArrow extends AbstractArrow {
	private ArrowPathNavigation nav = new ArrowPathNavigation(this, level);
	
	/** when tickCount > this, arrow dies */
	private final int maxLife;
	
	private enum ArrowState {
		SEARCHING, // Looking for a target
		DIRECT,    // Has direct line of sight to target
		PATHING,   // Following path to to target
		INERT
	}
	private static final EntityDataAccessor<Byte> AI_STATE = SynchedEntityData.defineId(SentientArrow.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(SentientArrow.class, EntityDataSerializers.INT);
	
	/**
	 * the position we are currently going toward <br>
	 * usually the position of target entity, or next node in path
	 */
	@Nullable
	private Vec3 targetPos = null;
	
	/** The current path we are moving along */
	@Nullable
	private Path currentPath = null;
	
	/**
	 * "memory" of previous paths this arrow has taken <br>
	 * used for making sure its not going in circles
	 */
	private Stack<Path> previousPaths = new Stack<>();
	
	private boolean isReturningToOwner = false;
	private int searchTime = 0;
	
	public SentientArrow(EntityType<? extends SentientArrow> entityType, Level level) {
		super(entityType, level);
		maxLife = 200;
	}

	public SentientArrow(Level level, double x, double y, double z) {
		super(ObjectInit.Entities.SENTIENT_ARROW.get(), x,y,z, level);
		maxLife = 200;
	}

	public SentientArrow(Level level, LivingEntity shooter) {
		super(ObjectInit.Entities.SENTIENT_ARROW.get(), shooter, level);
		maxLife = 200;
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(AI_STATE, (byte) 0);
		entityData.define(TARGET_ID, -1);
	}
	
	@Override
	public void tick() {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "Tick");
		if ( tickCount > maxLife || owner() == null ) {
			if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "OldOrBadOwner");
			this.kill();
		}
		else if (tickCount < 5) {
			if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "YoungArrow");
			this.setPos(position().add(this.getDeltaMovement()));
		}
		else {
			if (isLookingForTarget()) {
				if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "LookingForTarget");
				if (searchTime < 10) {
					searchTime++;
					if (!attemptAutoRetarget() && searchTime >= 10) {
						if (tickCount == searchTime) {
							becomeInert();
						}
						else {
							isReturningToOwner = true;
						}
					}
				} else {
					becomeInert();
				}
			}
			// TRAJECTORY MODIFICATION
			if (isHoming()) {
				if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "Homing");
				Entity target = isReturningToOwner ? owner() : getTarget();
				if (isReturningToOwner || this.shouldContinueHomingTowards(target)) {
					boolean lineOfSight = canSee(target);
					if (lineOfSight) {
						if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "LineOfSight");
						// BEELINE
						forgetPaths();
						targetPos = target.getBoundingBox().getCenter();
						if (getState() != ArrowState.DIRECT) {
							//particles(0);
							setState(ArrowState.DIRECT); // target visible
						}
					} else {
						if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "Obstructed");
						// PATHFIND
						setState(ArrowState.PATHING); // target obstructed
						pathTo(target);
					}
					if (!isInert() && targetPos != null) {
						if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "Redirecting");
						if (!level.isClientSide) {
							shootAt(targetPos, 3f);
						}
					}
				} else {
					if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "InvalidTarget");
					resetTarget();
					setState(ArrowState.SEARCHING); // searching for target
				}
			}
			// MOVEMENT & COLLISION
			moveAndCollide();
		}
		this.hasImpulse = true;
	}
	
	@Override
	protected void onHitBlock(BlockHitResult hitRes) {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "OnHitBlock");
		BlockPos pos = hitRes.getBlockPos();
		BlockState hit = level.getBlockState(hitRes.getBlockPos());
		if (!hit.isAir()) {
			// TODO re-enable when implemented
			if (false && hit.is(BlockTP.ARROW_ANNIHILATE)) {
				if (transmuteBlockIntoCovDust(pos)) {
					//level.playSound(null, pos, EffectInit.ARCHANGELS_SENTIENT_HIT.get(), this.getSoundSource(), 1, 2);
					return; // dont need to process collision on something that doesnt exist
				}
			} else if (hit.is(BlockTP.ARROW_NOCLIP)) {
				return;
			}
		}
		if (isLookingForTarget()) {
			if (!attemptAutoRetarget()) {
				becomeInert();
			}
		}
		if (isInert()) {
			super.onHitBlock(hitRes);
		}
	}
	
	@Override
	protected void onHitEntity(EntityHitResult hitRes) {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "OnHitEntity");
		Entity hit = hitRes.getEntity();
		if (isReturningToOwner && hit.is(owner())) {
			isReturningToOwner = false;
			if (!attemptAutoRetarget()) {
				isReturningToOwner = true;
			}
		} else if (!hit.is(owner())) {
			// not owner
			if (hit instanceof LivingEntity entity) {
				attemptToTransmuteEntity(entity);
			}
		}
	}
	
	/**
	 * AbstractArrow.tick() with some minor changes
	 */
	private void moveAndCollide() {
		projectileTick();
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "MoveAndCollide");
		boolean noClip = !isInert() || this.isNoPhysics();
		Vec3 curPos = this.position();
		Vec3 motion = getDeltaMovement();
		double horizVel = motion.horizontalDistance();
		if (!level.isClientSide && this.xRotO == 0.0F && this.yRotO == 0.0F) {
			this.setXRot((float)(Mth.atan2(motion.y, horizVel) * (180F / (float)Math.PI)));
			this.setYRot((float)(Mth.atan2(motion.x, motion.z) * (180F / (float)Math.PI)));
			this.xRotO = this.getXRot();
			this.yRotO = this.getYRot();
		}

		BlockPos curBlockPos = this.blockPosition();
		BlockState blockInside = this.level.getBlockState(curBlockPos);
		if (!blockInside.isAir()) {
			/*if (blockInside.is(BlockTP.ARROW_ANNIHILATE)) {
				if (transmuteBlockIntoCovDust(curBlockPos)) {
					level.playSound(null, curBlockPos, PESoundEvents.DESTRUCT.get(), this.getSoundSource(), 1, 2);
					return; // dont need to process collision on something that doesnt exist
				}
			} else*/ if (!noClip) {
				VoxelShape blockShape = blockInside.getCollisionShape(this.level, curBlockPos);
				if (!blockShape.isEmpty()) {

					for(AABB aabb : blockShape.toAabbs()) {
						if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "BlockAABBCollisionLoop");
						if (aabb.move(curBlockPos).contains(curPos)) {
							this.inGround = true;
							break;
						}
					}
				}
			}
		}

		if (this.shakeTime > 0) {
			if (noClip) this.shakeTime = 0;
			else this.shakeTime--;
		}

		if (noClip || this.isInWaterOrRain() || blockInside.is(Blocks.POWDER_SNOW)) {
			this.clearFire();
		}
		
		if (this.inGround && !noClip) {
			if (this.lastState != blockInside && this.shouldFall()) {
				this.startFalling();
			} else if (!this.level.isClientSide) {
				this.tickDespawn();
			}
			++this.inGroundTime;
		} else {
			this.inGroundTime = 0;
			Vec3 nextPos = curPos.add(motion);
			HitResult hitRes = this.level.clip(new ClipContext(curPos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
			if (hitRes.getType() != HitResult.Type.MISS) {
				if ( !noClip && !level.getBlockState(new BlockPos(hitRes.getLocation())).is(BlockTP.ARROW_NOCLIP) ) {
					nextPos = hitRes.getLocation();
				}
			}

			boolean didHit = false;
			while(!this.isRemoved()) {
				if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "EntCollisionLoop");
				EntityHitResult entHitRes = this.findHitEntity(curPos, nextPos);
				if (entHitRes != null) {
					hitRes = entHitRes;
				}

				if (hitRes != null && hitRes.getType() == HitResult.Type.ENTITY) {
					Entity victim = ((EntityHitResult)hitRes).getEntity();
					Entity owner = this.getOwner();
					if (victim instanceof Player plrVictim && owner instanceof Player plrOwner && !plrOwner.canHarmPlayer(plrVictim)) {
						hitRes = null;
						entHitRes = null;
					}
				}

				if (hitRes != null && hitRes.getType() != HitResult.Type.MISS && !this.isNoPhysics() && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitRes)) {
					this.onHit(hitRes);
					didHit = true;
					this.hasImpulse = true;
				}

				// game hangs without the pierce level check
				// NOTE TO SELF: do not ever give this a pierce value
				if (entHitRes == null || this.getPierceLevel() <= 0) {
					break;
				}

				hitRes = null;
			}
			if (!didHit && isHoming()) {
				Entity target = isReturningToOwner ? owner() : getTarget();
				if (target != null && target.getBoundingBox().contains(position())) {
					onHit(new EntityHitResult(target, position()));
				}
			}
			
			double velX = motion.x;
			double velY = motion.y;
			double velZ = motion.z;
			if (level instanceof ServerLevel lvl) {
				//ParticleOptions particle = WispParticleData.wisp(0.5f, Color.PHILOSOPHERS.R/255f, Color.PHILOSOPHERS.G/255f, Color.PHILOSOPHERS.B/255f, 1);
				//MiscHelper.drawVectorWithParticles(position(), position().add(getDeltaMovement()), particle, 0.1, lvl);
				//for(int i = 0; i < 24; ++i) {
				//	this.level.addParticle(particle, this.getX() + velX * (double)i / 4.0D, this.getY() + velY * (double)i / 4.0D, this.getZ() + velZ * (double)i / 4.0D, 0,0,0);//-velX, -velY + 0.2D, -velZ);
				//}
				// TODO: fix clientside jank because doing this every tick is bad juju
				for (ServerPlayer plr : lvl.players()) {
					BlockPos pos = plr.blockPosition();
					if (pos.closerToCenterThan(this.position(), 96d)) {
						AASBNet.toClient(new DrawParticleLinePacket(position().add(getDeltaMovement()), position(), LineParticlePreset.SENTIENT_TRACER), plr);
					}
				}
			}

			double nextX = getX() + velX;
			double nextY = getY() + velY;
			double nextZ = getZ() + velZ;
			//double horizVel = vel.horizontalDistance();
			setYRot((float)(Mth.atan2(velX, velZ) * (180d / (float)Math.PI)));
			setXRot((float)(Mth.atan2(velY, horizVel) * (180d / (float)Math.PI)));
			setXRot(lerpRotation(this.xRotO, this.getXRot()));
			setYRot(lerpRotation(this.yRotO, this.getYRot()));
			float resistanceFactor = 0.99f;
			//float f1 = 0.05F;
			if (this.isInWater()) {
				for(int j = 0; j < 4; ++j) {
					if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "InWaterLoop");
					//float f2 = 0.25F;
					this.level.addParticle(ParticleTypes.BUBBLE, nextX - velX * 0.25, nextY - velY * 0.25, nextZ - velZ * 0.25, velX, velY, velZ);
				}

				resistanceFactor = this.getWaterInertia();
			}
			if (noClip) resistanceFactor = 1;

			this.setDeltaMovement(motion.scale(resistanceFactor));
			if (!this.isNoGravity() && !noClip) {
				Vec3 vec34 = this.getDeltaMovement();
				this.setDeltaMovement(vec34.x, vec34.y - 0.05, vec34.z);
			}

			this.setPos(nextX, nextY, nextZ);
			this.checkInsideBlocks();
		}
	}
	
	/**
	 * identical to Projectile.tick()
	 */
	private void projectileTick() {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "ProjectileTick");
		if (!this.hasBeenShot) {
			this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.blockPosition());
			this.hasBeenShot = true;
		}

		if (!this.leftOwner) {
			this.leftOwner = this.checkLeftOwner();
		}

		entityTick();
	}

	/**
	 * identical to Entity.tick()
	 */
	private void entityTick() {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "EntityTick");
		this.level.getProfiler().push("entityBaseTick");
		this.feetBlockState = null;
		if (this.isPassenger() && this.getVehicle().isRemoved()) {
			this.stopRiding();
		}

		if (this.boardingCooldown > 0) {
			--this.boardingCooldown;
		}

		this.walkDistO = this.walkDist;
		this.xRotO = this.getXRot();
		this.yRotO = this.getYRot();
		this.handleNetherPortal();
		if (this.canSpawnSprintParticle()) {
			this.spawnSprintParticle();
		}

		this.wasInPowderSnow = this.isInPowderSnow;
		this.isInPowderSnow = false;
		this.updateInWaterStateAndDoFluidPushing();
		this.updateFluidOnEyes();
		this.updateSwimming();
		int remainingFireTicks = this.getRemainingFireTicks();
		if (this.level.isClientSide) {
			this.clearFire();
		} else if (remainingFireTicks > 0) {
			if (this.fireImmune()) {
				this.setRemainingFireTicks(remainingFireTicks - 4);
				if (remainingFireTicks < 0) {
					this.clearFire();
				}
			} else {
				if (remainingFireTicks % 20 == 0 && !this.isInLava()) {
					this.hurt(DamageSource.ON_FIRE, 1.0F);
				}

				this.setRemainingFireTicks(remainingFireTicks - 1);
			}

			if (this.getTicksFrozen() > 0) {
				this.setTicksFrozen(0);
				this.level.levelEvent((Player)null, 1009, this.blockPosition(), 1);
			}
		}

		if (this.isInLava()) {
			this.lavaHurt();
			this.fallDistance *= 0.5F;
		}

		this.checkOutOfWorld();
		if (!this.level.isClientSide) {
			this.setSharedFlagOnFire(remainingFireTicks > 0);
		}

		this.firstTick = false;
		this.level.getProfiler().pop();
	}
	
	
	
	
	
	
	
	
	///////////////
	// FUNCTIONS //
	///////////////
	public void becomeInert() {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "BecomingInert");
		this.setState(ArrowState.INERT);
		resetTarget();
	}
	
	@Override
	public void kill() {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "EndOfLife");
		//playSound(EffectInit.ARCHANGELS_EXPIRE.get(), 1, 1);
		if (getOwner() != null) {
			//level.playSound(null, getOwner().blockPosition(), EffectInit.ARCHANGELS_EXPIRE.get(), SoundSource.PLAYERS, 1, 0.1f);
		}
		discard();
	}
	
	private boolean canSee(Entity ent) {
		return (!ent.isInvisible() || ent.isCurrentlyGlowing()) && canSee(ent.getBoundingBox().getCenter());
	}
	private boolean canSee(Vec3 pos) {
		return isUnobstructed(this.getBoundingBox().getCenter(), pos);
		//BlockHitResult hitRes = this.level.clip(new ClipContext(this.getBoundingBox().getCenter(), pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		//if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK && !level.getBlockState(hitRes.getBlockPos()).is(BlockTP.ARROW_NOCLIP)) {
		//	return false;
		//}
		//return true;
	}
	private boolean isUnobstructed(Vec3 start, Vec3 end) {
		BlockHitResult hitRes = this.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK && !level.getBlockState(hitRes.getBlockPos()).is(BlockTP.ARROW_NOCLIP)) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unused")
	private void shootAt(Entity ent) {
		shootAt(ent, 1);
	}
	private void shootAt(Entity ent, float vel) {
		shootAt(ent.getBoundingBox().getCenter(), vel);
	}
	@SuppressWarnings("unused")
	private void shootAt(Vec3 pos) {
		shootAt(pos, 1);
	}
	private void shootAt(Vec3 pos, float vel) {
		Vec3 between = pos.subtract(position());
		if (between.length() < vel) {
			vel = (float) between.length();
		}
		Vec3 heading = between.normalize();
		Vec3 motion = heading.scale(vel);
		shoot(heading.x, heading.y, heading.z, (float) motion.length(), 0);
	}
	@Nullable
	private Path findPathTo(@NotNull Entity ent) {
		return findPathTo(ent.getBoundingBox().getCenter());
	}
	@Nullable
	private Path findPathTo(Vec3 pos) {
		return nav.createPath(pos, 0);
	}
	
	private Path trimNodes(Path path) {
		List<Node> nodes = Lists.newArrayList();
		nodes.add(path.getNode(0));
		for (int i = 1; i < path.getNodeCount() - 1; i++) {
			if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "NodeTrimLoop");
			if (!isUnobstructed(nodes.get(nodes.size() - 1).asVec3(), path.getNode(i+1).asVec3())) {
				nodes.add(path.getNode(i));
			}
		}
		nodes.add(path.getEndNode());
		return new Path(nodes, path.getTarget(), path.canReach());
	}
	
	private boolean isPathInsane(Path path) {
		boolean isInsane = path == null || path.sameAs(currentPath) || path.getNode(0) == path.getEndNode();
		if (!isInsane) {
			for (Path oldPath : previousPaths) {
				if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "PathInsanityLoop");
				if (path.sameAs(oldPath)) {
					isInsane = true;
					break;
				}
			}
		}
		return isInsane;
	}
	private void changeTargetPos(Vec3 newPos, boolean particles) {
		if (particles && level instanceof ServerLevel lvl) {
			for (ServerPlayer plr : lvl.players()) {
				BlockPos pos = plr.blockPosition();
				if (pos.closerToCenterThan(newPos, 64d) || pos.closerToCenterThan(this.position(), 64d)) {
					AASBNet.toClient(new DrawParticleLinePacket(this.getBoundingBox().getCenter(), newPos, LineParticlePreset.ARROW_TARGET_LOCK), plr);
				}
			}
		}
		targetPos = newPos;
	}
	
	/**
	 * @param target
	 * @return if pathfinding was unsuccessfull
	 */
	private void pathTo(Entity ent) {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "PathTo");
		Entity target = ent;
		Path lastMemorized = null;
		while (!hasPath()) {
			if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "FindPathLoop");
			// FIND PATH
			if (currentPath != null && !currentPath.sameAs(lastMemorized)) {
				previousPaths.push(currentPath);
				lastMemorized = currentPath;
			}
			Path newPath = findPathTo(target);
			boolean insane;
			if (newPath == null) {
				insane = true;
			} else {
				newPath = trimNodes(newPath);
				insane = isPathInsane(newPath);
			}
			if (insane) {
				if (isReturningToOwner) {
					isReturningToOwner = false;
					becomeInert();
					return;
				}
				if (!attemptAutoRetarget()) {
					isReturningToOwner = true;
					target = owner();
					continue;
				}
			}
			if (DebugCfg.ARROW_PATHFIND.get()) {
				drawDebugPath(newPath);
			}
			currentPath = newPath;
			break;
		}
		if (!isInert()) {
			// FOLLOW PATH
			Node node = currentPath.getPreviousNode();
			if (node == null) {
				node = currentPath.getNode(0);
			}
			Node nextNode = currentPath.getNextNode();
			Vec3 nextTargetPos = Vec3.atCenterOf(nextNode.asBlockPos());
			if (nextTargetPos != null) {
				if (targetPos == null || !nextTargetPos.closerThan(targetPos, 0.5)) {
					targetPos = nextTargetPos;
					//particles(0);
				} else {
					targetPos = nextTargetPos;
				}
			}
			if (targetPos != null && this.position().closerThan(targetPos, 0.5)) {
				currentPath.advance();
			}
		}
	}
	private void particles(int type) {
		if (level.isClientSide()) return;
		
		switch (type) {
		case 0: // tracer
			for (ServerPlayer plr : ((ServerLevel) level).players()) {
				BlockPos pos = plr.blockPosition();
				if (pos.closerToCenterThan(this.getBoundingBox().getCenter(), 64) || pos.closerToCenterThan(targetPos, 64)) {
					AASBNet.toClient(new DrawParticleLinePacket(this.getBoundingBox().getCenter(), targetPos, LineParticlePreset.SENTIENT_RETARGET), plr);
				}
			}
			break;
			
		case 1: // retarget
			for (ServerPlayer plr : ((ServerLevel) level).players()) {
				Vec3 pos = plr.position();
				Entity target = getTarget();
				if (pos.closerThan(this.getBoundingBox().getCenter(), 128) || pos.closerThan(target.getBoundingBox().getCenter(), 128)) {
					AASBNet.toClient(new DrawParticleLinePacket(this.getBoundingBox().getCenter(), target.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_RETARGET), plr);
				}
			}
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * 
	 * @param blockPos
	 * @return if was successfull
	 */
	private boolean transmuteBlockIntoCovDust(BlockPos blockPos) {
		// TODO: possibly reimplement 
		return false;
		/*
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "BlockToCovDust");
		if (this.level.isClientSide) return false;
		else {
			ServerLevel lvl = (ServerLevel) level;
			BlockState block = level.getBlockState(blockPos);
			if (level.destroyBlock(blockPos, false, owner())) {
				List<ItemStack> drops = Block.getDrops(block, lvl, blockPos, WorldHelper.getBlockEntity(level, blockPos), owner(), new ItemStack(PEItems.RED_MATTER_MORNING_STAR.get()));
				long dropEmc = 0;
				if (!drops.isEmpty()) {
					for (ItemStack drop : drops) {
						if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "DropsEmcLoop");
						if (EMCHelper.doesItemHaveEmc(drop)) {
							dropEmc += EMCHelper.getEmcValue(drop);
						} else {
							dropEmc += 1;
						}
					}
				}
				if (dropEmc <= 0) dropEmc = 1;
				Vec3 pos = Vec3.atCenterOf(blockPos);
				for (Entry<Item, Long> dust : EmcHelper.emcToCovalenceDust(dropEmc).entrySet()) {
					if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "DustCreationLoop");
					int count = dust.getValue().intValue();
					if (count > 0) {
						ItemEntity itemEnt = new ItemEntity(this.level, pos.x, pos.y, pos.z, new ItemStack(dust.getKey(), count));
						itemEnt.setDefaultPickUpDelay();
						level.addFreshEntity(itemEnt);
					}
				}
				return true;
			}
			return false;
		}
		*/
	}
	private void attemptToTransmuteEntity(LivingEntity entity) {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "AttemptTransmuteEntity");
		Player owner = owner();
		int oldInvuln = entity.invulnerableTime;
		entity.invulnerableTime = 0;
		/*if (entity instanceof EntityDoppleganger gaia && owner != null) {
			// gaia refuses to take damage unless its player damage
			gaia.hurt(DamageSource.playerAttack(owner), gaia.getMaxHealth());
		} else*/ if (entity instanceof Player plr && AmuletItem.isBarrierActive(plr) && owner != null) {
			// massive damage to alchshield
			entity.hurt(AASBDmgSrc.transmutation(owner), entity.getMaxHealth() * 3);
		//} else if (entity instanceof EntityPixie pixie) {
		//	// pixies are weird so we just kill them
		//	pixie.setHealth(0);
		} else if (!entity.addEffect(new MobEffectInstance(ObjectInit.MobEffects.TRANSMUTING.get(), 7, 1))) {
			// if we cant do the effect, do a shitload of damage
			entity.hurt(AASBDmgSrc.transmutation(owner), entity.getMaxHealth() / 8);
		}
		entity.invulnerableTime = oldInvuln;
		//entity.playSound(EffectInit.ARCHANGELS_SENTIENT_HIT.get(), 1, 2f);
		
		if (entity.is(getTarget())) {
			resetTarget();
			if (!attemptAutoRetarget()) {
				isReturningToOwner = true;
			}
		}
	}
	
	protected void drawDebugPath(Path path) {
		if (this.level.isClientSide) return;
		Node lastNode = null;
		Node thisNode = null;
		for (int i = 0; i < path.getNodeCount() - 1; i++) {
			if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "DebugPathLoop");
			Node node = path.getNode(i);
			lastNode = thisNode;
			thisNode = node;
			if (lastNode == null) {
				AASBNet.toClient(new DrawParticleLinePacket(this.getBoundingBox().getCenter(), Vec3.atCenterOf(thisNode.asBlockPos()), LineParticlePreset.DEBUG), (ServerPlayer) this.getOwner());
			} else {
				AASBNet.toClient(new DrawParticleLinePacket(Vec3.atCenterOf(lastNode.asBlockPos()), Vec3.atCenterOf(thisNode.asBlockPos()), LineParticlePreset.DEBUG_2), (ServerPlayer) this.getOwner());
			}
		}
	}
	
	
	//////////////////////
	// TARGET SELECTION //
	//////////////////////
	private LivingEntity findTargetNear(Vec3 pos) {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "FindTargetNear");
		if (!level.isClientSide()) {
			List<LivingEntity> validTargets = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pos, 128, 128, 128), SentientArrow.this::isValidHomingTargetForAutomatic);
			if (!validTargets.isEmpty()) {
				validTargets.sort(Comparator.comparing(SentientArrow.this::distanceToSqr, Double::compare));
				LivingEntity chosenTarget = null;
				for (LivingEntity candidate : validTargets) {
					// gets closest entity with line of sight
					if (level.clip(new ClipContext(pos, candidate.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS) {
						chosenTarget = candidate;
						break;
					}
				}
				if (chosenTarget == null) {
					// if there were none with line of sight, just go with closest
					chosenTarget = validTargets.get(0);
				}
				level.playSound(null, this.blockPosition(), EffectInit.Sounds.TARGETLOCK.get(), this.getSoundSource(), 1f, 1);
				level.playSound(null, chosenTarget.blockPosition(), EffectInit.Sounds.TARGETLOCK.get(), this.getSoundSource(), 1f, 0.5f);
				return chosenTarget;
			}
		}
		return null;
	}
	private boolean attemptAutoRetarget() {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "AutoTargetAttempt");
		LivingEntity newTarget = findTargetNear(this.getBoundingBox().getCenter());
		if (newTarget != null && !newTarget.is(getTarget())) {
			setTarget(newTarget.getId());
			setState(ArrowState.DIRECT); // target visible
			searchTime = 0;
			particles(1);
			return true;
		}
		return false;
	}
	public boolean attemptManualRetarget() {
		if (DebugCfg.LOGS.get()) LogHelper.debug("SentientArrow", "ManualTargetAttempt");
		if (isInert()) setState(ArrowState.DIRECT);
		Entity owner = getOwner();
		if (owner == null) return false;
		if (this.inGround) {
			// teleport to owner if we are stuck
			// TODO: COST
			long teleCost = (long) position().distanceTo(owner.position()) * (maxLife - tickCount);
			if (true){//EmcHelper.getAvaliableEmc(owner()) >= teleCost) {
				inGround = false;
				level.playSound(null, this.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, this.getSoundSource(), 1, 2);
				level.playSound(null, owner.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, this.getSoundSource(), 1, 2);
				//EmcHelper.consumeAvaliableEmc(owner(), teleCost);
				this.setPos(owner.position());
			}
		}
		// Entity oldTarget = getTarget();
		Vec3 ray = owner.getLookAngle().scale(128);
		EntityHitResult hitRes = ProjectileUtil.getEntityHitResult(level, owner, owner.getEyePosition(), owner.getEyePosition().add(ray), owner.getBoundingBox().expandTowards(ray).inflate(1.0D), SentientArrow.this::isValidHomingTarget);
		if (hitRes != null && !inGround) {
			BlockHitResult sightCheck = level.clip(new ClipContext(owner.getEyePosition(), hitRes.getEntity().getBoundingBox().getCenter(), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, owner));
			if (sightCheck != null && sightCheck.getType() != HitResult.Type.MISS) return false; // check for blocks in the way
			if (trySwappingTargetTo(hitRes.getEntity())) {
				particles(1);
				return true;
			}
		}
		// not pointing at anything, so recall
		resetTarget();
		isReturningToOwner = true;
		return false;
	}
	private boolean trySwappingTargetTo(Entity newTarget) {
		if (newTarget != null && !newTarget.is(getTarget())) {
			setTarget(newTarget.getId());
			setState(ArrowState.DIRECT);
			searchTime = 0;
			return true;
		}
		return false;
	}
	protected boolean isValidHomingTarget(LivingEntity entity) {
		Player owner = owner();
		return entity != null
				&& owner != null
				&& canTheoreticallyHitEntity(entity)
				&& !entity.getType().is(EntityTP.HOMING_ARROW_BLACKLIST)
				&& (!entity.isInvisible() || entity.isCurrentlyGlowing())
				&& !entity.hasEffect(ObjectInit.MobEffects.TRANSMUTING.get())
				&& !EntityHelper.isTamedByOrTrusts(entity, owner);
	}
	protected boolean isValidHomingTarget(Entity entity) {
		if (entity instanceof LivingEntity ent) {
			return isValidHomingTarget(ent);
		}
		return false;
	}
	protected boolean isValidHomingTargetForAutomatic(LivingEntity entity) {
		return isValidHomingTarget(entity) && !EntityHelper.isTamedOrTrusting(entity);
	}
	
	protected boolean shouldContinueHomingTowards(Entity entity) {
		if (entity instanceof LivingEntity ent) {
			return canHitEntity(entity)
					&& (!ent.isInvisible() || ent.isCurrentlyGlowing())
					&& !ent.hasEffect(ObjectInit.MobEffects.TRANSMUTING.get());
		}
		return false;
	}

	@Override
	protected boolean canHitEntity(Entity ent) {
		// we will never hit our owner
		if (ent.is(owner())) {
			return isReturningToOwner;
		}
		boolean canHit = !EntityHelper.isInvincible(ent)
					&& ( !EntityHelper.isTamedOrTrusting(ent) || ent.is(getTarget()) ); // && !isInert();
		return canHit && super.canHitEntity(ent);
	}

	/**
	 * variant of canHitEntity() used in homing target validation
	 * @param ent
	 * @return
	 */
	protected boolean canTheoreticallyHitEntity(Entity ent) {
		boolean canHit = !ent.is(getOwner()) && !EntityHelper.isInvincible(ent);
		return canHit && super.canHitEntity(ent);
	}
	
	
	
	
	
	
	
	
	
	
	////////////////////////
	// DATA / STATE STUFF //
	////////////////////////
	public ArrowState getState() {
		switch (entityData.get(AI_STATE)) {
		case 0:
			return ArrowState.SEARCHING;
		case 1:
			return ArrowState.DIRECT;
		case 2:
			return ArrowState.PATHING;
		default:
			return ArrowState.INERT;
		}
	}
	private void setState(ArrowState newState) {
		switch (newState) {
		case SEARCHING:
			entityData.set(AI_STATE, (byte)0);
			break;
		case DIRECT:
			entityData.set(AI_STATE, (byte)1);
			break;
		case PATHING:
			entityData.set(AI_STATE, (byte)2);
			break;
		case INERT:
			entityData.set(AI_STATE, (byte)3);
			break;
		}
	}
	public boolean isLookingForTarget() {
		return getState() == ArrowState.SEARCHING;
	}
	public boolean isHoming() {
		return isReturningToOwner || hasTarget();
	}
	public boolean hasTarget() {
		return (getState() == ArrowState.DIRECT || getState() == ArrowState.PATHING) && getTargetId() != -1;
	}
	public boolean isInert() {
		return getState() == ArrowState.INERT;
	}
	
	public Player owner() {
		if (super.getOwner() instanceof Player player) {
			return player;
		}
		return null;
	}
	
	@Nullable
	public Entity getTarget() {
		return level.getEntity(getTargetId());
	}
	public int getTargetId() {
		return entityData.get(TARGET_ID);
	}
	public void setTarget(int targetId) {
		entityData.set(TARGET_ID, targetId);
	}
	private void resetTarget() {
		searchTime = 0;
		setTarget(-1);
		targetPos = null;
		forgetPaths();
	}
	private void forgetPaths() {
		currentPath = null;
		previousPaths.clear();
	}
	private boolean hasPath() {
		return currentPath != null && !currentPath.isDone();
	}

	//////////////
	// SETTINGS //
	//////////////
	@Override
	public boolean ignoreExplosion() {return true;}
	@Override
	public boolean isNoGravity() {return !isInert() || super.isNoGravity();}
	@Override
	protected ItemStack getPickupItem() {return ItemStack.EMPTY;}
	@Override
	public boolean shouldBeSaved() {return false;}
}
