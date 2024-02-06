package com.quartzshard.aasb.api.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.api.alchemy.rune.shape.AirRune;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public interface IRuneableTool extends IRuneable {
	
	@Override
	default boolean handle(PressContext ctx) {
		ItemStack stack = ctx.stack();
		ServerPlayer player = ctx.player();
		@Nullable Rune rune;
		switch (ctx.bind()) {
			case ITEMMODE:
				rune = getMinorRune(stack);
				break;
			case ITEMFUNC_1:
			case ITEMFUNC_2:
				rune = getMajorRune(stack);
				break;
				
			default:
				return false;
		}
		if (rune == null)
			return false; // Nothing to do, return false
		boolean strong = runesAreStrong(stack);
		if (rune instanceof ToolRune tr && tr.hasToolAbility())
			return tr.toolAbility(stack, getToolStyle(stack), player, ctx.level(), ctx.state(), strong);
		return false;
	}
	
	@Nullable
	default Rune getMajorRune(ItemStack stack) {
		return getRune(stack, 0);
	}
	
	@Nullable
	default Rune getMinorRune(ItemStack stack) {
		return getRune(stack, 1);
	}
	
	@Override
	default boolean runesAreStrong(ItemStack stack) {
		return getMinorRune(stack) instanceof AirRune;
	}
	
	@Override
	default boolean canInscribeRune(Rune rune, ItemStack stack, int slot) {
		if (rune instanceof ToolRune tr) {
			return slot == 0 && tr.isMajorToolRune()
					|| slot == 1 && !tr.isMajorToolRune();
		}
		return false;
	}

	@Override
	default ItemAbility getAbility(ItemStack stack) {
		return ItemAbility.TOOL;
	}
	
	ToolStyle getToolStyle(ItemStack stack);
	
	/**
	 * Appends rune information to the tooltip
	 * @param stack
	 * @param level
	 * @param tips
	 * @param flags
	 */
	default void appendRuneText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		Component runeText = null;
		Rune major = getMajorRune(stack), minor = getMinorRune(stack);
		boolean hasNull = major == null || minor == null,
				didDo = false; 
		if (hasNull && major != minor) {
			// only one is null, only 1 rune
			runeText = LangData.tc(LangData.TIP_RUNE, major == null ? minor.fLoc() : major.fLoc()).copy().withStyle(ChatFormatting.GRAY);
			didDo = true;
		} else if (!hasNull) {
			// neither is null, we have both
			runeText = LangData.tc(LangData.TIP_RUNE_MULTI,
					major.fLoc(),
					minor.fLoc()
			).copy().withStyle(ChatFormatting.GRAY);
			didDo = true;
		}
		if (didDo)
			tips.add(runeText);
	}
}
