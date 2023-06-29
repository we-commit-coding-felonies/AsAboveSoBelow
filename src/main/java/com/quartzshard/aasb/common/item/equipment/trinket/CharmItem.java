package com.quartzshard.aasb.common.item.equipment.trinket;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CharmItem extends AbilityTrinket {
	public CharmItem(Properties props) {
		super(props);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (entity instanceof ServerPlayer plr && level instanceof ServerLevel lvl) {
			boolean strong = isStrong(stack);
			if (canUse(stack, plr, true)) {
				getRune(stack, true).passiveAbility(stack, plr, lvl, BindState.HELD, strong);
			}
			if (canUse(stack, plr, false)) {
				getRune(stack, false).passiveAbility(stack, plr, lvl, BindState.HELD, strong);
			}
		}
	}

	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (canUse(stack, player, true)) {
			return getRune(stack, true).passiveAbility(stack, player, level, BindState.PRESSED, isStrong(stack));
		}
		return false;
	}
	
	@Override
	public boolean onPressedFunc2(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (canUse(stack, player, false)) {
			return getRune(stack, false).passiveAbility(stack, player, level, BindState.PRESSED, isStrong(stack));
		}
		return false;
	}
}
