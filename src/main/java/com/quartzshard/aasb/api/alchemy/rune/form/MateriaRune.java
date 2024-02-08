package com.quartzshard.aasb.api.alchemy.rune.form;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * This rune allows the user to transmute their trinket into another
 */
public class MateriaRune extends FormRune {
	public MateriaRune() {
		super(AASB.rl("materia"));
	}

	/**
	 * Transform into Bracelet
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Transform into Charm
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Transform into Glove
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {}

}
