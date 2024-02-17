package com.quartzshard.aasb.api.item;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.util.MathUtil.Constants;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

/**
 * Item that recieves boosts based on the enchantments it has
 */
public interface IEnchantBoostable {
	default void appendEnchText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags, boolean strong) {
		// strong = hasRune(stack, ShapeRune.AIR)
		tips.add(LangData.NL);
		tips.add(LangData.tc(LangData.TIP_TOOL_ENCHBONUS_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
		tips.add(LangData.tc(LangData.TIP_TOOL_ENCHBONUS_DESC));
		double maxPow = getBonusStrength(stack);
		if (strong) maxPow *= Math.PI + Math.E;
		tips.add(LangData.tc(LangData.TIP_TOOL_ENCHBONUS_VAL, ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(maxPow)));
	}
	
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
	
	double calculateBonus(ItemStack stack);
}
