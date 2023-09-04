package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.AspectWay;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class ShapeStack extends AspectStack<AspectShape> {
	public static final String TYPE_KEY = "shape";
	public ShapeStack(AspectShape aspect, long amount) {
		super(TYPE_KEY, aspect, amount);
	}
	public ShapeStack(AspectShape aspect) {
		this(aspect, 1);
	}

	@Override
	public boolean setAspect(AspectShape aspect) {
		if (this.aspect != aspect) {
			this.aspect = aspect;
			return true;
		}
		return false;
	}
	
	/**
	 * @param aspectTag
	 * @return Stack from NBT, or null if invalid
	 */
	@Nullable
	public static ShapeStack deserialize(CompoundTag aspectTag) {
		String dat = aspectTag.getString(ASPECT_KEY);
		AspectShape aspect = AspectShape.deserialize(dat);
		long amount = aspectTag.getLong(AMOUNT_KEY);
		if (aspect != null && amount > 0) {
			return new ShapeStack(aspect, amount);
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
	public static ShapeStack[] readFromList(@NotNull ListTag list) {
		ShapeStack[] shapes = new ShapeStack[list.size()];
		int i = 0;
		for (Tag t : list) {
			if (t.getType() == CompoundTag.TYPE) {
				CompoundTag aspectTag = (CompoundTag)t;
				ShapeStack shapeStack = ShapeStack.deserialize(aspectTag);
				if (shapeStack != null) {
					shapes[i] = shapeStack;
					i++;
				}
			}
		}
		return i > 0 ? shapes : null;
	}
	
	/**
	 * Attempts to find and read a list of stacks from the given tag <br>
	 * null means there was no valid stacks found
	 * @param tag
	 * @return
	 */
	@Nullable
	public static ShapeStack[] readFromTag(CompoundTag tag) {
		if (tag != null) {
			ListTag list = NBTHelper.Tags.getCompoundList(tag, TYPE_KEY, true);
			if (list != null)
				return readFromList(list);
		}
		return null;
	}
}
