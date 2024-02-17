package com.quartzshard.aasb.common.item.equipment.armor.jewellery;

import java.util.function.Consumer;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.WayUtil;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public abstract class JewelleryArmorItem extends ArmorItem {
	public JewelleryArmorItem(Type slot, Properties props) {
		super(JewelleryMaterial.MAT, slot, props);
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {return 0;}
	
	/**
	 * Helper function for doing things involving this armor set <br>
	 * gathers information about the wearer (such as pieces worn & avaliable energy) for easy-access
	 * 
	 * @param stack The armor piece ItemStack
	 * @param level The level
	 * @param player The player with the armor
	 * @return Info about the set's current state
	 */
	public static JewellerySetInfo getArmorSetInfo(ItemStack stack, Level level, Player player) {
		JewelleryInfo head = getInfo(player, EquipmentSlot.HEAD),
				chest = getInfo(player, EquipmentSlot.CHEST),
				legs = getInfo(player, EquipmentSlot.LEGS),
				feet = getInfo(player, EquipmentSlot.FEET);
		boolean setBonus = head.id > 1 && chest.id > 1 && legs.id > 1 && feet.id > 1;
		return new JewellerySetInfo(head, chest, legs, feet, setBonus, WayUtil.getAvaliableWay(player));
	}
	
	public static boolean fullSet(Player player) {
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof JewelleryArmorItem)
				continue;
			return false;
		}
		return true;
	}
	
	public static JewelleryInfo getInfo(Player player, EquipmentSlot slot) {
		ItemStack stack = player.getItemBySlot(slot);
		if ( stack.isEmpty() || !(stack.getItem() instanceof JewelleryArmorItem) ) {
			return JewelleryInfo.MISSING;
		} else if (stack.isDamaged()) {
			return JewelleryInfo.BROKEN;
		} else {
			return JewelleryInfo.PRISTINE;
		}
	}
	
	record JewellerySetInfo(JewelleryInfo head, JewelleryInfo chest, JewelleryInfo legs, JewelleryInfo feet, boolean hasBonus, long plrWay) {
		public JewelleryInfo get(EquipmentSlot slot) {
			switch (slot) {
			case HEAD:
				return head;
			case CHEST:
				return chest;
			case LEGS:
				return legs;
			case FEET:
				return feet;
			default:
				Logger.warn("JewellerySetInfo.get()", "InvalidArmorSlot", slot.toString());
				return JewelleryInfo.MISSING;
			}
		}
	}
	
	public enum JewelleryInfo {
		ACTIVE((byte)3), PRISTINE((byte)2), BROKEN((byte)1), MISSING((byte)0);
		
		public final byte id;
		private JewelleryInfo(byte id) {
			this.id = id;
		}

		public boolean exists() {
			return id >= 1;
		}
		public static boolean exists(JewelleryInfo info) {
			return info.id >= 1;
		}

		public boolean pristine() {
			return id >= 2;
		}
		public static boolean pristine(JewelleryInfo info) {
			return info.id >= 2;
		}
	}

	public static class JewelleryMaterial implements ArmorMaterial {
		public static final JewelleryMaterial MAT = new JewelleryMaterial();

		@Override
		public int getEnchantmentValue() {
			return 0;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ARMOR_EQUIP_CHAIN;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}

		@Override
		public String getName() {return AASB.rl("jewellery").toString();}

		@Override
		public float getToughness() {
			return 0;
		}

		@Override
		public float getKnockbackResistance() {
			return 0;
		}

		@Override
		public int getDurabilityForType(Type pType) {
			return 0;
		}

		@Override
		public int getDefenseForType(Type pType) {
			return 1;
		}
		
	}
}
