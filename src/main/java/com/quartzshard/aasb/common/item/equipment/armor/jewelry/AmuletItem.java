package com.quartzshard.aasb.common.item.equipment.armor.jewelry;

import java.util.HashMap;
import java.util.Map;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IAlchemicalBarrier;
import com.quartzshard.aasb.common.damage.source.AASBDmgSrc.ICustomDamageSource;
import com.quartzshard.aasb.init.EffectInit;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID)
public class AmuletItem extends JewelryArmor implements IAlchemicalBarrier {
	public AmuletItem(Properties props) {
		super(EquipmentSlot.CHEST, props);
	}
	/** Damage sources with corresponging cost multipliers. 0.5 would mean 1/2 cost */
	public static final Map<DamageSource, Float> DMG_SRC_MODS_ALCHSHIELD = new HashMap<>();
	/** Damage sources in here will *never* be blocked by the gem shield */
	public static DamageSource[] dmgSrcBlacklistAlchshield = {
			DamageSource.DROWN,
			DamageSource.FREEZE,
			DamageSource.OUT_OF_WORLD,
			DamageSource.STARVE
	};
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		// TODO: COST, major cleanup
		if (level.isClientSide) {
			// Client
			if (!stack.isDamaged()) {
				//GemJewelrySetInfo set = jewelryTick(stack, level, player);
				//long plrEmc = set.plrEmc();
				//
				//// This should be last, so that plrEmc is accurate!
				if (/*plrEmc > 0 &&*/ level.getGameTime() % 160 == 0) {
					shieldHum(player, stack);
				}
			}
		} else {
			// Server
			//GemJewelrySetInfo set = jewelryTick(stack, level, player);
			//long plrEmc = set.plrEmc();
			//if (!stack.isDamaged()) {
			//	// gem of density
			//	long amount = Math.min(CAPACITY/10, CAPACITY - getStoredEmc(stack));
			//	if (amount > 0 && getInfo(player, EquipmentSlot.LEGS).pristine()) {
			//		condenserRefill(stack, player, amount);
			//		// recalculate plrEmc, since it could be desynced
			//		plrEmc = EmcHelper.getAvaliableEmc(player);
			//	}
				
				// life stone
				if (/*!player.hasEffect(EffectInit.TRANSMUTING.get()) && plrEmc >= Chest.REJUVENATE.get() &&*/ level.getGameTime() % 7 == 0) {
					if (tryHeal(player)) {
						//plrEmc -= EmcHelper.consumeAvaliableEmc(player, Chest.REJUVENATE.get());
					}
					// plrEmc might have changed, need to re-check
					if (/*plrEmc >= Chest.REJUVENATE.get() &&*/ tryFeed(player)) {
						//plrEmc -= EmcHelper.consumeAvaliableEmc(player, Chest.REJUVENATE.get());
					}
				}
				
				
				// This should be last, so that plrEmc is accurate!
				if (/*plrEmc > 0 &&*/ level.getGameTime() % 160 == 0) {
					shieldHum(player, stack);
				}
		}
	}
	
	private void shieldHum(Player player, ItemStack stack) {
		if (shieldCondition(player, 1f, DamageSource.GENERIC, stack)) {
			player.level.playSound(player, player, EffectInit.Sounds.BARRIER_AMBIENT.get(), SoundSource.PLAYERS, 1f, 1);
		}
	}
	
	private static boolean tryHeal(Player player) {
		if (player.getHealth() < player.getMaxHealth()) {
			player.heal(1);
			return true;
		}
		return false;
	}
	private static boolean tryFeed(Player player) {
		if (player.getFoodData().needsFood()) {
			player.getFoodData().eat(1, 10);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean shieldCondition(Player player, float damage, DamageSource source, ItemStack stack) {
    	return sourceBlockedByGemShield(source) && isBarrierActive(player);
    }
	
	/** used in both canProtectAgainstFire and shieldCondition */
	public static boolean isBarrierActive(Player player) {
		if (fullSet(player)) {
	    	return true;//EmcHelper.hasEmc(player);
		}
		return false;
	}
	
	/**
	 * checks if a given damage source can be blocked by the gem shield
	 * @param source
	 * @return if the source can be blocked by the gem shield
	 */
	public static boolean sourceBlockedByGemShield(DamageSource source) {
		// hardcoded checks for things that should absolutely never be blocked
		if (source.isCreativePlayer()
			|| source.isBypassInvul()
			/*|| EntityHelper.isDamageSourceInfinite(source)*/) {
			return false;
		}
		if (source instanceof ICustomDamageSource src && src.isBypassAlchShield()) return false;
		
		for (int i = 0; i < dmgSrcBlacklistAlchshield.length; i++) {
			if (source == dmgSrcBlacklistAlchshield[i]) return false;
		}
		return true;
	}
	
	/**
	 * Gets the cost multiplier for a given source <br>
	 * default to 1.0f (no multiplier)
	 * @param source
	 * @return
	 */
	public static float getCostMultiplierForSource(DamageSource source) {
		// explicit overrides
		if (DMG_SRC_MODS_ALCHSHIELD.containsKey(source)) {
			return DMG_SRC_MODS_ALCHSHIELD.get(source);
		}
		
		// overriders, biggest goes last so it takes priority
		float mult = 1f;
		if (source.isBypassArmor()) mult = 1.1f;
		if (source.isMagic() || source.isBypassMagic()) mult = 1.5f;
		if (source instanceof ICustomDamageSource src) {
			if (src.isBypassDr()) mult = 2f;
			if (src.isDivine()) mult = 42f;
			// adders, order doesnt matter
			if (src.isAlchemy()) mult += 0.1f;
		}
		return mult;
	}
	
	@Override
	public long calcShieldingCost(Player player, float damage, DamageSource source, ItemStack stack) {
		// ( dmg * mod ) ^ exp = emc
		return (long) Math.max(64, Math.pow(damage*getCostMultiplierForSource(source), 2));
	}
	
	@Override
	public float calcAffordableDamage(Player player, float damage, DamageSource source, ItemStack stack, long emcHeld) {
		// ( emc^(1/exp) ) / mod = dmg
		return (float) (Math.pow(emcHeld, 1/64)/getCostMultiplierForSource(source));
	}
	
	
	/** what */
	@SubscribeEvent
	public static void checkAlchemicalBarrier(LivingAttackEvent event) {
		// Run the IFireProtector checks from projecte before proceeding
		//PlayerEvents.onAttacked(event);
		//if (event.isCanceled()) return;
		
		Entity ent = event.getEntity();
		
		// this fixes the server getting stuck in an infinite loop when hit by piercing smart arrows
		// TODO: check if this is actually necessary
		//if (ent instanceof EntityDoppleganger gaia && gaia.getInvulTime() > 0) {
		//	if (event.getSource().getDirectEntity() instanceof SmartArrow arrow) {
		//		arrow.becomeInert();
		//		arrow.expire();
		//		event.setCanceled(true);
		//		return;
		//	}
		//}
		
		 // order of priority: offhand, curios, armor, inventory
		if (ent instanceof Player player) {
			//if (player.getOffhandItem().getItem() instanceof IAlchemicalBarrier shieldItem) {
			//	shieldItem.tryShield(event, player.getOffhandItem());
			//	if (event.isCanceled()) return;
			//}
			
			// TODO: curios for alch shield maybe
			//IItemHandler curios = PlayerHelper.getCurios(player);
			//if (curios != null) { // does is curios?
			//	for (int i = 0; i < curios.getSlots(); i++) {
			//		ItemStack stack = curios.getStackInSlot(i);
			//		if (stack.getItem() instanceof IAlchemicalBarrier shieldItem) {
			//			shieldItem.tryShield(event, stack);
			//			if (event.isCanceled()) return;
			//		}
			//	}
			//}
			
			ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
			if (stack.getItem() instanceof AmuletItem amulet) {
				amulet.tryShield(event, stack);
				//if (event.isCanceled()) return;
			}
			
			//for (ItemStack stack : player.getArmorSlots()) {
			//	if (stack.getItem() instanceof IAlchemicalBarrier shieldItem) {
			//		shieldItem.tryShield(event, stack);
			//		if (event.isCanceled()) return;
			//	}
			//}
			
			//Optional<IItemHandler> itemHandlerCap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
			//if (itemHandlerCap.isPresent()) {
			//	IItemHandler inv = itemHandlerCap.get();
			//	for (int i = 0; i < inv.getSlots(); i++) {
			//		ItemStack stack = inv.getStackInSlot(i);
			//		if (stack.getItem() instanceof IAlchShield shieldItem) {
			//			shieldItem.tryShield(event, stack);
			//			if (event.isCanceled()) return;
			//		}
			//	}
			//}
			
		}
	}

}
