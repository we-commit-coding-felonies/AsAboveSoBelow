package com.quartzshard.aasb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;
import com.quartzshard.aasb.common.entity.projectile.SmartArrowEntity;
import com.quartzshard.aasb.init.object.ItemInit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * generic entity helper functions <br>
 * player specific stuff in PlrHelper
 * @author solunareclipse1
 */
public class EntUtil {
	
	@Nullable
	public static LightningBolt smite(Level level, Vec3 pos, @Nullable ServerPlayer culprit, boolean harmless) {
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
		if (bolt != null) {
			bolt.moveTo(pos);
			bolt.setCause(culprit);
			bolt.setVisualOnly(harmless);
			level.addFreshEntity(bolt);
		}
		return bolt;
	}
	
	public class Projectiles {
		
		public static class ShootContext {
			final Level level;
			
			@Nullable
			final LivingEntity shooter;
			
			@Nullable
			final Vec3 pos, rot;
			
			public ShootContext(Level level, LivingEntity shooter) {
				this.level = level;
				this.shooter = shooter;
				this.pos = null;
				this.rot = new Vec3(shooter.getXRot(), shooter.getYRot(), 0);
			}
			public ShootContext(Level level, LivingEntity shooter, Vec3 rot) {
				this.level = level;
				this.shooter = shooter;
				this.pos = null;
				this.rot = rot;
			}
			public ShootContext(Level level, Vec3 pos, Vec3 rot) {
				this.level = level;
				this.shooter = null;
				this.pos = pos;
				this.rot = rot;
			}
		}
		
		public record ArrowOptions(float damage, float velocity, float spread, byte pierce, boolean crit, Pickup pickup) {}
		
		public enum ArrowType {
			NORMAL,
			STRAIGHT,
			SMART,
			HOMING,
			SENTIENT;
			
			AbstractArrow make(ShootContext ctx, ArrowOptions opts) {
				AbstractArrow arrow;
				switch (this) {
				case STRAIGHT:
					if (ctx.shooter == null) {
						Logger.warn("EntUtil.Projectiles.ArrowType.make()", "UnsupportedNullShooter", "Attempted to create arrow of type "+this.name()+" with a null shooter, but that type does not support it. Falling back on a normal arrow.");
						arrow = new Arrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z);
						arrow.setBaseDamage(opts.damage);
					} else arrow = new SmartArrowEntity(ctx.level, ctx.shooter, opts.damage, 20, (byte) 2);
					arrow.setNoGravity(true);
					break;
				case SMART:
					if (ctx.shooter == null) {
						Logger.warn("ProjectileHelper.ArrowType.make()", "UnsupportedNullShooter", "Attempted to create arrow of type "+this.name()+" with a null shooter, but that type does not support it. Falling back on a normal arrow.");
						arrow = new Arrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z);
						arrow.setBaseDamage(opts.damage);
					} else arrow = new SmartArrowEntity(ctx.level, ctx.shooter, opts.damage);
					break;
				case HOMING:
					if (true || ctx.shooter == null) {
						Logger.warn("ProjectileHelper.ArrowType.make()", "ArrowNYI", "ArrowType " + this.name() + " is currently unimplemented.");
						//LogHelper.warn("ProjectileHelper.ArrowType.make()", "UnsupportedNullShooter", "Attempted to create arrow of type "+this.name()+" with a null shooter, but that type does not support it. Falling back on a normal arrow.");
						arrow = new Arrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z);
						arrow.setBaseDamage(opts.damage);
					}// else arrow = new EntityHomingArrow(ctx.level, ctx.shooter, opts.damage);
					break;
				case SENTIENT:
					arrow = ctx.shooter == null ?
							new SentientArrowEntity(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z) :
							new SentientArrowEntity(ctx.level, ctx.shooter);
					arrow.setGlowingTag(true);
					arrow.setBaseDamage(opts.damage);
					break;
					
				default:
					arrow = ctx.shooter == null ?
							new Arrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z) :
							new Arrow(ctx.level, ctx.shooter);
					arrow.setBaseDamage(opts.damage);
					break;
				}
				if (ctx.shooter != null) {
					arrow.shootFromRotation(ctx.shooter, (float)ctx.rot.x, (float)ctx.rot.y, 0, opts.velocity, opts.spread);
				} else {
					float mx = -Mth.sin((float) (ctx.rot.y * (Math.PI / 180d))) * Mth.cos((float) (ctx.rot.x * (Math.PI / 180d)));
					float my = -Mth.sin((float) ((ctx.rot.x + ctx.rot.z) * (Math.PI / 180d)));
					float mz = Mth.cos((float) (ctx.rot.y * (Math.PI / 180d))) * Mth.cos((float) (ctx.rot.x * (Math.PI / 180d)));
					RandomSource rand = ctx.level.random;
					Vec3 m = new Vec3(mx, my, mz).normalize().add(rand.nextGaussian() * 0.0075d * opts.spread, rand.nextGaussian() * 0.0075d * opts.spread, rand.nextGaussian() * 0.0075d * opts.spread).scale(opts.velocity);
					arrow.setDeltaMovement(m);
				}
				arrow.setPierceLevel(opts.pierce);
				arrow.setCritArrow(opts.crit);
				arrow.pickup = opts.pickup;
				return arrow;
			}
		}
		
		/**
		 * helper for shooting arrows
		 * @param amount number of arrows to shoot
		 * @param type what kind of arows to shoot
		 * @param ctx the context of shooting
		 * @param opts universal arrow settings
		 * 
		 * @return array of all arrows shot
		 */
		public static List<AbstractArrow> shootArrow(int amount, ArrowType type, ShootContext ctx, ArrowOptions opts) {
			List<AbstractArrow> arrows = new ArrayList<>(amount);
			for (int i = 0; i < amount; i++) {
				AbstractArrow a = type.make(ctx, opts);
				ctx.level.addFreshEntity(a);
				ctx.level.playSound(null, a.getX(), a.getY(), a.getZ(),
						SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
						1f, 1f / (ctx.level.getRandom().nextFloat() * 0.4f + 1.2f) + (opts.velocity/3f) * 0.5f);
				arrows.add(a);
			}
			return arrows;
		}

		public static SmallFireball fireball(Level level, Vec3 pos, Vec3 target, @Nullable LivingEntity owner, double accMod) {
	        double dist = pos.distanceToSqr(target);//owner.distanceToSqr(target);
	        Vec3 shootVec = new Vec3(
	        	target.x - pos.x,
	        	target.y - pos.y,
	        	target.z - pos.z
	        );
	        double acc = Math.sqrt(Math.sqrt(dist)) * accMod;
			SmallFireball fb = new SmallFireball(level, owner,
					shootVec.x + owner.getRandom().nextGaussian() * acc,
					shootVec.y + owner.getRandom().nextGaussian() * acc,
					shootVec.z + owner.getRandom().nextGaussian() * acc);
			fb.setPos(pos);
			level.addFreshEntity(fb);
			return fb;
		}
		public static SmallFireball fireball(Level level, Vec3 pos, Vec3 target, @Nullable LivingEntity owner) {
			return fireball(level, pos, target, owner, 0.35);
		}
	}

	
	/**
	 * makes an entity teleport like an enderman
	 * @param ent
	 * @return if the teleport was sucessful
	 */
	public static boolean attemptRandomTeleport(LivingEntity ent) {
		boolean didDo = false;
		RandomSource rand = ent.getRandom();
		Level level =  ent.level();
		boolean shouldTry = !level.isClientSide() && ent.isAlive();
		for (int i = 0; i < 64; ++i) {
			if (shouldTry) {
				double tryX = ent.getX() + (rand.nextDouble() - 0.5) * 64d;
				double tryY = ent.getY() + (rand.nextInt(64) - 32);
				double tryZ = ent.getZ() + (rand.nextDouble() - 0.5D) * 64.0D;
				if (true) {
					BlockPos.MutableBlockPos mbPos = new BlockPos.MutableBlockPos(tryX, tryY, tryZ);
					while (mbPos.getY() > level.getMinBuildHeight() && !level.getBlockState(mbPos).blocksMotion()) {
						mbPos.move(Direction.DOWN);
					}
					BlockState targetBlock = level.getBlockState(mbPos);
					boolean targetIsSolid = targetBlock.blocksMotion();
					boolean targetIsWater = targetBlock.getFluidState().is(FluidTags.WATER);
					if (targetIsSolid && (!targetIsWater || !ent.isSensitiveToWater())) {
						if (ent instanceof EnderMan man) {
							// only send the enderman tele event if we are an enderman
							net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(man, tryX, tryY, tryZ);
							if (event.isCanceled()) return false;
							didDo = ent.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
						} else {
							didDo = ent.randomTeleport(tryX, tryY, tryZ, true);
						}
						if (didDo) {
							if (!ent.isSilent()) {
								level.playSound(null, ent.xo, ent.yo, ent.zo, SoundEvents.ENDERMAN_TELEPORT, ent.getSoundSource(), 1, 1);
								ent.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
							}
							break;
						}
					}
				}
			} else {
				// invalid, return early
				return false;
			}
		}
		return didDo;
	}
	
	/**
	 * checks if an entity should never be harmed <br>
	 * stuff like the invulnerable tag, creative mode, and infinity armor <br>
	 * does NOT check for iframes (invulnerableTime), its only for "non-bypassable" invincibility
	 * 
	 * @param entity
	 * @return if entity is invincible
	 */
	public static boolean isInvincible(Entity entity) {
		boolean invincible = entity.isInvulnerable();
		if (!invincible && entity instanceof Player plr) {
			invincible = plr.isCreative() || plr.isSpectator();
		}
		return invincible;
	}
	
	/**
	 * @param entity
	 * @return false if entity isInvincible or has iframes
	 */
	public static boolean canCurrentlyHurt(Entity entity) {
		return entity.invulnerableTime == 0 && !isInvincible(entity);
	}
	
	/*
	public static boolean isImmuneToGravityManipulation(Entity entity) {
		return (entity instanceof Player player && player.getItemBySlot(EquipmentSlot.LEGS).is(ObjectInit.Items.POCKETWATCH.get())) || isInvincible(entity);
	}
	*/
	
	/**
	 * true if given entity is tamed
	 */
	public static boolean isTamed(Entity entity) {
		return entity instanceof TamableAnimal animal && animal.isTame()
				|| entity instanceof AbstractHorse horse && horse.isTamed();
	}
	
	/**
	 * player-specific
	 */
	public static boolean isTamedBy(Entity entity, Player player) {
		if (entity instanceof TamableAnimal animal) {
			return animal.isOwnedBy(player);
		}
		if (entity instanceof AbstractHorse horse && horse.isTamed()) {
			UUID ownerUUID = horse.getOwnerUUID();
			if (ownerUUID != null) {
				return ownerUUID.equals(player.getUUID());
			}
		}
		return false;
	}
	
	/**
	 * checks if the entity trusts anything <br>
	 * for things like foxes
	 */
	public static boolean hasTrust(Entity entity) {
		// Fox, Ocelot
		return entity instanceof Fox fox && !fox.getTrustedUUIDs().isEmpty()
				|| entity instanceof Ocelot cat && cat.isTrusting();
	}
	
	/**
	 * checks if the entity trusts the given player <br>
	 * for things like foxes
	 */
	public static boolean isTrustingOf(Entity entity, Player player) {
		// Fox, Ocelot
		return entity instanceof Fox fox && fox.getTrustedUUIDs().contains(player.getUUID())
				|| entity instanceof Ocelot cat && cat.isTrusting();
	}
	
	public static boolean isTamedOrTrusting(Entity entity) {
		return isTamed(entity) || hasTrust(entity);
	}
	
	public static boolean isTamedByOrTrusts(Entity entity, Player player) {
		return isTamedBy(entity, player) || isTrustingOf(entity, player);
	}

	public static boolean resistsSpacetimeShenanigans(@NotNull LivingEntity entity) {
		return (entity instanceof Player player && player.getItemBySlot(EquipmentSlot.LEGS).is(ItemInit.POCKETWATCH.get())) || isInvincible(entity);
	}
}
