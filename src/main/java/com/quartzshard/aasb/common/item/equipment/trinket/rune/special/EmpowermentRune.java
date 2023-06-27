package com.quartzshard.aasb.common.item.equipment.trinket.rune.special;

import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * special rune <br>
 * makes the other inscribed rune more powerful
 */
public class EmpowermentRune extends TrinketRune {
	
	private static final String TAG_BOOSTED = "IsBoosted";

	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		setBoost(stack, !strong);
		return true;
	}

	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		setBoost(stack, !strong);
		return true;
	}

	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		setBoost(stack, !strong);
		return true;
	}

	public void setBoost(ItemStack stack, boolean boost) {
		NBTHelper.Item.setBoolean(stack, TAG_BOOSTED, boost);
	}
	
	public static boolean hasBoost(ItemStack stack) {
		return NBTHelper.Item.getBoolean(stack, TAG_BOOSTED, true);
	}

}
