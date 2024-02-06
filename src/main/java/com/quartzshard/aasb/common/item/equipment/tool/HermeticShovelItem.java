package com.quartzshard.aasb.common.item.equipment.tool;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Multimap;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.api.item.IHermeticTool;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HermeticShovelItem extends ShovelItem implements IHermeticTool {
	public HermeticShovelItem(int damage, float speed, Properties props) {
		super(Tier.HERMETIC, damage, speed, props);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flags) {/*
		super.appendHoverText(stack, level, tips, flags);
		
		int runesVal = getRunesVal(stack);
		if (validateRunes(runesVal) && runesVal > 0) {
			appendRuneText(stack, level, tips, flags);
			if (ClientUtil.shiftHeld()) {
				if (runesVal > 1) {
					if (hasRune(runesVal, ShapeRune.WATER)) {
						tips.add(AASBLang.NL);
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_PICKAXE_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_PICKAXE_DESC, AASBKeys.Bind.ITEMFUNC_1.fLoc()));
					} else if (hasRune(runesVal, ShapeRune.FIRE)) {
						appendEnchText(stack, level, tips, flags);
					}
					
					if (hasRune(runesVal, ShapeRune.EARTH)) {
						appendDigStabilizerText(stack, level, tips, flags);
					}
				}
				appendEmpowerText(stack, level, tips, flags);
			} else {
				LangData.appendMoreInfoText(stack, level, tips, flags);
			}
			//tips.add(AASBLang.NL);
		}*/
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack s, int a, T e, Consumer<T> b) {return 0;}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float sup = super.getDestroySpeed(stack, state);
		float calced = calcDestroySpeed(stack, sup);
		return calced;
	}
	
	@Override
	public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return enchAttribMods(slot, stack, super.getAttributeModifiers(slot, stack));
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return stacksDifferentIgnoreWay(oldStack, newStack);
    }
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (slotChanged) return true;
		return stacksDifferentIgnoreWay(oldStack, newStack);
	}

	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		tickEmpower(stack, entity);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		Rune major = this.getMajorRune(stack);
		if (major instanceof ToolRune tr) {
			return tr.isEnchantable();
		}
		return false;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getStoredWay(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return empowerBarWidth(stack);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return empowerBarColor(stack);
	}

	@Override
	public ToolStyle getToolStyle(ItemStack stack) {
		return ToolStyle.SHOVEL;
	}
}
