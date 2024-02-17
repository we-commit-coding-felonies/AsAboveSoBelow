package com.quartzshard.aasb.common.entity.projectile;

import java.util.Random;
import java.util.Stack;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.data.tags.BlockTP;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.net.client.PresetParticlePacket;
import com.quartzshard.aasb.net.client.PresetParticlePacket.ParticlePreset;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.fluids.FluidStack;

/**
 * flies straight, creates a fiery explosion when expiring <br>
 * has a fixed max lifetime
 * can be fired in air or underwater, but cannot go between them
 */
public class MustangEntity extends Projectile {
	public MustangEntity(EntityType<? extends MustangEntity> type, Level level) {
		super(type, level);
	}
	private MustangEntity(Level level, double x, double y, double z, Vec3 vel) {
		super(EntityInit.ENT_MUSTANG.get(), level);
		if (level.getFluidState(new BlockPos((int)x,(int)y,(int)z)).is(FluidTags.WATER))
			entityData.set(WATER, true);
		setPos(x,y,z);
		setDeltaMovement(vel);
	}
	public MustangEntity(Level level, Vec3 pos, Vec3 vel) {
		this(level, pos.x,pos.y,pos.z, vel);
	}
	public MustangEntity(Level level, Entity shooter) {
		this(level, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ(), shooter.getLookAngle().normalize());
		setOwner(shooter);
		setRot(shooter.getYRot() + 180, -shooter.getXRot());
		float f = 0.4F;
		double mx = Mth.sin(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(this.getXRot() / 180.0F * (float) Math.PI) * f / 2D;
		double mz = -(Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(this.getXRot() / 180.0F * (float) Math.PI) * f) / 2D;
		double my = Mth.sin(this.getXRot() / 180.0F * (float) Math.PI) * f / 2D;
		setDeltaMovement(new Vec3(mx, my, mz));
	}
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(MustangEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> WATER = SynchedEntityData.defineId(MustangEntity.class, EntityDataSerializers.BOOLEAN);

	@Override
	protected void defineSynchedData() {
		entityData.define(COLOR, 0xff4400);
		entityData.define(WATER, false);
	}

	
	@Override
	public void tick() {
		if (this.tickCount > 12) {
			expire();
			return;
		}
		FluidState liquid = level().getFluidState(blockPosition());
		if ( (!liquid.is(Fluids.WATER) && isWater())
			|| (!liquid.is(Fluids.EMPTY) && !isWater()) ) {
			expire();
		}
		// Useful variables
		Vec3 curVel = getDeltaMovement(),
			nextVel = curVel;
		Vec3 curPos = position(),
			oldPos = position().subtract(curVel),
			nextPos = curPos;
		
		// Logic
		AABB assistBox = AABB.ofSize(position(), 4, 4, 4);
		for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, assistBox, this::canHitEntity)) {
			if (living.hurtTime == 0) {
				expire();
				return;
			}
		}
		
		// Finalize movement & check collision
		nextPos = curPos.add(nextVel);
		boolean updateVel = !nextVel.equals(curVel);
		if (updateVel)
			updateRotation();
		HitResult hitRes = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		if (hitRes != null && hitRes.getType() != HitResult.Type.MISS) {
			expire();
			return;
		}
		liquid = level().getFluidState(BlockPos.containing(nextPos));
		if ( (!liquid.is(Fluids.WATER) && isWater())
			|| (!liquid.is(Fluids.EMPTY) && !isWater()) ) {
			expire();
		}
		if (updateVel)
			setDeltaMovement(nextVel);
		setPos(curPos.add(nextVel));
		super.tick();
	}

	@Override
	protected void updateRotation() {
		Vec3 vel = this.getDeltaMovement();
		double horizVel = vel.horizontalDistance();
		this.setXRot( (float)(Mth.atan2(vel.y, horizVel) * (180d / Math.PI)) );
		this.setYRot( (float)(Mth.atan2(vel.x, vel.z) * (180d / Math.PI)) );
	}
	
	@Override
	protected boolean canHitEntity(Entity entity) {
		if (entity instanceof LivingEntity ent && !EntUtil.isInvincible(ent)) {
			if (ent.is(getOwner()) || ent instanceof Player plrVictim && getOwner() instanceof Player plrShooter && !plrShooter.canHarmPlayer(plrVictim)) {
				return false;
			}
			return !ent.ignoreExplosion() && !(ent instanceof Blaze) && super.canHitEntity(ent);
		}
		return false;
	}
	
	@Override
	protected void onHitEntity(EntityHitResult hitRes) {
		expire();
	}

	@Override
	protected void onHitBlock(BlockHitResult hitRes) {
		expire();
	}
	
	public int getColor() {
		return entityData.get(COLOR);
	}
	
	public boolean isWater() {
		return entityData.get(WATER);
	}
	
	private void expire() {
		if (level() instanceof ServerLevel lvl) {
			AABB burnArea = AABB.ofSize( Vec3.atCenterOf(blockPosition()), 6, 6, 6 );
			superCoolHugeFireExplosionOfUnlimitedCarnage(lvl, getOwner(), burnArea);
		}
		discard();
	}
	
	/**
	 * as advertised <br>
	 * everything is centered on the AABB
	 * 
	 * @param level the SERVER world to explode in
	 * @param culprit the entity that caused this kaboom
	 * @param box the AABB to deal stupid amounts of damage in (to entities)
	 */
	public void superCoolHugeFireExplosionOfUnlimitedCarnage(ServerLevel level, Entity culprit, AABB box) {
		RandomSource rand = level.getRandom();
		Vec3 cent = box.getCenter();
		BlockPos bCent = BlockPos.containing(cent);
		
		////////////////////
		// DAMAGE & WORLD //
		////////////////////
		
		// hurting entities
		// based on vanilla explosion code
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, box)) {
			if (!(ent instanceof Blaze)) {
				double distance = Math.sqrt(ent.distanceToSqr(cent)) / 8d;
				if (distance <= 1) {
					double xDiff = ent.getX() - cent.x();
					double yDiff = ent.getY() - cent.y();
					double zDiff = ent.getZ() - cent.z();
					double sqrtXYZ = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
					if (sqrtXYZ != 0.0D) {
						xDiff /= sqrtXYZ;
						yDiff /= sqrtXYZ;
						zDiff /= sqrtXYZ;
						double seenPercent = Explosion.getSeenPercent(cent, ent);
						double invDist = (1d - distance) * seenPercent;
						ent.setRemainingFireTicks(1200);
						DamageSource dmgSrc = EntityInit.dmg(EntityInit.DMG_MUSTANG, this.level(), this, this.getOwner());
						// TODO change the mustang damage calculation to something easier to understand, and maybe less overpowered
						float dmg = (float) Math.pow( ((int)((invDist*invDist+invDist)/2d*7d*8d+1d)), 2 );
						if (ent.fireImmune() || ent.hasEffect(MobEffects.FIRE_RESISTANCE)) dmg /= 10;
						ent.hurt(dmgSrc, dmg);
					}
				}
			}
		}
		
		// 2nd pass for items. should catch stuff like mob drops
		for (ItemEntity ent : level.getEntitiesOfClass(ItemEntity.class, box)) {
			if (!ent.ignoreExplosion() && !ent.fireImmune()) {
				double distance = Math.sqrt(ent.distanceToSqr(cent)) / 8d;
				if (distance <= 1.0D) {
					double xDiff = ent.getX() - cent.x();
					double yDiff = ent.getY() - cent.y();
					double zDiff = ent.getZ() - cent.z();
					double sqrtXYZ = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
					if (sqrtXYZ != 0.0D) {
						xDiff /= sqrtXYZ;
						yDiff /= sqrtXYZ;
						zDiff /= sqrtXYZ;
						DamageSource dmgSrc = EntityInit.dmg(EntityInit.DMG_MUSTANG, this.level(), this, this.getOwner());
						ent.hurt(dmgSrc, Float.MAX_VALUE);
					}
				}
			}
		}
		
		// screwing with blocks
		// we keep track of things in these stacks to do particles later
		Stack<BlockPos>
			vaporized = new Stack<>(), // water that gets instantly turned to steam
			incinerated = new Stack<>(); // blocks that get instantly turned to ash
		BlockPos.betweenClosedStream(box).forEach(bPos -> {
			
			// debug: marks all for paticles
			//if (DebugCfg.HITBOX_SERVER.get()) vaporized.push(bPos);
			
			// glass the desert :3
			if (level.getBlockState(bPos).is(BlockTags.SMELTS_TO_GLASS)) {
				if (culprit instanceof ServerPlayer plr) {
					PlayerUtil.checkedPlaceBlock(plr, bPos, Blocks.BROWN_STAINED_GLASS.defaultBlockState());
				} else level.setBlockAndUpdate(bPos, Blocks.BROWN_STAINED_GLASS.defaultBlockState());
			}
			
			// vaporize waters
			else if (level.getFluidState(bPos) != Fluids.EMPTY.defaultFluidState()) {
				FluidState fState = level.getFluidState(bPos);
				
				// check if this fluid vaporizes when too hot (like in nether)
				if (fState.is(FluidTags.WATER)) {//.getAttributes().doesVaporize(level, bPos, new FluidStack(fState.getType(), 1000))) {
					BlockState bState = level.getBlockState(bPos);
					
					// just vaporize
					if (bState.is(BlockTP.MUSTANG_VAPORIZES)) {
						if (culprit instanceof ServerPlayer plr && PlayerUtil.checkedPlaceBlock(plr, bPos, Blocks.AIR.defaultBlockState())) {
							vaporized.push(bPos.immutable());
						} else if (level.setBlockAndUpdate(bPos, Blocks.AIR.defaultBlockState())) {
							vaporized.push(bPos.immutable());
						}
					}
					
					// waterlogged blocks
					else if (bState.getBlock() instanceof BucketPickup block) {
						if (culprit instanceof ServerPlayer plr && PlayerUtil.hasEditPermission(plr, bPos) && !block.pickupBlock(level, bPos, bState).isEmpty()) {
							vaporized.push(bPos.immutable());
						} else if (!block.pickupBlock(level, bPos, bState).isEmpty()) {
							vaporized.push(bPos.immutable());
						}
					}
				}
			}
			
			// snow melts / vaporizes
			else if (level.getBlockState(bPos).is(BlockTP.MUSTANG_VAPORIZES) && level.getBlockState(bPos).is(BlockTP.MUSTANG_MELTS)) {
				if (rand.nextInt(3) == 0 && !level.getBlockState(bPos).is(Blocks.SNOW)) {
					if (culprit instanceof ServerPlayer plr) {
						PlayerUtil.checkedPlaceBlock(plr, bPos, Blocks.WATER.defaultBlockState());
					} else level.setBlockAndUpdate(bPos, Blocks.WATER.defaultBlockState());
				} else {
					if (culprit instanceof ServerPlayer plr && PlayerUtil.checkedPlaceBlock(plr, bPos, Blocks.AIR.defaultBlockState())) {
						vaporized.push(bPos.immutable());
					} else if (level.setBlockAndUpdate(bPos, Blocks.AIR.defaultBlockState())) {
						vaporized.push(bPos.immutable());
					}
				}
			}
			
			// ice melts
			else if (level.getBlockState(bPos).is(BlockTP.MUSTANG_MELTS)) {
				if (rand.nextInt(3) != 0) return;
				if (culprit instanceof ServerPlayer plr) {
					PlayerUtil.checkedPlaceBlock(plr, bPos, Blocks.WATER.defaultBlockState());
				} else level.setBlockAndUpdate(bPos, Blocks.WATER.defaultBlockState());
			}
			
			// Burn!!!
			else if (level.getBlockState(bPos).is(BlockTP.MUSTANG_INCINERATES)) {
				if (rand.nextInt(5) == 0) return;
				boolean drop = false;
				if (culprit instanceof ServerPlayer plr && PlayerUtil.checkedPlaceBlock(plr, bPos, Blocks.AIR.defaultBlockState())) {
					incinerated.push(bPos.immutable());
					drop = true;
				} else if (level.setBlockAndUpdate(bPos, Blocks.AIR.defaultBlockState())) {
					incinerated.push(bPos.immutable());
					drop = true;
				}
				if (drop) {
					ItemEntity ash = new ItemEntity(level, bPos.getX(), bPos.getY(), bPos.getZ(), new ItemStack(ItemInit.ASH.get()));
					double v = 0.4;
					ash.setDeltaMovement(AASB.RNG.nextDouble(-v, v), AASB.RNG.nextDouble(0.25*-v, 1.5*v), AASB.RNG.nextDouble(-v, v));
					level.addFreshEntity(ash);
				}
			}
			
			// LAVAAAA
			else if (level.getBlockState(bPos).is(BlockTP.MUSTANG_LIQUEFIES)) {
				if (rand.nextInt(10) != 0) return;
				if (culprit instanceof ServerPlayer plr) {
					PlayerUtil.checkedPlaceBlock(plr, bPos, Blocks.LAVA.defaultBlockState());
				} else level.setBlockAndUpdate(bPos, Blocks.LAVA.defaultBlockState());
			}
		});
		
		BlockPos.betweenClosedStream(box).forEach(bPos -> {
			// we do fire after the other stuff is done so the other stuff doesnt get in the way of ALL CONSUMING FLAMES!!!!
			if (level.isEmptyBlock(bPos)) {
				if (rand.nextInt(4) != 0) return;
				if (culprit instanceof ServerPlayer player) {
					PlayerUtil.checkedPlaceBlock(player, bPos, Blocks.FIRE.defaultBlockState());
				} else {
					level.setBlockAndUpdate(bPos, Blocks.FIRE.defaultBlockState());
				}
			}
		});
		
		
		///////////////////////
		// PARTICLES & SOUND //
		///////////////////////

		// big fwoosh of fire!
		level.playSound(null, bCent, FxInit.SND_MUSTANG.get(), SoundSource.NEUTRAL, 3f, 1f);
		/*if (DebugCfg.HITBOX_SERVER.get()) {
			
			//// drawing the hitbox
			for (ServerPlayer plr : level.players()) {
				if (plr.blockPosition().closerToCenterThan(cent, 512d)) {
					Vec3 minCorner = new Vec3(box.minX, box.minY, box.minZ);
					Vec3 maxCorner = new Vec3(box.maxX, box.maxY, box.maxZ);
					AASBNet.toClient(new DrawParticleAABBPacket(minCorner, maxCorner, AABBParticlePreset.DEBUG), plr);
				}
			}
			
			// center and blockcenter
			level.sendParticles(ParticleTypes.DRIPPING_HONEY, cent.x(), cent.y(), cent.z(), 1, 0, 0, 0, 0);
			level.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, bCent.getX(), bCent.getY(), bCent.getZ(), 1, 0, 0, 0, 0);
			
			// marking every individual block
			while (!vaporized.empty()) {
				Vec3 pos = Vec3.atCenterOf(vaporized.pop());
				level.sendParticles(ParticleTypes.DRIPPING_WATER, pos.x(), pos.y(), pos.z(), 1, 0, 0, 0, 0);
			}
		}*/
		
		// badass does immolation
		NetInit.toNearbyClients(new PresetParticlePacket(ParticlePreset.MUSTANG, new Vec3(cent.x, cent.y, cent.z)), level, cent, 256);
		
		// steam from the steamed clams were having
		while (!vaporized.empty() /*&& !DebugCfg.HITBOX_SERVER.get()*/) {
			BlockPos bPos = vaporized.pop();
			level.playSound(null, bPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);
			level.sendParticles(ParticleTypes.CLOUD, bPos.getX(), bPos.getY(), bPos.getZ(), 4, 0, 0, 0, 0.3);
		}
		while (!incinerated.empty() /*&& !DebugCfg.HITBOX_SERVER.get()*/) {
			BlockPos bPos = incinerated.pop();
			level.playSound(null, bPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.1F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);
			level.sendParticles(ParticleTypes.ASH, bPos.getX(), bPos.getY(), bPos.getZ(), 15, 1, 1, 1, 0);
		}
	}
}
