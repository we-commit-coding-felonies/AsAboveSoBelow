package com.quartzshard.aasb.api.alchemy.rune;

import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public abstract class ToolRune extends Rune {
	
	/**
	 * Checks if this rune will accept enchantments for the tool
	 * @return True if this rune is enchantable
	 */
	public abstract boolean isEnchantable();
	
	/**
	 * Checks if this rune has an acivated ability for tools
	 * @return True if this rune has a tool ability
	 */
	public abstract boolean hasToolAbility();
	
	/**
	 * Checks if this rune is considered "major" when applied on tools <br>
	 * Hermetic tools can only have 1 major and 1 minor rune.
	 * @return True if this rune is considered major, false if minor
	 */
	public abstract boolean isMajorToolRune();

	/**
	 * Triggers this rune's tool ability, called by runeable tools (Hermetic)
	 * @param stack The ItemStack activating this ability
	 * @param player The ServerPlayer with the stack
	 * @param level The ServerLevel that the player is in
	 * @param state Whether the button was pressed or released
	 * @param strong True if this ability has been powered up
	 * @return True if the ability was activated, false otherwise
	 * @apiNote This defaults to
	 */
	public abstract boolean toolAbility(ItemStack stack, ToolStyle style, ServerPlayer player, ServerLevel level, BindState state, boolean strong);
	
	public enum ToolStyle {
		SWORD, PICKAXE, SHOVEL, AXE, HOE
	}
}
