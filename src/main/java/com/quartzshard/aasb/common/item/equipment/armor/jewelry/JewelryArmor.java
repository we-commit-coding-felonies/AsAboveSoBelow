package com.quartzshard.aasb.common.item.equipment.armor.jewelry;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public abstract class JewelryArmor extends ArmorItem {
	public JewelryArmor(EquipmentSlot slot, Properties props) {
		super(JewelryMaterial.MAT, slot, props);
	}
	
	/**
	 * Helper function for doing things involving this armor set <br>
	 * gathers information about the wearer (such as pieces worn & avaliable energy) for easy-access
	 * 
	 * @param stack The armor piece ItemStack
	 * @param level The level
	 * @param player The player with the armor
	 * @return Info about the set's current state
	 */
	public static JewelrySetInfo getArmorSetInfo(ItemStack stack, Level level, Player player) {
		//long plrEmc = EmcHelper.getAvaliableEmc(player);
		JewelryInfo head = getInfo(player, EquipmentSlot.HEAD),
				chest = getInfo(player, EquipmentSlot.CHEST),
				legs = getInfo(player, EquipmentSlot.LEGS),
				feet = getInfo(player, EquipmentSlot.FEET);
		boolean setBonus = head.id > 1 && chest.id > 1 && legs.id > 1 && feet.id > 1;
		return new JewelrySetInfo(head, chest, legs, feet, setBonus, 0);
	}
	
	public static boolean fullSet(Player player) {
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof JewelryArmor)
				continue;
			return false;
		}
		return true;
	}
	
	public static JewelryInfo getInfo(Player player, EquipmentSlot slot) {
		ItemStack stack = player.getItemBySlot(slot);
		if ( stack.isEmpty() || !(stack.getItem() instanceof JewelryArmor) ) {
			return JewelryInfo.MISSING;
		} else if (stack.isDamaged()) {
			return JewelryInfo.BROKEN;
		} else {
			return JewelryInfo.PRISTINE;
		}
	}
	
	record JewelrySetInfo(JewelryInfo head, JewelryInfo chest, JewelryInfo legs, JewelryInfo feet, boolean hasBonus, long plrEmc) {
		public JewelryInfo get(EquipmentSlot slot) {
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
				LogHelper.warn("JewelrySetInfo.get()", "InvalidArmorSlot", slot.toString());
				return JewelryInfo.MISSING;
			}
		}
	}
	
	public enum JewelryInfo {
		ACTIVE((byte)3), PRISTINE((byte)2), BROKEN((byte)1), MISSING((byte)0);
		
		public final byte id;
		private JewelryInfo(byte id) {
			this.id = id;
		}

		public boolean exists() {
			return id >= 1;
		}
		public static boolean exists(JewelryInfo info) {
			return info.id >= 1;
		}

		public boolean pristine() {
			return id >= 2;
		}
		public static boolean pristine(JewelryInfo info) {
			return info.id >= 2;
		}
	}

	public static class JewelryMaterial implements ArmorMaterial {
		public static final JewelryMaterial MAT = new JewelryMaterial();

		@Override
		public int getDurabilityForSlot(EquipmentSlot pSlot) {
			return 0;
		}

		@Override
		public int getDefenseForSlot(EquipmentSlot pSlot) {
			return 1;
		}

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
		public String getName() {return AsAboveSoBelow.rl("jewelry").toString();}

		@Override
		public float getToughness() {
			return 0;
		}

		@Override
		public float getKnockbackResistance() {
			return 0;
		}
		
	}
}
