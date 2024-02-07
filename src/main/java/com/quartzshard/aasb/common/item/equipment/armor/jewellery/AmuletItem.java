package com.quartzshard.aasb.common.item.equipment.armor.jewellery;

import java.util.HashMap;
import java.util.Map;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.item.IAlchemicalBarrier;
import com.quartzshard.aasb.data.tags.DmgTP;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.util.WayUtil;

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

@Mod.EventBusSubscriber(modid = AASB.MODID)
public class AmuletItem extends JewelleryArmorItem implements IAlchemicalBarrier {
	public AmuletItem(Properties props) {
		super(Type.CHESTPLATE, props);
	}
	/** Damage sources with corresponging cost multipliers. 0.5 would mean 1/2 cost */
	public static final Map<DamageSource, Float> DMG_SRC_MODS_ALCHSHIELD = new HashMap<>();
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		// TODO: COST, major cleanup
		JewellerySetInfo set = getArmorSetInfo(stack, level, player);
		long plrWay = set.plrWay();
		if (plrWay > 0 && level.getGameTime() % 160 == 0) {
			shieldHum(player, stack);
		}
	}
	
	private void shieldHum(Player player, ItemStack stack) {
		if (shieldCondition(player, 1f, player.damageSources().generic(), stack)) {
			player.level().playSound(player, player, FxInit.SND_BARRIER_AMBIENT.get(), SoundSource.PLAYERS, 1f, 1);
		}
	}
	
	@Override
	public boolean shieldCondition(Player player, float damage, DamageSource source, ItemStack stack) {
    	return IAlchemicalBarrier.super.shieldCondition(player, damage, source, stack) && isBarrierActive(player);
    }
	
	/** used in both canProtectAgainstFire and shieldCondition */
	public static boolean isBarrierActive(Player player) {
		if (fullSet(player)) {
	    	return WayUtil.hasEmc(player);
		}
		return false;
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
		if (source.is(DmgTP.BYPASSES_PHYSICAL)) mult = 1.1f;
		if (source.is(DmgTP.BYPASSES_MAGICAL)) mult = 1.5f;
		if (source.is(DmgTP.BYPASSES_DMG_SPONGE)) mult = 2f;
		//if (src.isDivine()) mult = 42f;
		// adders, order doesnt matter
		//if (source.is(DmgTP.BYPASSES_ALCHEMICAL)) mult += 0.1f;
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
	
	
	/** what
	 * <p>
	 * i agree, previous me. what?
	 * <p>
	 * i cleaned it up dont worry :3
	 * */
	@SubscribeEvent
	public static void checkAlchemicalBarrier(LivingAttackEvent event) {
		Entity ent = event.getEntity();
		
		if (ent instanceof Player player) {
			ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
			if (stack.getItem() instanceof AmuletItem amulet) {
				amulet.tryShield(event, stack);
			}
		}
	}

}
