package com.quartzshard.aasb.common.item.equipment.trinket;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.item.ITrinket;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.init.ObjectInit.TrinketRunes;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RingItem extends AbilityTrinket {
	public RingItem(Properties props) {
		super(props);
	}
	
	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (hasAnyRune(stack)) {
			return getRune(stack).utilityAbility(stack, player, level, BindState.PRESSED);
		}
		return false;
	}
}
