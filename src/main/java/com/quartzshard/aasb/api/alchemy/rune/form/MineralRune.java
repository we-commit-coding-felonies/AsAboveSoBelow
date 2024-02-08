package com.quartzshard.aasb.api.alchemy.rune.form;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class MineralRune extends FormRune {

	public MineralRune() {
		super(AASB.rl("mineral"));
	}

	/**
	 * normal: resistance effect <br>
	 * strong: turn self to bedrock (immobile, immune to damage)
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: divining rod <br>
	 * strong: silk-touch veinmine
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: +1 fortune <br>
	 * strong: +3 fortune
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
