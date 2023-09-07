package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectWay;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.nbt.CompoundTag;

/**
 * @implNote Value is different from Amount, and specific to WayStacks <br>
 * 			 WayStack stores 2 longs: <ul>
 * 				<li> AMOUNT: the number of AspectWays in the stack
 * 				<li> VALUE: the Way value of each AspectWay</ul>
 * 			 You should treat Value like you would treat the AspectForm of a FormStack
 */
public class WayStack extends AspectStack<AspectWay> {
	public static final String TYPE_KEY = "way";
	public static final WayStack EMPTY = new WayStack(null, 0);
	public WayStack(AspectWay aspect, int amount) {
		super(TYPE_KEY, aspect, amount);
	}
	public WayStack(long value, int amount) {
		this(new AspectWay(value), amount);
	}
	public WayStack(long value) {
		this(value, 1);
	}

	@Override
	public WayStack dupe() {
		return new WayStack(new AspectWay(aspect.getValue()), this.getAmount());
	}

	@Override
	public boolean isEmpty() {
		if (EMPTY == null || EMPTY.aspect != null || EMPTY.getAmount() != 0) {
			LogHelper.error("WayStack.isEmpty()", "EmptyIsNotEmpty", "CATASTROPHIC: Something has changed the static EMPTY WayStack to not be empty! THIS IS EXTREMELY BAD AND SHOULD NEVER HAPPEN! THE GAME WILL NOW CRASH!");
			LogHelper.LOGGER.error("The static EMPTY WayStack (henceforth referred to as just EMPTY) is directly linked to every single empty slot in every single thing that can store WayStacks.");
			LogHelper.LOGGER.error("Changing EMPTY to something else causes all of said empty WayStack slots to be changed as well, wreaking havok everywhere and causing unpredictable behavior.");
			LogHelper.LOGGER.error("Because of the extreme severity of this issue, and the enormous butterfly effect it can have on things, THE GAME WILL NOW CRASH.");
			throw new Error("EMPTY is not empty: If you are running with addons, please report this crash to the addon developer(s). If you are running normal AASB, PLEASE REPORT THIS CRASH ASAP!");
		}
		return this == EMPTY || amount <= 0 || aspect == null || aspect.getValue() <= 0;
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
		int amount = aspectTag.getInt(AMOUNT_KEY);
		if (aspect != null && amount > 0) {
			return new WayStack(aspect, amount);
		}
		return null;
	}
	
	@Override
	public void clear() {
		this.become(EMPTY);
	}
}
