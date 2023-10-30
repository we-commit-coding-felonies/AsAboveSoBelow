package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.init.AlchemyInit.FormTree;
import com.quartzshard.aasb.util.LogHelper;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class FormStack extends AspectStack<AspectForm> {
	public static final String TYPE_KEY = "form";
	public static final FormStack EMPTY = new FormStack(null, 0);
	public FormStack(AspectForm aspect, int amount) {
		super(TYPE_KEY, aspect, amount);
	}
	public FormStack(AspectForm aspect) {
		this(aspect, 1);
	}

	@Override
	public FormStack dupe() {
		return new FormStack(this.aspect, this.getAmount());
	}

	@Override
	public boolean isEmpty() {
		if (EMPTY == null || EMPTY.aspect != null || EMPTY.getAmount() != 0) {
			LogHelper.error("FormStack.isEmpty()", "EmptyIsNotEmpty", "CATASTROPHIC: Something has changed the static EMPTY FormStack to not be empty! THIS IS EXTREMELY BAD AND SHOULD NEVER HAPPEN! THE GAME WILL NOW CRASH!");
			LogHelper.LOGGER.error("The static EMPTY FormStack (henceforth referred to as just EMPTY) is directly linked to every single empty slot in every single thing that can store FormStacks.");
			LogHelper.LOGGER.error("Changing EMPTY to something else causes all of said empty FormStack slots to be changed as well, wreaking havok everywhere and causing unpredictable behavior.");
			LogHelper.LOGGER.error("Because of the extreme severity of this issue, and the enormous butterfly effect it can have on things, THE GAME WILL NOW CRASH.");
			throw new Error("EMPTY is not empty: If you are running with addons, please report this crash to the addon developer(s). If you are running normal AASB, PLEASE REPORT THIS CRASH ASAP!");
		}
		return this == EMPTY || amount <= 0 || aspect == null || !FormTree.exists(aspect.getName());
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
		int amount = aspectTag.getInt(AMOUNT_KEY);
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
	
	@Override
	public void clear() {
		this.become(EMPTY);
	}
}
