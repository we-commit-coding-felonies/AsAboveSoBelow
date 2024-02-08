package com.quartzshard.aasb.api.alchemy.rune.shape;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Yes, this is technically not a ShapeRune. Fuck you. <br>
 * Does nothing but exist as a flag for other runes to be strong
 */
public class QuintessenceRune extends Rune {
	
	@Override
	public MutableComponent loc() {
		return ShapeAspect.QUINTESSENCE.loc();
	}
	@Override
	public MutableComponent fLoc() {
		return ShapeAspect.QUINTESSENCE.fLoc();
	}
	@Override
	public int color() {
		return ShapeAspect.QUINTESSENCE.color;
	}
	

	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}
	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
	}
}
