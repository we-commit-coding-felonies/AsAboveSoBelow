package com.quartzshard.aasb.api.item;

import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.api.alchemy.rune.shape.AirRune;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IRuneableArmor extends IRuneable {
	
	@Override
	default boolean handle(@NotNull PressContext ctx) {
		// rune armor doesnt handle keybinds
		return false;
	}
	
	@Nullable
	default Rune getMajorRune(@NotNull ItemStack stack) {
		return getRune(stack, 0);
	}
	
	@Nullable
	default Rune getMinorRune(@NotNull ItemStack stack) {
		return getRune(stack, 1);
	}
	
	@Override
	default boolean runesAreStrong(@NotNull ItemStack stack) {
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
		return ItemAbility.NONE;
	}
	
	@Override @Nullable
	default Item getMateriaRuneTarget(ItemStack stack) {
		return null;
	}
}
