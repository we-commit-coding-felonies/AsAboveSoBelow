package com.quartzshard.aasb.common.item.equipment.tool;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Multimap;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.api.alchemy.rune.shape.WaterRune;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.client.Keybinds;
import com.quartzshard.aasb.common.item.ItemTraits.Tier;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HermeticHoeItem extends HoeItem implements IHermeticTool {
	public HermeticHoeItem(int damage, float speed, Properties props) {
		super(Tier.HERMETIC, damage, speed, props);
	}
	
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		
		int runesVal = IHermeticTool.getRunesVal(stack);
		if (IHermeticTool.validateRunesVal(runesVal) && runesVal > 0) {
			appendRuneText(stack, level, tips, flags);
			if (ClientUtil.shiftHeld()) {
				if (runesVal > 1) {
					if (hasRune(stack, AlchInit.RUNE_WATER.get())) {
						tips.add(LangData.NL);
						tips.add(LangData.tc(LangData.TIP_HOE_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
						byte mode = NBTUtil.getByte(stack, WaterRune.TK_HYPERSICKLE, (byte) 0);
						tips.add(LangData.tc(LangData.TIP_HOE_DESC, Keybinds.Bind.ITEMFUNC_1.fLoc(), WaterRune.opLangLong(mode), Keybinds.Bind.ITEMFUNC_2.fLoc()));
					} else if (hasRune(stack, AlchInit.RUNE_FIRE.get())) {
						appendEnchText(stack, level, tips, flags, runesAreStrong(stack));
					}
					
					if (hasRune(stack, AlchInit.RUNE_EARTH.get())) {
						appendDigToggleText(stack, level, tips, flags);
					}
				}
				appendEmpowerText(stack, level, tips, flags);
			} else {
				LangData.appendMoreInfoText(stack, level, tips, flags);
			}
			tips.add(LangData.NL);
		}
	}

	@Override
	public boolean handle(PressContext ctx) {
		switch (ctx.bind()) {
			case ITEMFUNC_2:
				if (ctx.state() != BindState.PRESSED || !hasRune(ctx.stack(), AlchInit.RUNE_WATER.get()))
					return false;
				return WaterRune.changeOperation(ctx.player(), ctx.stack());
			default:
				return IHermeticTool.super.handle(ctx);
		}
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
	public float calcDestroySpeed(ItemStack stack, float baseSpeed) {
		float speed = baseSpeed;
		if (speed > 1) {
			double bonus = calculateBonus(stack) / 10d;
			if (bonus > 0 && shouldApplyBonus(stack)) {
				speed += speed*bonus;
			}
		}
		return speed;
	}
	
	@Override
	public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot, @NotNull ItemStack stack) {
		return enchAttribMods(slot, stack, super.getAttributeModifiers(slot, stack));
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return stacksDifferentIgnoreWay(oldStack, newStack);
    }
	
	@Override
	public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (slotChanged) return true;
		return stacksDifferentIgnoreWay(oldStack, newStack);
	}

	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		tickEmpower(stack, entity);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		Rune minor = this.getMinorRune(stack);
		if (minor instanceof ToolRune tr) {
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
	public @NotNull ToolStyle getToolStyle(ItemStack stack) {
		return ToolStyle.HOE;
	}
}
