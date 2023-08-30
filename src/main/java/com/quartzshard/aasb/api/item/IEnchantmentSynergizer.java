package com.quartzshard.aasb.api.item;

import java.util.Map.Entry;
import java.util.Set;

import com.quartzshard.aasb.util.Constants;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public interface IEnchantmentSynergizer {
	default double getBonusStrength(ItemStack stack) {
		Set<Entry<Enchantment,Integer>> enchList = EnchantmentHelper.getEnchantments(stack).entrySet();
		double lvls = 0;
		for (Entry<Enchantment,Integer> ench : enchList) {
			lvls += ench.getValue();
		}
		int amount = enchList.size();
		// there is no particular reason the constant here was chosen
		// it just happens to make a good curve for this, and is easy to remember
		return (lvls + 2*amount) / Constants.SQRT_PI_E;
	}
	
	default boolean shouldApplyBonus(ItemStack stack) {
		return stack.isEnchanted();
	}
	public double calculateBonus(ItemStack stack);
}
