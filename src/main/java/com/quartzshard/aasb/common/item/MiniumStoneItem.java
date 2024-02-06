package com.quartzshard.aasb.common.item;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MiniumStoneItem extends Item {
	public MiniumStoneItem(Properties props) {
		super(props);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (level instanceof ServerLevel) {
			byte variant = getVariant(stack);
			if (variant == -1) {
				NBTUtil.setByte(stack, "MiniumVariant", AASB.RNG.nextInt(0, 8));
			}
		}
	}
	
	public byte getVariant(ItemStack stack) {
		return NBTUtil.getByte(stack, "MiniumVariant", -1);
	}

}
