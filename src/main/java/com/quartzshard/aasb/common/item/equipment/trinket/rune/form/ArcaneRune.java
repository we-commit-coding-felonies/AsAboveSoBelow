package com.quartzshard.aasb.common.item.equipment.trinket.rune.form;

import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ArcaneRune extends TrinketRune {

	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		// transmutation attack
		return true;
	}

	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		// time accel
		return false;
	}

	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		// gem of density???
		return false;
	}
}