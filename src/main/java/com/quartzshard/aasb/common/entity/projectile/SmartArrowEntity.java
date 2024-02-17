package com.quartzshard.aasb.common.entity.projectile;

import java.util.Comparator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import com.quartzshard.aasb.data.tags.EntityTP;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.net.client.DrawParticleAABBPacket;
import com.quartzshard.aasb.net.client.DrawParticleAABBPacket.AABBParticlePreset;
import com.quartzshard.aasb.net.client.DrawParticleLinePacket;
import com.quartzshard.aasb.net.client.DrawParticleLinePacket.LineParticlePreset;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.MathUtil;

public class SmartArrowEntity extends Arrow {
	/** 0 = searching, 1 = found & currently chasing, 2 = target lost */
	private static final EntityDataAccessor<Byte> AI_STATE = SynchedEntityData.defineId(SmartArrowEntity.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Float> TARGETPOS_X = SynchedEntityData.defineId(SmartArrowEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> TARGETPOS_Y = SynchedEntityData.defineId(SmartArrowEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> TARGETPOS_Z = SynchedEntityData.defineId(SmartArrowEntity.class, EntityDataSerializers.FLOAT);
	
	protected int maxLife;

	public SmartArrowEntity(EntityType<? extends SmartArrowEntity> type, Level level) {
		super(type, level);
	}

	public SmartArrowEntity(Level level, LivingEntity shooter, float damage) {
		super(level, shooter);
		this.setBaseDamage(damage);
		this.pickup = Pickup.CREATIVE_ONLY;
		this.maxLife = 200;
	}

	public SmartArrowEntity(Level level, LivingEntity shooter, float damage, int maxLife) {
		super(level, shooter);
		this.setBaseDamage(damage);
		this.pickup = Pickup.CREATIVE_ONLY;
		this.maxLife = maxLife;
	}

	public SmartArrowEntity(Level level, LivingEntity shooter, float damage, int maxLife, byte aiState) {
		super(level, shooter);
		this.setBaseDamage(damage);
		this.pickup = Pickup.CREATIVE_ONLY;
		changeAiState(aiState);
		this.maxLife = maxLife;
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(AI_STATE, (byte)0);
		entityData.define(TARGETPOS_X, 0.0f);
		entityData.define(TARGETPOS_Y, 0.0f);
		entityData.define(TARGETPOS_Z, 0.0f);
	}
	
	@Override
	protected boolean canHitEntity(Entity ent) {
		// we will never hit our owner
		boolean canHit = !ent.is(getOwner());
		if (canHit && getPierceLevel() > 0) {
			canHit = !EntUtil.isInvincible(ent);
		}
		return canHit && super.canHitEntity(ent);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult hitRes) {
		if (!canHitEntity(hitRes.getEntity())) {
			return;
		}
		// ignore invincibility frames
		int iFrames = hitRes.getEntity().invulnerableTime;
		hitRes.getEntity().invulnerableTime = 0;
		super.onHitEntity(hitRes);
		hitRes.getEntity().invulnerableTime = iFrames;
	}
	
	@Override
	protected void onHitBlock(BlockHitResult hitRes) {
		becomeInert();
		super.onHitBlock(hitRes);
	}
	
	/**
	 * checks if the arrow has gone past its target location <br>
	 * if so, it stops homing so it doesnt get stuck midair
	 */
	protected void checkOvershotTarget() {
		if (level().isClientSide()) return;
		if (hasTarget()) {
			double currentDistance = position().distanceToSqr(getTargetPos());
			double projectedDistance = position().add(getDeltaMovement()).distanceToSqr(getTargetPos());
			if (currentDistance < projectedDistance) {
				becomeInert();
			}
		}
	}

	@Override
	public void tick() {
		// updates 
		//if (!level.isClientSide() && isNoGravity() && tickCount % 3 == 0) {
		//	this.hasImpulse = true;
		//}
		if ((inGround && shakeTime <= 0) || tickCount > maxLife) {
			expire();
		} else if (!inGround && tickCount > 4) {
			// try a few times to find target
			if (canChangeTarget()) {
				if (tickCount <= 9) {
					findNewTarget();
					setDeltaMovement(getDeltaMovement().scale(0.35));
				}
				else becomeInert();
			}
			if (hasTarget()) {
				seekTarget();
				checkOvershotTarget();
			}
		}
		super.tick();
	}

	//protected void superTick() {
	//	super.tick();
	//}

	@NotNull
	@Override
	protected ItemStack getPickupItem() {
		return ItemStack.EMPTY;
	}
	
	protected boolean isValidHomingTarget(LivingEntity entity) {
		return canHitEntity(entity)
				&& !entity.is(getOwner())
				&& !entity.getType().is(EntityTP.HOMING_LIST)
				&& !entity.isInvulnerable()
				&& (!entity.isInvisible() || entity.isCurrentlyGlowing())
				&& (level().clip(new ClipContext(this.getBoundingBox().getCenter(), entity.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS)
				;
	}

	protected void findNewTarget() {
		if (level().isClientSide()) return;
		
		List<LivingEntity> validTargets = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(64), SmartArrowEntity.this::isValidHomingTarget);
		if (!validTargets.isEmpty()) {
			validTargets.sort(Comparator.comparing(SmartArrowEntity.this::distanceToSqr, Double::compare));
			LivingEntity target = validTargets.get(0);
			Vec3 targetPos = target.getBoundingBox().getCenter();
			for (ServerPlayer plr : ((ServerLevel)level()).players()) {
				if (plr.blockPosition().closerToCenterThan(this.position(), 64d)) {
					NetInit.toClient(new DrawParticleLinePacket(this.position(), targetPos, LineParticlePreset.ARROW_TARGET_LOCK), plr);
				}
			}
			level().playSound(null, this.blockPosition(), FxInit.SND_TARGETLOCK.get(), this.getSoundSource(), 1f, 1);
			level().playSound(null, BlockPos.containing(targetPos), FxInit.SND_TARGETLOCK.get(), this.getSoundSource(), 1f, 0.5f);
			changeTarget(targetPos);
			changeAiState((byte) 1);
		}
	}

	protected Vec3 getTargetPos() {
		return new Vec3(entityData.get(TARGETPOS_X), entityData.get(TARGETPOS_Y), entityData.get(TARGETPOS_Z));
	}
	
	protected byte getAiState() {
		return entityData.get(AI_STATE);
	}

	protected boolean hasTarget() {
		return getAiState() == 1;
	}

	protected boolean canChangeTarget() {
		return getAiState() == 0;
	}
	
	protected void changeAiState(byte newState) {
		entityData.set(AI_STATE, newState);
	}
	
	protected void changeTarget(Vec3 tPos) {
		entityData.set(TARGETPOS_X, (float)tPos.x);
		entityData.set(TARGETPOS_Y, (float)tPos.y);
		entityData.set(TARGETPOS_Z, (float)tPos.z);
	}
	
	protected void changeTarget(Entity tEnt) {
		changeTarget(tEnt.getBoundingBox().getCenter());
	}
	
	protected void seekTarget() {
		if (!level().isClientSide()) {
			((ServerLevel)level()).sendParticles(ParticleTypes.ELECTRIC_SPARK, position().x, position().y, position().z, 3, 0.1, 0.1, 0.1, 0);
		}
		Vec3 vel = getDeltaMovement();
		Vec3 arrowLoc = position();
		Vec3 targetLoc = getTargetPos();

		// Get the vector that points straight from the arrow to the target
		Vec3 lookVec = targetLoc.subtract(arrowLoc);

		// Find the angle between the direct vec and arrow vec, and then clamp it so it arcs a bit
		double theta = MathUtil.angleBetween(vel, lookVec);
		theta = MathUtil.wrap180Radian(theta);
		//theta = CalcHelper.clampAbs(theta, Math.PI / 2); // Dividing by higher numbers kills accuracy

		// Find the cross product to determine the axis of rotation
		Vec3 crossProduct = vel.cross(lookVec).normalize();

		// Create the rotation using the axis and our angle and adjust the vector to it
		Vec3 adjustedLookVec = MathUtil.transform(crossProduct, theta, vel);

		// Tell mc to adjust our rotation accordingly
		shoot(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 5.0F, 0);
		// force a network sync packet so everything looks proper clientside
		this.hasImpulse = true;
	}
	
	/** makes the smart arrow stop being smart */
	public void becomeInert() {
		if (level().isClientSide() || getAiState() == 2) return;
		changeAiState((byte) 2);
		for (ServerPlayer plr : ((ServerLevel)level()).players()) {
			if (plr.blockPosition().closerToCenterThan(this.position(), 64d)) {
				Vec3 min = new Vec3(getBoundingBox().minX, getBoundingBox().minY, getBoundingBox().minZ),
						max = new Vec3(getBoundingBox().maxX, getBoundingBox().maxY, getBoundingBox().maxZ);
				NetInit.toClient(new DrawParticleAABBPacket(min, max, AABBParticlePreset.SENTIENT_ARROW_TARGET_LOST), plr);
			}
		}
	}
	
	public void expire() {
		//playSound(EffectInit.ARCHANGELS_EXPIRE.get(), 1, 1);
		discard();
	}
	
	@Override
	public boolean isNoGravity() {
		return getAiState() < 2 || super.isNoGravity();
	}

	@Override
	public boolean ignoreExplosion() {
		return getAiState() < 2;
	}
}