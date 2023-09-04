package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class FormStack extends AspectStack<AspectForm> {
	public static final String TYPE_KEY = "form";
	public FormStack(AspectForm aspect, long amount) {
		super(TYPE_KEY, aspect, amount);
	}
	public FormStack(AspectForm aspect) {
		this(aspect, 1);
	}
	@Override
	public boolean setAspect(AspectForm aspect) {
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
	public static FormStack deserialize(CompoundTag aspectTag) {
		String dat = aspectTag.getString(ASPECT_KEY);
		AspectForm aspect = AspectForm.deserialize(dat);
		long amount = aspectTag.getLong(AMOUNT_KEY);
		if (aspect != null && amount > 0) {
			return new FormStack(aspect, amount);
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
	public static FormStack[] readFromList(@NotNull ListTag list) {
		FormStack[] forms = new FormStack[list.size()];
		int i = 0;
		for (Tag t : list) {
			if (t.getType() == CompoundTag.TYPE) {
				CompoundTag aspectTag = (CompoundTag)t;
				FormStack formStack = FormStack.deserialize(aspectTag);
				if (formStack != null) {
					forms[i] = formStack;
					i++;
				}
			}
		}
		return i > 0 ? forms : null;
	}
	
	/**
	 * Attempts to find and read a list of stacks from the given tag <br>
	 * null means there was no valid stacks found
	 * @param tag
	 * @return
	 */
	@Nullable
	public static FormStack[] readFromTag(CompoundTag tag) {
		if (tag != null) {
			ListTag list = NBTHelper.Tags.getCompoundList(tag, TYPE_KEY, true);
			if (list != null)
				return readFromList(list);
		}
		return null;
	}
}
