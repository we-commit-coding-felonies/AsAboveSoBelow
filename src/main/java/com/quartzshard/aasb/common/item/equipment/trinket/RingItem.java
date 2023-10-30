package com.quartzshard.aasb.common.item.equipment.trinket;

import com.quartzshard.aasb.common.item.equipment.trinket.rune.RuneTicks;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RingItem extends AbilityTrinket {
	public RingItem(Properties props) {
		super(props);
	}

	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (canUse(stack, player, true)) {
			return getRune(stack, true).utilityAbility(stack, player, level, BindState.PRESSED, isStrong(stack));
		}
		return false;
	}
	
	@Override
	public boolean onPressedFunc2(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (canUse(stack, player, false)) {
			return getRune(stack, false).utilityAbility(stack, player, level, BindState.PRESSED, isStrong(stack));
		}
		return false;
	}

	@Override
	public <R extends TrinketRune> void tickRune(R rune, RuneTicks tInfo, ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong) {
		if (tInfo.utility())
			rune.utilityAbility(stack, player, level, BindState.HELD, strong);
	}
}
