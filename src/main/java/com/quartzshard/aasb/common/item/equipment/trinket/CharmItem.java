package com.quartzshard.aasb.common.item.equipment.trinket;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CharmItem extends AbilityTrinket {
	public CharmItem(Properties props) {
		super(props);
	}
	
	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (hasAnyRune(stack)) {
			return getRune(stack).passiveAbility(stack, player, level, BindState.PRESSED);
		}
		return false;
	}
}
