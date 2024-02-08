package com.quartzshard.aasb.api.alchemy.rune.form;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ArcaneRune extends FormRune {

	public ArcaneRune() {
		super(AASB.rl("arcane"));
	}

	/**
	 * normal: instant damage touch <br>
	 * strong: transmuting touch
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal" teleport <br>
	 * strong: jojo reference
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * an exception to the charm stuff being passives <br>
	 * normal: portable crafting table <br>
	 * strong: portable transmutation
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		// TODO Auto-generated method stub
	}

}
