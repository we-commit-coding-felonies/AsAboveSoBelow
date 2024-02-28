package com.quartzshard.aasb.common.item.equipment;

import com.quartzshard.aasb.api.item.IWayHolder;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

public class WaystoneItem extends Item implements IWayHolder {
	public static final String TK_CURINSERTED = "CurrentWayInserted";

	public WaystoneItem(Properties props) {
		super(props);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, InteractionHand hand) {
		if (!FMLEnvironment.production) {
			// debug code to give lots of way
			ItemStack stack = player.getItemInHand(hand);
			long todo = player.isShiftKeyDown() ? Long.MAX_VALUE : player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).getCount();
			long did;
			boolean insert = canInsertWay(stack);
			if (canInsertWay(stack)) {
				did = insertWay(stack, todo);
			} else {
				did = extractWay(stack, todo);
			}
			Logger.chat("WaystoneItem.use()", "WayOperationValue", did+" Way was " + (insert ? "INSERTED" : "EXTRACTED"), player);
			return InteractionResultHolder.consume(stack);
		}
		return super.use(level, player, hand);
	}

	@Override
	public long getMaxWay(ItemStack stack) {
		// inb4 this causes problems later
		long curIns = getCurInserted(stack);
		return curIns > 0 ? curIns : Long.MAX_VALUE;
	}
	
	@Override
	public long insertWay(ItemStack stack, long amount) {
		long inserted = IWayHolder.super.insertWay(stack, amount);
		if (inserted > 0) setCurInserted(stack, inserted);
		return inserted;
	}
	
	@Override
	public long extractWay(ItemStack stack, long amount) {
		long extracted = IWayHolder.super.extractWay(stack, amount);
		if (extracted > 0 && getStoredWay(stack) == 0) setCurInserted(stack, 0);
		return extracted;
	}

	public long getCurInserted(ItemStack stack) {
		return NBTUtil.getLong(stack, TK_CURINSERTED, 0);
	}
	
	public void setCurInserted(ItemStack stack, long li) {
		NBTUtil.setLong(stack, TK_CURINSERTED, li);
	}

}
