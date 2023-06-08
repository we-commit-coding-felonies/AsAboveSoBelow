package com.quartzshard.aasb.api.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * item that breaks any block in a specific amount of ticks <br>
 * so for example, it could mine both obsidian and cobblestone in 5 ticks
 * @author solunareclipse1
 */
public interface IStaticSpeedBreaker {
	public static final String TAG_BREAKSPEED = "static_break_speed";
	
	/**
	 * defines how many ticks it takes for the tool to break blocks <br>
	 * speed == 1 is insta-mine, speed <= 0 means use default tool speed calculations
	 * @param stack
	 * @return 
	 */
	public int blockBreakSpeedInTicks(ItemStack stack, BlockState state);
}
