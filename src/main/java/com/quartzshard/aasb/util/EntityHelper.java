package com.quartzshard.aasb.util;

import java.util.Random;
import java.util.UUID;

import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * generic entity helper functions <br>
 * player specific stuff in PlrHelper
 * @author solunareclipse1
 */
public class EntityHelper {
	
	/**
	 * makes an entity teleport like an enderman
	 * @param ent
	 * @return if the teleport was sucessful
	 */
	public static boolean attemptRandomTeleport(LivingEntity ent) {
		boolean didDo = false;
		Random rand = ent.getRandom();
		Level level =  ent.level;
		boolean shouldTry = !level.isClientSide() && ent.isAlive();
		for (int i = 0; i < 64; ++i) {
			if (shouldTry) {
				double tryX = ent.getX() + (rand.nextDouble() - 0.5) * 64d;
				double tryY = ent.getY() + (double)(rand.nextInt(64) - 32);
				double tryZ = ent.getZ() + (rand.nextDouble() - 0.5D) * 64.0D;
				if (true) {
					BlockPos.MutableBlockPos mbPos = new BlockPos.MutableBlockPos(tryX, tryY, tryZ);
					while (mbPos.getY() > level.getMinBuildHeight() && !level.getBlockState(mbPos).getMaterial().blocksMotion()) {
						mbPos.move(Direction.DOWN);
					}
					BlockState targetBlock = level.getBlockState(mbPos);
					boolean targetIsSolid = targetBlock.getMaterial().blocksMotion();
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
			invincible = plr.isCreative();
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
	
	public static boolean isImmuneToGravityManipulation(Entity entity) {
		return (entity instanceof Player player && player.getItemBySlot(EquipmentSlot.LEGS).is(ObjectInit.Items.MINIUM_STONE.get())) || isInvincible(entity);
	}
	
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
	
	/**
	 * checks if a damagesource is "infinite", like from avaritia items (sword of the cosmos)
	 * @param src
	 * @return
	 */
	public static boolean isDamageSourceInfinite(DamageSource src) {
		return false;
		//return src instanceof InfinityDamageSource
		//		|| src.getDirectEntity() instanceof InfinityArrowEntity
		//		|| (!src.isProjectile()
		//				&& src.getEntity() instanceof LivingEntity lEnt
		//				&& lEnt.getMainHandItem().getItem() == AvaritiaModContent.INFINITY_SWORD.get());
	}
	
	/**
	 * Hurts an entity, completely bypassing iframes
	 * @param ent
	 * @param src
	 * @param dmg
	 */
	public static void hurtNoDamI(LivingEntity ent, DamageSource src, float dmg) {
		int iframes = ent.invulnerableTime;
		ent.invulnerableTime = 0;
		ent.hurt(src, dmg);
		ent.invulnerableTime = iframes;
	}
}
