package com.quartzshard.aasb.api.item;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune;
import com.quartzshard.aasb.api.item.bind.IHandleKeybind;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Items that can receive runes via Projection
 */
public interface IRuneable extends IHandleKeybind {
	public static final String
		TK_RUNES = "InscribedRunes";
	
	@Override
	default boolean handle(PressContext ctx) {
		switch (ctx.bind()) {
			case GLOVE:
			case BRACELET:
			case CHARM:
				break;
			
			default: // this is so we dont handle things unless we are equipped
				return false;
		}
		ItemStack stack = ctx.stack();
		ServerPlayer player = ctx.player();
		@Nullable Rune rune = getInscribedRunes(stack).get(player.isShiftKeyDown() ? 1 : 0);
		if (rune == null) // Null rune, lets try the other one...
			rune = getInscribedRunes(stack).get(player.isShiftKeyDown() ? 0 : 1);
		if (rune == null) // Thats no good either
			return false; // Nothing to do, return false
		boolean strong = runesAreStrong(stack);
		switch (getAbility(stack)) {
			case COMBAT:
				return rune.combatAbility(stack, player, ctx.level(), ctx.state(), strong);
			case UTILITY:
				return rune.utilityAbility(stack, player, ctx.level(), ctx.state(), strong);
			case PASSIVE:
				return rune.passiveAbility(stack, player, ctx.level(), ctx.state(), strong);
			default:
				return false;
		}
	}
	
	/**
	 * @param stack
	 * @return The maximum amount of runes this item can be inscribed with
	 */
	default int getMaxRunes(ItemStack stack) {
		return 2;
	}
	
	/**
	 * Gets all the runes currently inscribed on this item
	 * @param stack
	 * @return List of Runes
	 */
	default List<Rune> getInscribedRunes(ItemStack stack) {
		ListTag tags = NBTUtil.getList(stack, TK_RUNES, 8, false);
		if (tags.isEmpty()) {
			for (int i = 0; i < getMaxRunes(stack); i++) {
				tags.add(StringTag.valueOf("null"));
			}
			NBTUtil.setList(stack, TK_RUNES, tags);
		}
		List<Rune> runes = new ArrayList<>(getMaxRunes(stack));
		for (Tag tag : tags) {
			runes.add(Rune.deserialize(tag.getAsString()));
		}
		return runes;
	}
	
	@Nullable
	default Rune getRune(ItemStack stack, int slot) {
		List<Rune> runes = getInscribedRunes(stack);
		if (runes.size() == 0) return null;
		return runes.get(slot);
	}
	
	default boolean hasRune(ItemStack stack, Rune rune) {
		List<Rune> runes = getInscribedRunes(stack);
		for (Rune found : runes) {
			if (found == rune) {
				return true;
			}
		}
		return false;
	}
	
	default boolean canInscribeRune(Rune rune, ItemStack stack, int slot) {
		if (slot+1 <= getMaxRunes(stack)) {
			Rune currentRune = getRune(stack, slot);
			if (currentRune == null) {
				return true;
			}
		}
		return false;
	}
	
	default void inscribeRune(Rune rune, ItemStack stack, int slot) {
		// FIXME doesnt work
		if (canInscribeRune(rune, stack, slot)) {
			ListTag tags = NBTUtil.getList(stack, TK_RUNES, 8, false);
			if (tags.isEmpty()) {
				for (int i = 0; i < getMaxRunes(stack); i++) {
					tags.add(StringTag.valueOf("null"));
				}
				NBTUtil.setList(stack, TK_RUNES, tags);
			}
			tags.set(slot, StringTag.valueOf(rune.serialize()));
		}
	}
	
	/**
	 * Defines whether runes should use their "strong" abilities instead of their normal ones
	 * @param stack
	 * @return True if runes should be strong
	 */
	boolean runesAreStrong(ItemStack stack);
	
	/**
	 * Gets what type of rune ability the item should trigger
	 * @param stack
	 * @return
	 */
	ItemAbility getAbility(ItemStack stack);
	
	enum ItemAbility {
		COMBAT, UTILITY, PASSIVE, TOOL;
	}
}
