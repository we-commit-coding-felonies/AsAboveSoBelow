package com.quartzshard.aasb.api.alchemy.rune.shape;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class AirRune extends ShapeRune {

	public AirRune() {
		super(ShapeAspect.AIR);
	}

	/**
	 * Normal: Smite <br>
	 * Strong: UNNNNLIMITED POWWWEERRRRR (chain lightning)
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Gust self <br>
	 * Strong: Absurd gust self + nearby
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Gem glide <br>
	 * Strong: Creative flight
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}
	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong) {
		// TODO Auto-generated method stub
	}

	/*
	 * This rune doesnt itself do much with a tool,
	 * but makes whatever major rune it is applied with much more powerful
	 */
	
	@Override
	public boolean isEnchantable() {
		return false;
	}

	@Override
	public boolean hasToolAbility() {
		return false;
	}

	@Override
	public boolean isMajorToolRune() {
		return false;
	}

	@Override
	public boolean toolAbility(ItemStack stack, ToolStyle style, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

}
