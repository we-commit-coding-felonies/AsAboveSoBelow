package com.quartzshard.aasb.util;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.DistExecutor;

/**
 * helper code for clientside stuff
 * <p>
 * DO NOT CALL THESE FUNCTIONS SERVERSIDE
 * @author solunareclipse1
 */
public class ClientUtil {
	
	public static Minecraft mc() {
		return Minecraft.getInstance();
	}
	
	public static boolean shiftHeld() {
		return InputConstants.isKeyDown(mc().getWindow().getWindow(), mc().options.keyShift.getKey().getValue());
	}
	
	/**
	 * from projecte gem boots <br>
	 * always returns false when freecam is active
	 * @return
	 */
	public static boolean isJumpPressed() {
		return DistExecutor.unsafeRunForDist(() -> () -> {
			return !AstralProjection.isEnabled() && mc().options.keyJump.isDown();
		}, () -> () -> false);
	}
	
	/**
	 * from projecte gem boots
	 * @return
	 */
	public static boolean isJumpPressedIgnoreFreecam() {
		return DistExecutor.unsafeRunForDist(() -> () -> mc().options.keyJump.isDown(), () -> () -> false);
	}
	
	/**
	 * DO NOT CALL THIS SERVERSIDE!!!!!
	 * @return clients sound manager
	 */
	public static SoundManager getSoundManager() {
		return mc().getSoundManager();
	}
	
	public static ClientLevel level() {
		return mc().level;
	}
	
	/**
	 * only use this if you have to <br>
	 * functions similarly to level.addParticle, but it returns the particle it created
	 * @return
	 */
	@Nullable
	public static Particle hackyParticle(ParticleOptions particle, boolean alwaysRender, boolean decreased, double x, double y, double z, double vx, double vy, double vz) {
		Minecraft mc = mc();
		ClientLevel level = mc.level;
		ParticleEngine engine = mc.particleEngine;
		level.addParticle(particle, x, y, z, x, y, z);
		//LevelRenderer lr = mc.levelRenderer;
		try {
			Camera camera = mc.gameRenderer.getMainCamera();
			if (camera.isInitialized() && engine != null) {
				@NotNull ParticleStatus status = calculateParticleLevel(level, decreased);
				if (alwaysRender) {
					return engine.createParticle(particle, x, y, z, vx, vy, vz);
				} else if (camera.getPosition().distanceToSqr(x, y, z) > 1024) {
					return null;
				} else {
					return status == ParticleStatus.MINIMAL ? null : engine.createParticle(particle, x, y, z, vx, vy, vz);
				}
			}
			return null;
		} catch (Throwable crash) {
			CrashReport report = CrashReport.forThrowable(crash, "Exception while adding particle");
			CrashReportCategory category = report.addCategory("Particle being added");
			category.setDetail("ID", particle.getType().toString()); // BuiltInRegistries.PARTICLE_TYPE.getKey(pOptions.getType())
			category.setDetail("Parameters", particle.writeToString());
			category.setDetail("Position", () -> {
				return CrashReportCategory.formatLocation(level, x, y, z);
			});
			throw new ReportedException(report);
		}
	}
	

	private static ParticleStatus calculateParticleLevel(ClientLevel level, boolean pDecreased) {
		ParticleStatus particleSetting = mc().options.particles().get();
		if (pDecreased && particleSetting == ParticleStatus.MINIMAL && level.random.nextInt(10) == 0) {
			particleSetting = ParticleStatus.DECREASED;
		}

		if (particleSetting == ParticleStatus.DECREASED && level.random.nextInt(3) == 0) {
			particleSetting = ParticleStatus.MINIMAL;
		}

		return particleSetting;
	}
	
	

	// stolen from freecam mod. used for astral projection
	public static class AstralProjection {
		private static boolean ENABLED = false;
		private static @Nullable FreeCamera FREECAM;
		private static @Nullable CameraType CAMERA_MEMORY;
		
		public static boolean isEnabled() {
			return ENABLED;
		}
		
		@Nullable
		public static FreeCamera getCamera() {
			return FREECAM;
		}
		
		public static void toggle() {
			if (ENABLED) disable();
			else enable();
			ENABLED = !ENABLED;
			if (!ENABLED && CAMERA_MEMORY != null)
				mc().options.setCameraType(CAMERA_MEMORY);
			
		}
		
		private static void enable() {
			mc().smartCull = false;
			mc().gameRenderer.setRenderHand(false);

			CAMERA_MEMORY = mc().options.getCameraType();
			if (mc().gameRenderer.getMainCamera().isDetached()) {
				mc().options.setCameraType(CameraType.FIRST_PERSON);
			}
			FREECAM = new FreeCamera(-6304);
			FREECAM.applyPerspective(true);
			FREECAM.spawn();
			mc().setCameraEntity(FREECAM);
		}
		
		private static void disable() {
			mc().smartCull = true;
			mc().gameRenderer.setRenderHand(true);
			mc().setCameraEntity(mc().player);
			FREECAM.despawn();
			FREECAM.input = new Input();
			FREECAM = null;

			if (mc().player != null) {
				mc().player.input = new KeyboardInput(mc().options);
			}
		}
		
		// https://github.com/MinecraftFreecam/Freecam/blob/b2f6429c37d82f8d6ff47efda3f08048402cb34e/src/main/java/net/xolt/freecam/util/FreeCamera.java#L22
		public static class FreeCamera extends LocalPlayer {
		
			private static final ClientPacketListener NETWORK_HANDLER = new ClientPacketListener(
					mc(),
					mc().screen,
					mc().getConnection().getConnection(),
					mc().getCurrentServer(),
					new GameProfile(UUID.randomUUID(), "AstralProjection"),
					mc().getTelemetryManager().createWorldSessionManager(false, null, null)) {
				@Override
				public void send(Packet<?> packet) {}
			};
		
			public FreeCamera(int id) {
				this(id, FreecamPosition.getSwimmingPosition(mc().player));
			}
		
			public FreeCamera(int id, FreecamPosition position) {
				super(mc(), mc().level, NETWORK_HANDLER, mc().player.getStats(), mc().player.getRecipeBook(), false, false);
		
				setId(id);
				applyPosition(position);
				getAbilities().flying = true;
				input = new KeyboardInput(mc().options);
			}
		
			public void applyPosition(@NotNull FreecamPosition position) {
				super.setPose(position.pose);
				moveTo(position.x, position.y, position.z, position.yaw, position.pitch);
				xBob = getXRot();
				yBob = getYRot();
				xBobO = xBob; // Prevents camera from rotating upon entering freecam.
				yBobO = yBob;
			}
		
			// Mutate the position and rotation based on perspective
			// If checkCollision is true, move as far as possible without colliding
			public void applyPerspective(/*ModConfig.Perspective perspective,*/ boolean checkCollision) {
				FreecamPosition position = new FreecamPosition(this);
		
				//switch (perspective) {
				//	case INSIDE:
				//		// No-op
				//		break;
				//	case FIRST_PERSON:
				//		// Move just in front of the player's eyes
				//		moveForwardUntilCollision(position, 0.4, checkCollision);
				//		break;
				//	case THIRD_PERSON_MIRROR:
				//		// Invert the rotation and fallthrough into the THIRD_PERSON case
				//		position.mirrorRotation();
				//	case THIRD_PERSON:
				//		// Move back as per F5 mode
				//		moveForwardUntilCollision(position, -4.0, checkCollision);
				//		break;
				//}
			}
		
			// Move FreeCamera forward using FreecamPosition.moveForward.
			// If checkCollision is true, stop moving forward before hitting a collision.
			// Return true if successfully able to move.
			private boolean moveForwardUntilCollision(FreecamPosition position, double distance, boolean checkCollision) {
				if (!checkCollision) {
					position.moveForward(distance);
					applyPosition(position);
					return true;
				}
				return moveForwardUntilCollision(position, distance);
			}
		
			// Same as above, but always check collision.
			private boolean moveForwardUntilCollision(FreecamPosition position, double maxDistance) {
				boolean negative = maxDistance < 0;
				maxDistance = negative ? -1 * maxDistance : maxDistance;
				double increment = 0.1;
		
				// Move forward by increment until we reach maxDistance or hit a collision
				for (double distance = 0.0; distance < maxDistance; distance += increment) {
					FreecamPosition oldPosition = new FreecamPosition(this);
		
					position.moveForward(negative ? -1 * increment : increment);
					applyPosition(position);
		
					if (!canEnterPose(getPose())) {
						// Revert to last non-colliding position and return whether we were unable to move at all
						applyPosition(oldPosition);
						return distance > 0;
					}
				}
		
				return true;
			}
		
			public void spawn() {
				if (clientLevel != null) {
					clientLevel.putNonPlayerEntity(getId(), this);
				}
			}
		
			public void despawn() {
				if (clientLevel != null && clientLevel.getEntity(getId()) != null) {
					clientLevel.removeEntity(getId(), RemovalReason.DISCARDED);
				}
			}
		
			// Prevents fall damage sound when FreeCamera touches ground with noClip disabled.
			@Override
			protected void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
			}
		
			// Needed for hand swings to be shown in freecam since the player is replaced by FreeCamera in HeldItemRenderer.renderItem()
			@Override
			public float getAttackAnim(float tickDelta) {
				return mc().player.getAttackAnim(tickDelta);
			}
		
			// Needed for item use animations to be shown in freecam since the player is replaced by FreeCamera in HeldItemRenderer.renderItem()
			@Override
			public int getUseItemRemainingTicks() {
				return mc().player.getUseItemRemainingTicks();
			}
		
			// Also needed for item use animations to be shown in freecam.
			@Override
			public boolean isUsingItem() {
				return mc().player.isUsingItem();
			}
		
			// Prevents slow down from ladders/vines.
			@Override
			public boolean onClimbable() {
				return false;
			}
		
			// Prevents slow down from water.
			@Override
			public boolean isInWater() {
				return false;
			}
		
			// Makes night vision apply to FreeCamera when Iris is enabled.
			@Override @Nullable
			public MobEffectInstance getEffect(MobEffect effect) {
				return mc().player.getEffect(effect);
			}
		
			// Prevents pistons from moving FreeCamera when collision.ignoreAll is enabled.
			@Override
			public @NotNull PushReaction getPistonPushReaction() {
				return PushReaction.IGNORE;
			}
		
			// Prevents collision with solid entities (shulkers, boats)
			@Override
			public boolean canCollideWith(Entity other) {
				return false;
			}
		
			// Ensures that the FreeCamera is always in the swimming pose.
			@Override
			public void setPose(Pose pose) {
				super.setPose(Pose.SWIMMING);
			}
		
			// Prevents slow down due to being in swimming pose. (Fixes being unable to sprint)
			@Override
			public boolean isMovingSlowly() {
				return false;
			}
		
			// Prevents water submersion sounds from playing.
			@Override
			protected boolean updateIsUnderwater() {
				this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
				return this.wasUnderwater;
			}
		
			// Prevents water submersion sounds from playing.
			@Override
			protected void doWaterSplashEffect() {}
		
			@Override
			public void aiStep() {
				//if (ModConfig.INSTANCE.movement.flightMode.equals(ModConfig.FlightMode.DEFAULT)) {
					getAbilities().setFlyingSpeed(0);
					//Motion.doMotion(this, ModConfig.INSTANCE.movement.horizontalSpeed, ModConfig.INSTANCE.movement.verticalSpeed);
					Motion.doMotion(this, 1, 1);
				//} else {
				//	getAbilities().setFlySpeed((float) ModConfig.INSTANCE.movement.verticalSpeed / 10);
				//}
				super.aiStep();
				getAbilities().flying = true;
				setOnGround(false);
			}
		}
	

		public static class FreecamPosition {
			public double x;
			public double y;
			public double z;
			public float pitch;
			public float yaw;
			public Pose pose;
		
			private final Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
			private final Vector3f verticalPlane = new Vector3f(0.0F, 1.0F, 0.0F);
			private final Vector3f diagonalPlane = new Vector3f(1.0F, 0.0F, 0.0F);
			private final Vector3f horizontalPlane = new Vector3f(0.0F, 0.0F, 1.0F);
		
			public FreecamPosition(Entity entity) {
				x = entity.getX();
				y = entity.getY();
				z = entity.getZ();
				pose = entity.getPose();
				setRotation(entity.getYRot(), entity.getXRot());
			}
		
			// From net.minecraft.client.render.Camera.setRotation
			public void setRotation(float yaw, float pitch) {
				this.pitch = pitch;
				this.yaw = yaw;
				rotation.rotationYXZ(-yaw * ((float) Math.PI / 180), pitch * ((float) Math.PI / 180), 0.0f);
				horizontalPlane.set(0.0f, 0.0f, 1.0f).rotate(rotation);
				verticalPlane.set(0.0f, 1.0f, 0.0f).rotate(rotation);
				diagonalPlane.set(1.0f, 0.0f, 0.0f).rotate(rotation);
			}
		
			// Invert the rotation so that it is mirrored
			// As-per net.minecraft.client.render.Camera.update
			public void mirrorRotation() {
				setRotation(yaw + 180.0F, -pitch);
			}
		
			// Move forward/backward relative to the current rotation
			public void moveForward(double distance) {
				move(distance, 0, 0);
			}
		
			// Move relative to current rotation
			// From net.minecraft.client.render.Camera.moveBy
			public void move(double fwd, double up, double right) {
				x += (double) horizontalPlane.x() * fwd
				   + (double) verticalPlane.x()   * up
				   + (double) diagonalPlane.x()   * right;
				
				y += (double) horizontalPlane.y() * fwd
				   + (double) verticalPlane.y()   * up
				   + (double) diagonalPlane.y()   * right;
				
				z += (double) horizontalPlane.z() * fwd
				   + (double) verticalPlane.z()   * up
				   + (double) diagonalPlane.z()   * right;
			}
		
			public static FreecamPosition getSwimmingPosition(Entity entity) {
				@NotNull FreecamPosition position = new FreecamPosition(entity);
		
				// Set pose to swimming, adjusting y position so eye-height doesn't change
				if (position.pose != Pose.SWIMMING) {
					position.y += entity.getEyeHeight(position.pose) - entity.getEyeHeight(Pose.SWIMMING);
					position.pose = Pose.SWIMMING;
				}
		
				return position;
			}
		
			public @NotNull ChunkPos getChunkPos() {
				return new ChunkPos((int) (x / 16), (int) (z / 16));
			}
		}
		
		public class Motion {

			public static final double DIAGONAL_MULTIPLIER = Mth.sin((float) Math.toRadians(45));

			public static void doMotion(FreeCamera freeCamera, double hSpeed, double vSpeed) {
				float yaw = freeCamera.getYRot();
				double velocityX = 0.0;
				double velocityY = 0.0;
				double velocityZ = 0.0;

				Vec3 forward = Vec3.directionFromRotation(0, yaw);
				Vec3 side = Vec3.directionFromRotation(0, yaw + 90);

				freeCamera.input.tick(false, 0.3F);
				hSpeed = hSpeed * (freeCamera.isSprinting() ? 1.5 : 1.0);

				boolean straight = false;
				if (freeCamera.input.up) {
					velocityX += forward.x * hSpeed;
					velocityZ += forward.z * hSpeed;
					straight = true;
				}
				if (freeCamera.input.down) {
					velocityX -= forward.x * hSpeed;
					velocityZ -= forward.z * hSpeed;
					straight = true;
				}

				boolean strafing = false;
				if (freeCamera.input.right) {
					velocityZ += side.z * hSpeed;
					velocityX += side.x * hSpeed;
					strafing = true;
				}
				if (freeCamera.input.left) {
					velocityZ -= side.z * hSpeed;
					velocityX -= side.x * hSpeed;
					strafing = true;
				}

				if (straight && strafing) {
					velocityX *= DIAGONAL_MULTIPLIER;
					velocityZ *= DIAGONAL_MULTIPLIER;
				}

				if (freeCamera.input.jumping) {
					velocityY += vSpeed;
				}
				if (freeCamera.input.shiftKeyDown) {
					velocityY -= vSpeed;
				}

				freeCamera.setDeltaMovement(velocityX, velocityY, velocityZ);
			}
		}
	}
}
