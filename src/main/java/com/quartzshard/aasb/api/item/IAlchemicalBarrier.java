package com.quartzshard.aasb.api.item;

import java.util.LinkedHashMap;

import com.quartzshard.aasb.data.tags.DmgTP;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.WayUtil;

import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Block damage, similar to the Draconic Evolution shield <br>
 * TODO: COST, clean up, fix outdated comments & names
 * @author solunareclipse1
 */
public interface IAlchemicalBarrier {



	/**
	 * Checks to make sure we can even shield in the first place <br>
	 * If you want to conditionally stop shielding, do it here
	 * 
	 * @param player The player being shielded
	 * @param damage The amount of damage to shield
	 * @param source The DamageSource we are shielding
	 * @param stack The ItemStack doing the shielding
	 * @return boolean specifying whether or not to shield
	 */
	default boolean shieldCondition(Player player, float damage, DamageSource source, ItemStack stack) {
		if (source.isCreativePlayer()
			|| source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
			|| source.is(DmgTP.BYPASSES_FORCEFIELD)) return false;
		return true;
	}

	/**
	 * Attempts to shield a LivingAttackEvent using an ItemStack <br>
	 * This is what gets called by the event subscriber. <br>
	 * By default it just does some sanity checks, then offloads the rest to shieldWithWay. <br>
	 * Recommend you dont override this unless you need to for some reason
	 * 
	 * @param event The LivingAttackEvent being shielded
	 * @param stack The ItemStack doing the shielding
	 */
	default void tryShield(@NotNull LivingAttackEvent event, ItemStack stack) {
		Entity hurt = event.getEntity();
		if (hurt.level().isClientSide || event.isCanceled()) return;
		if (shieldWithWay((Player)hurt, event.getAmount(), event.getSource(), stack)) {
			event.setCanceled(true);
		}
	}

	/**
	 * Preliminary shielding against things that can be blocked for free so long as the shield is up
	 * 
	 * @param player The player being shielded
	 * @param damage The amount of damage to shield
	 * @param source The DamageSource we are shielding
	 * @param stack The ItemStack doing the shielding
	 * @return If shielding was at all successful
	 */
	default boolean shieldForFree(Player player, float damage, @NotNull DamageSource source, ItemStack stack) {
		return damage <= 0 || source.is(DmgTP.FORCEFIELD_EZBLOCK);
	}

	/**
	 * Handles sounds and Way consumption, as well as running many of the other functions.
	 * Override this if you need to change sounds or how EMC is consumed (for example, taking from the inventory instead of just amulet)
	 * 
	 * @param player The player being shielded
	 * @param damage The amount of damage to shield
	 * @param source The DamageSource we are shielding
	 * @param stack The ItemStack doing the shielding
	 * @return If shielding was at all successful
	 */
	default boolean shieldWithWay(@NotNull Player player, float damage, @NotNull DamageSource source, ItemStack stack) {
		boolean doDebug = false;
		if (doDebug) {
			long storedWay = WayUtil.getAvaliableWay(player);
			@NotNull LinkedHashMap<String,String> info = new LinkedHashMap<String,String>();
			info.put("Player Name", player.getName().getString());
			info.put("Player UUID", player.getStringUUID());
			info.put("Held Way", storedWay+"");
			info.put("Incoming Damage", damage+"");
			info.put("Damage Source", source.getMsgId());
			if(source.getDirectEntity() != source.getEntity() && source.getDirectEntity() != null) {
				info.put("Source Projectile", source.getDirectEntity().getEncodeId());
				info.put("Projectile UUID", source.getDirectEntity().getStringUUID());
				info.put("Projectile Position", source.getDirectEntity().position().toString());
			}
			if (source.getEntity() != null) {
				info.put("Source Entity", source.getEntity().getEncodeId());
				info.put("Entity UUID", source.getEntity().getStringUUID());
				info.put("Entity Position", source.getEntity().position().toString());
			}
			info.put("Will try shield", shieldCondition(player, damage, source, stack)+"");
			info.put("Way Cost", calcShieldingCost(player, damage, source, stack)+"");
			info.put("Affordable damage", calcAffordableDamage(player, damage, source, stack, storedWay)+"");
			
			Logger.debug("IAlchemicalBarrier", "ShieldingDebug", "Attempting to shieldWithWay", info);
		}
		
		if (shieldCondition(player, damage, source, stack)) {
			if (shieldForFree(player, damage, source, stack)) return true;
			long wayCost = calcShieldingCost(player, damage, source, stack);
			long wayHeld = WayUtil.getAmuletWay(player);//WayUtil.getAvaliableWay(player);
			if (wayCost <= wayHeld && wayHeld > 0) {
				long wayConsumed = WayUtil.consumeAmuletWay(player, wayCost);//WayUtil.consumeAvaliableWay(player, wayCost);
				if (wayConsumed > wayCost) {
					player.level().playSound(null, player, FxInit.SND_WAY_WASTE.get(), SoundSource.PLAYERS, 0.45F, 1.0F);
				}
				
				wayHeld -= wayConsumed;
				if (wayHeld > 0) {
					player.level().playSound(null, player, FxInit.SND_BARRIER_PROTECT.get(), SoundSource.PLAYERS, 0.45F, 1.0F);
				} else {
					player.level().playSound(null, player, FxInit.SND_BARRIER_FAIL.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
				}
				return true;
			}
			if (wayHeld <= 0) return false;
			float canAfford = calcAffordableDamage(player, damage, source, stack, wayHeld);
			WayUtil.consumeAmuletWay(player, wayHeld);
			player.hurt(source, damage - canAfford);
			player.level().playSound(null, player, FxInit.SND_BARRIER_FAIL.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
			return true;
		}
		return false;
	}
	
	/**
	 * Calculates the Way cost to shield <br>
	 * Override this to change the cost calculation <br>
	 * Default is Math.max(64, damage^2)
	 * 
	 * @param player Player being shielded
	 * @param damage Amount of incoming damage
	 * @param source DamageSource we are shielding
	 * @param stack ItemStack doing the shielding
	 * 
	 * @return Way cost to shield
	 */
	default long calcShieldingCost(Player player, float damage, DamageSource source, ItemStack stack) {
		return (long) Math.max(64, Math.pow(damage, 2));
		//long calcCost = (long) Math.pow(Math.max(8, damage), 2);
		//return calcCost < 64 ? Long.MAX_VALUE : calcCost;
	}
	
	/**
	 * Calculates how much damage we can afford to shield. <br>
	 * Normally, this is only called if (wayHeld < wayCost) <br>
	 * <br>
	 * Defaults to sqrt(wayHeld)
	 * 
	 * @param player Player being shielded
	 * @param damage Amount of incoming damage
	 * @param source DamageSource we are shielding
	 * @param stack ItemStack doing the shielding
	 * @param emcHeld Total EMC avaliable
	 * 
	 * @return Amount of damage we can afford
	 */
	default float calcAffordableDamage(Player player, float damage, DamageSource source, ItemStack stack, long emcHeld) {
		return (float)Math.sqrt(emcHeld);
	}
}
