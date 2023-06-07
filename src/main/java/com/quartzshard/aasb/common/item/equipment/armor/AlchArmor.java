package com.quartzshard.aasb.common.item.equipment.armor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IDamageReducer;
import com.quartzshard.aasb.common.damage.source.AASBDmgSrc.ICustomDamageSource;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID)
public abstract class AlchArmor extends ArmorItem implements IDamageReducer {
	private float baseDr;
	
	/** Damage sources with corresponging DR multipliers. 0.5 would mean 1/2 DR */
	public static final Map<DamageSource, Float> DMG_SRC_MODS_DR = new HashMap<>();
	/** Damage sources in here will *never* be affected by DR */
	public static DamageSource[] dmgSrcBlacklistDr = {
			DamageSource.DROWN,
			DamageSource.FREEZE,
			DamageSource.OUT_OF_WORLD,
			DamageSource.STARVE,
			DamageSource.WITHER
	};
	/**
	 * Semiclone of Dark Matter armor. <br>
	 * Has reduced damage reduction when enchanted. <br>
	 * Cannot be enchanted with protection enchantments.
	 * 
	 * @param slot The EquipmentSlot this item belongs in
	 * @param baseDR The base amount of Damage Reduction this item provides
	 * @param props The properties of the item
	 */
	public AlchArmor(ArmorMaterial mat, EquipmentSlot slot, Properties props, float baseDr) {
		super(mat, slot, props);
		this.baseDr = baseDr;

		DMG_SRC_MODS_DR.put(DamageSource.LIGHTNING_BOLT, 0.99f/4f);
		DMG_SRC_MODS_DR.put(DamageSource.ANVIL, 0.1f/4f);
		DMG_SRC_MODS_DR.put(DamageSource.badRespawnPointExplosion(), 0.8f/4f);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
	}
	
	public float getBaseDr() {
		return baseDr;
	}
	
	/**
	 * Gets the current damage reduction value this itemstack can provide
	 * 
	 * @param stack the stack 
	 * @return
	 */
	public float getDr(ItemStack stack, DamageSource source) {
		if (sourceCanBeReduced(source)) {
			return (float) (getDrForSource(source));
		}
		return 0;
	}
	
	public static boolean sourceCanBeReduced(DamageSource source) {
		// hardcoded checks for things that should absolutely never be blocked
		if (source.isCreativePlayer()
			|| source.isBypassInvul()) {
			return false;
		}
		if (source instanceof ICustomDamageSource src && src.isBypassDr()) return false;
		
		for (int i = 0; i < dmgSrcBlacklistDr.length; i++) {
			if (source == dmgSrcBlacklistDr[i]) return false;
		}
		return true;
	}
	
	public float getDrForSource(DamageSource source) {
		// explicit overrides
		if (DMG_SRC_MODS_DR.containsKey(source)) {
			return DMG_SRC_MODS_DR.get(source);
		}
		
		float dr = getBaseDr();
		if (source.isBypassArmor()) dr *= 0.9;
		if (source.isMagic() || source.isBypassMagic()) dr *= 0.75;
		if (source.isFire()) dr *= 1.1;
		if (source instanceof ICustomDamageSource src) {
			if (src.isPlasma()) dr *= 1.1;
			if (src.isAlchemy()) dr -= 0.2;
		}
		return dr;
	}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {return false;}
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {return false;}
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {return false;}
	

	/**
	 * damage absorb handling
	 * @param event
	 */
	@SubscribeEvent
	public static void reduceDamage(LivingHurtEvent event) {
		float damage = event.getAmount();
		if (damage > 0) {
			LivingEntity entity = event.getEntityLiving();
			DamageSource source = event.getSource();
			Map<ItemStack, Float> absorbList = new HashMap<>();
			float totalDr = 0;
			for (ItemStack stack : entity.getArmorSlots()) {
				if (!stack.isEmpty() && stack.getItem() instanceof IDamageReducer drItem) {
					float dr = drItem.getDr(stack, source);
					totalDr += dr;
					absorbList.put(stack, dr);
				}
			}
			if (totalDr > 0) {
				if (totalDr >= 1) {
					event.setCanceled(true);
				} else {
					event.setAmount(damage * (1f-totalDr));
				}
				entity.level.playSound(null, entity, SoundEvents.SHIELD_BLOCK, entity.getSoundSource(), Math.min(1, totalDr), 1.5f);
				for (Entry<ItemStack, Float> absorber : absorbList.entrySet()) {
					ItemStack stack = absorber.getKey();
					float absorbed = absorber.getValue()*event.getAmount();
					stack.hurtAndBreak(Mth.ceil(absorbed), entity, ent -> {
						armorBreak(stack, ent);
					});
				}
			}
		}
	}
	
	private static void armorBreak(ItemStack stack, LivingEntity entity) {
		entity.broadcastBreakEvent(LivingEntity.getEquipmentSlotForItem(stack));
	}

}
