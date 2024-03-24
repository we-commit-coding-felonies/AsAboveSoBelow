package com.quartzshard.aasb.api.item;

import java.util.List;

import com.quartzshard.aasb.client.Keybinds;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * item that breaks any block in a specific amount of ticks <br>
 * so for example, it could mine both obsidian and cobblestone in 5 ticks
 * @author solunareclipse1
 */
public interface IDigStabilizer {
	public static final String TK_DIGSTATE = "DigStabilizerState";
	public static final String TK_DIGSPEED = "DigStabilizerSpeed";
	
	/**
	 * defines how many ticks it takes for the tool to break blocks <br>
	 * speed == 1 is insta-mine, speed <= 0 means use default tool speed calculations
	 * @param stack
	 * @return 
	 */
	default int blockBreakSpeedInTicks(ItemStack stack, BlockState state) {
		return getDigState(stack) ? getDigSpeed(stack) : 0;
	}
	
	/**
	 * Appends a basic "toggle" tooltip, originally designed for use with hermetic tools
	 * @param stack
	 * @param level
	 * @param tips
	 * @param flags
	 */
	default void appendDigToggleText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(LangData.NL);
		@NotNull Component modeKeyText = Keybinds.Bind.ITEMMODE.fLoc();
		@NotNull Component stateText = LangData.tc(getDigState(stack) ? LangData.TIP_GENERIC_ON : LangData.TIP_GENERIC_OFF);
		tips.add(LangData.tc(LangData.TIP_TOOL_STATICDIG_DESC)); // Info
		tips.add(LangData.tc(LangData.TIP_TOOL_STATICDIG_STATE, stateText, modeKeyText)); // Key help
	}
	

	default boolean getDigState(ItemStack stack) {
		return NBTUtil.getBoolean(stack, TK_DIGSTATE, false);
	}
	default void setDigState(ItemStack stack, boolean state) {
		NBTUtil.setBoolean(stack, TK_DIGSTATE, state);
	}
	default void toggleDigState(@NotNull ItemStack stack) {
		setDigState(stack, !getDigState(stack));
	}
	
	default int getDigSpeed(ItemStack stack) {
		return NBTUtil.getInt(stack, TK_DIGSPEED, 0);
	}
	default void setDigSpeed(ItemStack stack, int speed) {
		NBTUtil.setInt(stack, TK_DIGSPEED, speed);
	}
}
