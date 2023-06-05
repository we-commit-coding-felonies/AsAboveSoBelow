package com.quartzshard.aasb.api.item;

import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * IBurnoutItem provides an alternative durability system. <br>
 * Default NBT tag is "burnout", stored as int. <br>
 * Minimum burnout is always 0.
 */
public interface IBurnoutItem {
	
	/**
	 * Gets the current amount of burnout.
	 * 
	 * @param stack The itemstack to check
	 * @return the amount
	 */
	default int getBurnout(ItemStack stack) {
		return NBTHelper.Item.getInt(stack, "burnout", 0);
	}
	
	default void setBurnout(ItemStack stack, int amount) {
		NBTHelper.Item.setInt(stack, "burnout", amount);
		//return stack.getOrCreateTag().getInt("burnout");
	}
	
	/**
	 * Define the maximum amount of burnout here.
	 * Should always be > 0 to avoid issues
	 * 
	 * @return The max burnout
	 */
	int getBurnoutMax();
	
	/**
	 * Gets the current amount of burnout, as a percentage of the maximum
	 * 
	 * @param stack The itemstack to check
	 * @return Percentage expressed as a number between 0 and 1
	 */
	default float getBurnoutPercent(ItemStack stack) {
		return Mth.clamp( (float)getBurnout(stack)/(float)getBurnoutMax() , 0f, 1f);
		//return Math.max(0.0f, Math.min(1.0f, getBurnout(stack) / getBurnoutMax()));
	}
}
