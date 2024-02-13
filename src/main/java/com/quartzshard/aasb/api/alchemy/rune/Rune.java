package com.quartzshard.aasb.api.alchemy.rune;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Multimap;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public abstract class Rune {
	public static final String
		TK_ACTIVATED = "TrinketActive";
	
	public abstract MutableComponent loc();
	public abstract MutableComponent fLoc();
	
	public abstract int color();
	
	/**
	 * Serialize this Rune to a String.
	 * @return A string representing this Rune
	 */
	@SuppressWarnings("null") // Attempting to serialize an unregistered rune is bad
	public String serialize() {
		return AlchInit.RUNES_SUPPLIER.get().getKey(this).toString();
	}
	
	/**
	 * Deserializes a rune from a String
	 * @param dat
	 * @return Rune
	 */
	@Nullable
	public static Rune deserialize(String dat) {
		if (dat == "null") return null;
		return AlchInit.RUNES_SUPPLIER.get().getValue(ResourceLocation.tryParse(dat));
	}
	
	/**
	 * Triggers this rune's combat ability, called by gloves
	 * @param stack The ItemStack activating this ability
	 * @param player The ServerPlayer with the stack
	 * @param level The ServerLevel that the player is in
	 * @param state Whether the button was pressed or released
	 * @param strong True if this ability has been powered up
	 * @return True if the ability was activated, false otherwise
	 */
	public abstract boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot);
	
	/**
	 * Triggers this rune's utility ability, called by bracelets
	 * @param stack The ItemStack activating this ability
	 * @param player The ServerPlayer with the stack
	 * @param level The ServerLevel that the player is in
	 * @param state Whether the button was pressed or released
	 * @param strong True if this ability has been powered up
	 * @return True if the ability was activated, false otherwise
	 */
	public abstract boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot);
	
	/**
	 * Triggers this rune's active passive ability, called by charms when the keybind is pressed <br>
	 * Usually this is used to toggle passive effects on/off
	 * @param stack The ItemStack activating this ability
	 * @param player The ServerPlayer with the stack
	 * @param level The ServerLevel that the player is in
	 * @param state Whether the button was pressed or released
	 * @param strong True if this ability has been powered up
	 * @return True if the ability was activated, false otherwise
	 */
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state != BindState.PRESSED) return false;
		togglePassive(stack);
		return true;
	}

	public boolean passiveEnabled(ItemStack stack) {
		return NBTUtil.getBoolean(stack, TK_ACTIVATED, false);
	}
	public void togglePassive(ItemStack stack) {
		NBTUtil.setBoolean(stack, TK_ACTIVATED, !passiveEnabled(stack));
	}
	
	/**
	 * Triggers this rune's active passive ability, called by charms EVERY TICK <br>
	 * This is also called one final time when unequipped, so that effects can be properly turned off if necessary
	 * @param stack The ItemStack activating this ability
	 * @param player The ServerPlayer with the stack
	 * @param level The ServerLevel that the player is in
	 * @param state Whether the button was pressed or released
	 * @param strong True if this ability has been powered up
	 * @param unequipped True if this is the final call due to being unequipped. Clean up after yourself!
	 * @return True if the ability was activated, false otherwise
	 */
	public abstract void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped);

	/**
	 * Called by the curios to get attrib modifiers. All params are passed directly from curio counterpart <br>
	 * @return Multimap of modifiers for this rune, or null for none / default 
	 */
	@Nullable
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext ctx, UUID uuid, ItemStack stack) {
		return null;
	}
}
