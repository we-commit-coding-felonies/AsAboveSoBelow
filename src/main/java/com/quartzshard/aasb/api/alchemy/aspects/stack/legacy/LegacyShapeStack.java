package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.*;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.nbt.CompoundTag;

public class ShapeStack {
	public static final String TYPE_KEY = "aspect_type";
	public static final String AMOUNT_KEY = "amount";
	public static final String TYPE = "shape";
	private final AspectShape shape;
	private long amount;

	public ShapeStack(AspectShape shape, long amount) {
		this.shape = shape;
		this.amount = amount;
	}
	public ShapeStack(AspectShape shape) {
		this(shape, 1);
	}
	
	public long getAmount() {
		return isValid() ? amount : 0;
	}
	
	public void setAmount(long newAmount) {
		if (validate(newAmount)) {
			amount = newAmount;
		}
	}
	
	public AspectShape getShape() {
		return shape;
	}
	
	private static boolean validate(AspectShape shape, long amount) {
		return validate(shape) && validate(amount);
	}
	private static boolean validate(AspectShape shape) {
		return shape != null;
	}
	private static boolean validate(long amount) {
		return amount > 0;
	}
	
	public boolean isValid() {
		return validate(shape, amount);
	}
	
	@Nullable
	public static ShapeStack fromTag(CompoundTag tag) {
		if (tag.getString(TYPE_KEY) == TYPE) {
			String shapeVal = tag.getString(TYPE);
			AspectShape shape = null;
			try {
				shape = AspectShape.valueOf(AspectShape.class, shapeVal);
			} catch (IllegalArgumentException e) {
				LogHelper.error("ShapeStack.fromTag()", "InvalidShape", "Invalid ShapeStack NBT data: " + tag.toString());
				return null;
			}
			long amount = tag.getLong(AMOUNT_KEY);
			if (validate(shape, amount))
				return new ShapeStack(shape, amount);
		}
		return null;
	}
	
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putString(TYPE_KEY, TYPE);
		tag.putString(TYPE, shape.name());
		tag.putLong(AMOUNT_KEY, amount);
		return tag;
	}
}
