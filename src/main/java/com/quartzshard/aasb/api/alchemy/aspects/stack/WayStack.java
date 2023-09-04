package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectWay;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/**
 * @implNote Value is different from Amount, and specific to WayStacks <br>
 * 			 WayStack stores 2 longs: <ul>
 * 				<li> AMOUNT: the number of AspectWays in the stack
 * 				<li> VALUE: the Way value of each AspectWay</ul>
 * 			 You should treat Value like you would treat the AspectForm of a FormStack
 */
public class WayStack extends AspectStack<AspectWay> {
	public static final String TYPE_KEY = "way";
	public WayStack(AspectWay aspect, long amount) {
		super(TYPE_KEY, aspect, amount);
	}
	public WayStack(long value, long amount) {
		this(new AspectWay(value), amount);
	}
	public WayStack(long value) {
		this(value, 1);
	}


	/**
	 * @deprecated Only for WayStacks, please use setValue() instead
	 */
	@Override
	@Deprecated
	public boolean setAspect(AspectWay aspect) {
		if (this.aspect.getValue() != aspect.getValue()) {
			this.aspect = aspect;
			return true;
		}
		return false;
	}

	/**
	 * @implNote Value is different from Amount, and specific to WayStacks <br>
	 * 			 WayStack stores 2 longs: <ul>
	 * 				<li> AMOUNT: the number of AspectWays in the stack
	 * 				<li> VALUE: the Way value of each AspectWay</ul>
	 * 			 You should treat Value like you would treat the AspectForm of a FormStack
	 */
	public long getValue() {
		return aspect.getValue();
	}
	
	/**
	 * @return if the change was successful
	 * 
	 * @implNote Value is different from Amount, and specific to WayStacks <br>
	 * 			 WayStack stores 2 longs: <ul>
	 * 				<li> AMOUNT: the number of AspectWays in the stack
	 * 				<li> VALUE: the Way value of each AspectWay</ul>
	 * 			 You should treat Value like you would treat the AspectForm of a FormStack
	 */
	public boolean setValue(long value) {
		if (this.aspect.getValue() != value) {
			return setAspect(new AspectWay(value));
		}
		return false;
	}
	
	/**
	 * @param aspectTag
	 * @return WayStack from NBT, or null if invalid
	 */
	@Nullable
	public static WayStack deserialize(CompoundTag aspectTag) {
		String dat = aspectTag.getString(ASPECT_KEY);
		AspectWay aspect = AspectWay.deserialize(dat);
		long amount = aspectTag.getLong(AMOUNT_KEY);
		if (aspect != null && amount > 0) {
			return new WayStack(aspect, amount);
		}
		return null;
	}
	
	/**
	 * Reads all valid stacks from the given list and puts them in an array <br>
	 * Returns null if there were no valid stacks
	 * @param list
	 * @return
	 */
	@Nullable
	public static WayStack[] readFromList(@NotNull ListTag list) {
		WayStack[] ways = new WayStack[list.size()];
		int i = 0;
		for (Tag t : list) {
			if (t.getType() == CompoundTag.TYPE) {
				CompoundTag aspectTag = (CompoundTag)t;
				WayStack wayStack = WayStack.deserialize(aspectTag);
				if (wayStack != null) {
					ways[i] = wayStack;
					i++;
				}
			}
		}
		return i > 0 ? ways : null;
	}
	
	/**
	 * Attempts to find and read a list of stacks from the given tag <br>
	 * null means there was no valid stacks found
	 * @param tag
	 * @return
	 */
	@Nullable
	public static WayStack[] readFromTag(CompoundTag tag) {
		if (tag != null) {
			ListTag list = NBTHelper.Tags.getCompoundList(tag, TYPE_KEY, true);
			if (list != null)
				return readFromList(list);
		}
		return null;
	}
}
