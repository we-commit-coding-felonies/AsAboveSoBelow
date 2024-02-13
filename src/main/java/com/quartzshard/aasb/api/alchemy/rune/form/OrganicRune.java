package com.quartzshard.aasb.api.alchemy.rune.form;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class OrganicRune extends FormRune {

	public OrganicRune() {
		super(AASB.rl("organic"));
	}

	/**
	 * normal: super poison touch <br>
	 * strong: wither vine
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: bonemeal <br>
	 * strong: area bonemeal
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: solar stat boost <br>
	 * strong: stronger boost and +10 hearts
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		// TODO Auto-generated method stub
	}

}
