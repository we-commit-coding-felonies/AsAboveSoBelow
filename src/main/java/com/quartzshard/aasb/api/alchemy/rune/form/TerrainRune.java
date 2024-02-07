package com.quartzshard.aasb.api.alchemy.rune.form;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class TerrainRune extends FormRune {

	public TerrainRune() {
		super(AASB.rl("terrain"));
	}

	/**
	 * normal: 2d worldedit //set <br>
	 * strong: 3d worldedit //set
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: 2d worldedit //replace <br>
	 * strong: 3d worldedit //replace
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: aqua + aerial affinity <br>
	 * strong: walk on air? should be distinct from Air rune flight
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong) {
		// TODO Auto-generated method stub
	}

}
