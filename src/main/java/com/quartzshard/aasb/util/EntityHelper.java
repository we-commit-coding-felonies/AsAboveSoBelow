package com.quartzshard.aasb.util;

import java.util.UUID;

import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;

/**
 * generic entity helper functions <br>
 * player specific stuff in PlrHelper
 * @author solunareclipse1
 */
public class EntityHelper {
	
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
	
	
}
