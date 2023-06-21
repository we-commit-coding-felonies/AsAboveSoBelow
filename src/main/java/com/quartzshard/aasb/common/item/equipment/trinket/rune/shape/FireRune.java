package com.quartzshard.aasb.common.item.equipment.trinket.rune.shape;

import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FireRune extends TrinketRune {

	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level) {
		System.out.println("fire combat");
		return false;
	}

	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level) {
		System.out.println("fire utility");
		return false;
	}

	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level) {
		System.out.println("fire passive");
		return false;
	}

}
