package com.quartzshard.aasb.api.alchemy.aspects.stack.legacy;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.*;

import net.minecraft.nbt.CompoundTag;

@Deprecated
public class LegacyWayStack {
	public static final String TYPE_KEY = "aspect_type";
	public static final String TYPE = "way";
	public static final String AMOUNT_KEY = "amount";
	private long amount;
	public LegacyWayStack(long amount) {
		this.amount = amount;
	}
	public LegacyWayStack(AspectWay aspect) {
		this.amount = aspect.getValue();
	}
	
	public long getAmount() {
		return isValid() ? amount : 0;
	}
	
	public void setAmount(long newAmount) {
		if (validate(newAmount)) {
			amount = newAmount;
		}
	}
	
	private static boolean validate(long amount) {
		return amount > 0;
	}
	
	public boolean isValid() {
		return validate(amount);
	}
	
	@Nullable
	public static LegacyWayStack fromTag(CompoundTag tag) {
		if (tag.getString(TYPE_KEY) == TYPE) {
			long amount = tag.getLong(AMOUNT_KEY);
			if (validate(amount))
				return new LegacyWayStack(amount);
		}
		return null;
	}
	
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putString(TYPE_KEY, TYPE);
		tag.putLong(AMOUNT_KEY, amount);
		return tag;
	}
}
