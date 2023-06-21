package com.quartzshard.aasb.common.item.equipment.trinket;

import com.quartzshard.aasb.api.item.ITrinket;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RingItem extends Item implements ITrinket {
	public RingItem(Properties props) {
		super(props);
	}
	
	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		return false;
	}
	
	
}
