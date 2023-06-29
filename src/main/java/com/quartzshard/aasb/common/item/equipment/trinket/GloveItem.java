package com.quartzshard.aasb.common.item.equipment.trinket;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.init.EffectInit;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public class GloveItem extends AbilityTrinket {
	public GloveItem(Properties props) {
		super(props);
	}

	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (canUse(stack, player, true)) {
			if (getRune(stack, true).combatAbility(stack, player, level, BindState.PRESSED, isStrong(stack))) {
				player.level.playSound(null, player, EffectInit.Sounds.TRINKET_GLOVE.get(), SoundSource.PLAYERS, 1f, 1.7f);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onPressedFunc2(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (canUse(stack, player, false)) {
			if (getRune(stack, false).combatAbility(stack, player, level, BindState.PRESSED, isStrong(stack))) {
				player.level.playSound(null, player, EffectInit.Sounds.TRINKET_GLOVE.get(), SoundSource.PLAYERS, 1f, 1.7f);
				return true;
			}
		}
		return false;
	}
}
