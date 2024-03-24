package com.quartzshard.aasb.api.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune;
import com.quartzshard.aasb.api.item.bind.IHandleKeybind;
import com.quartzshard.aasb.common.item.equipment.curio.AbilityCurioItem;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Items that can receive runes via Projection
 */
public interface IRuneable extends IHandleKeybind {
	public static final String
		TK_RUNES = "InscribedRunes";
	
	@Override
	default boolean handle(PressContext ctx) {
		String slot = null;
		switch (ctx.bind()) {
			case GLOVE:
				slot = "hands";
				break;
			case BRACELET:
				slot = "bracelet";
				break;
			case CHARM:
				slot = "charm";
				break;
			
			default: // this is so we dont handle things unless we are equipped
				return false;
		}
		ItemStack stack = ctx.stack();
		ServerPlayer player = ctx.player();
		@Nullable Rune rune;
		if (this.getMaxRunes(stack) == 1) {
			rune = getInscribedRunes(stack).get(0);
		} else {
			rune = getInscribedRunes(stack).get(player.isShiftKeyDown() ? 1 : 0);
			if (rune == null) // Null rune, lets try the other one...
				rune = getInscribedRunes(stack).get(player.isShiftKeyDown() ? 0 : 1);
		}
		if (rune == null) // Thats no good either
			return false; // Nothing to do, return false
		boolean strong = runesAreStrong(stack);
		switch (getAbility(stack)) {
			case COMBAT:
				if (rune.combatAbility(stack, player, ctx.level(), ctx.state(), strong, slot)) {
					ctx.level().playSound(null, player.blockPosition(), FxInit.SND_TRINKET_GLOVE.get(), player.getSoundSource(), 1f, AASB.RNG.nextFloat(1, 2));
					return true;
				}
				break;
			case UTILITY:
				if (rune.utilityAbility(stack, player, ctx.level(), ctx.state(), strong, slot)) {
					ctx.level().playSound(null, player.blockPosition(), FxInit.SND_TRINKET_RING.get(), player.getSoundSource(), 1f, AASB.RNG.nextFloat(1, 2));
					return true;
				}
				break;
			case PASSIVE:
				if (rune.passiveAbility(stack, player, ctx.level(), ctx.state(), strong, slot)) {
					ctx.level().playSound(null, player.blockPosition(), FxInit.SND_TRINKET_CHARM.get(), player.getSoundSource(), 1f, AASB.RNG.nextFloat(1, 2));
					return true;
				}
				break;
			default:
				break;
		}
		return false;
	}

	default void tickRunes(@NotNull ItemStack stack, ServerPlayer player, ServerLevel level, boolean unequip) {
		for (@Nullable Rune rune : getInscribedRunes(stack)) {
			if (rune != null) {
				rune.tickPassive(stack, player, level, runesAreStrong(stack), unequip);
			}
		}
	}
	default void tickRunesClient(ItemStack stack, Player player, Level level, boolean unequip) {
		for (@Nullable Rune rune : getInscribedRunes(stack)) {
			if (rune != null) {
				rune.tickPassiveClient(stack, player, level, runesAreStrong(stack), unequip);
			}
		}
	}
	
	/**
	 * @param stack
	 * @return The maximum amount of runes this item can be inscribed with
	 */
	default int getMaxRunes(ItemStack stack) {
		return 2;
	}
	
	default boolean isInscribed(ItemStack stack) {
		return getRune(stack, 0) != null;
	}
	
	/**
	 * Gets all the runes currently inscribed on this item
	 * @param stack
	 * @return List of Runes
	 */
	default List<Rune> getInscribedRunes(ItemStack stack) {
		@Nullable ListTag tags = NBTUtil.getList(stack, TK_RUNES, 8, false);
		if (tags.isEmpty()) {
			for (int i = 0; i < getMaxRunes(stack); i++) {
				tags.add(StringTag.valueOf("null"));
			}
			NBTUtil.setList(stack, TK_RUNES, tags);
		}
		@NotNull List<Rune> runes = new ArrayList<>(getMaxRunes(stack));
		for (Tag tag : tags) {
			runes.add(Rune.deserialize(tag.getAsString()));
		}
		return runes;
	}
	
	@Nullable
	default Rune getRune(ItemStack stack, int slot) {
		@NotNull List<Rune> runes = getInscribedRunes(stack);
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
	
	default boolean canInscribeRune(Rune rune, @NotNull ItemStack stack, int slot) {
		if (slot+1 <= getMaxRunes(stack)) {
			@Nullable Rune currentRune = getRune(stack, slot);
			if (currentRune == null) {
				return true;
			}
		}
		return false;
	}
	
	default void inscribeRune(Rune rune, ItemStack stack, int slot) {
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
	 * Gets the tagret item for when this is transmuted by an inscribed Materia rune
	 * @return Item to morph into, or null if this item does not morph
	 */
	@Nullable
	Item getMateriaRuneTarget(ItemStack stack);
	
	/**
	 * Gets what type of rune ability the item should trigger
	 * @param stack
	 * @return
	 */
	ItemAbility getAbility(ItemStack stack);
	
	enum ItemAbility {
		COMBAT(LangData.ITEM_GLOVE_RUNED),
		UTILITY(LangData.ITEM_BRACELET_RUNED),
		PASSIVE(LangData.ITEM_CHARM_RUNED),
		TOOL(null), // :troled:
		NONE(null); // :troled: 2 electric boogaloo
		
		@Nullable
		public final String runedLang;
		ItemAbility(String runedLang) {
			this.runedLang = runedLang;
		}
	}

	/**
	 * Appends rune information to the tooltip
	 * @param stack
	 * @param level
	 * @param tips
	 * @param flags
	 */
	default void appendRuneText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		@Nullable Component runeText = null;
		@Nullable Rune major = getRune(stack, 0), minor = getRune(stack, 1);
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
