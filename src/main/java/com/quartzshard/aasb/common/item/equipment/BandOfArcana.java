package com.quartzshard.aasb.common.item.equipment;

import com.quartzshard.aasb.api.item.bind.ICanEmpower;
import com.quartzshard.aasb.common.damage.source.AASBDmgSrc;
import com.quartzshard.aasb.util.EntityHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BandOfArcana extends Item implements ICanEmpower {
	public BandOfArcana(Properties props) {
		super(props);
	}
	@Override
	public boolean onEmpowerPressed(ItemStack stack, ServerPlayer player, ServerLevel level) {
		level.playSound(null, player.blockPosition(), SoundEvents.MUSIC_DISC_WARD, SoundSource.MASTER, 1, 1);
		return true;
	}
	
	@Override
	public boolean onEmpowerHeld(ItemStack stack, ServerPlayer player, ServerLevel level) {
		level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_BIT, SoundSource.MASTER, 1, 1);
		return false;
	}
	
	@Override
	public boolean onEmpowerReleased(ItemStack stack, ServerPlayer player, ServerLevel level) {
		EntityHelper.hurtNoDamI(player, DamageSource.OUT_OF_WORLD, -1);
		return false;
	}
}
