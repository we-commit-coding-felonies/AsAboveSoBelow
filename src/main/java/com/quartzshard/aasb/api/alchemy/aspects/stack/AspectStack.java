package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;
import com.quartzshard.aasb.api.alchemy.aspects.AspectEmpty;
import com.quartzshard.aasb.util.LogHelper;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/**
 * parent of the 3 aspect stack types, containing some common code
 * @param <T> the IAlchemicalFlow that this is a stack of
 */
public abstract class AspectStack<A extends IAlchemicalFlow<A>> {
	public static final String ASPECT_KEY = "aspect";
	public static final String AMOUNT_KEY = "amount";
	public static final AspectStack<AspectEmpty> EMPTY = new EmptyAspectStack();
	
	public final String TYPE_KEY;
	protected A aspect;
	protected long amount;
	
	public AspectStack(String typeKey, A aspect, long amount) {
		TYPE_KEY = typeKey;
		this.aspect = aspect;
		this.amount = amount;
	}

	public boolean isEmpty() {
		return amount <= 0 || isEmptyAspect();
	}
	private boolean isEmptyAspect() {
		return aspect == null
				|| aspect instanceof AspectEmpty;
	}
	
	@Nullable
	public A getAspect() {
		return aspect;
	}
	
	/**
	 * do not use this to change to a different aspect type, only to change to a different aspect of the same type
	 * @param aspect
	 * @return if the aspect was actually changed
	 */
	public abstract boolean setAspect(A aspect);
	
	public long getAmount() {
		return amount;
	}
	
	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	/**
	 * Serializes this AspectStack as an NBT tag <br>
	 * will return null if the AspectStack is empty (and thus doesnt need to be saved)
	 * @return
	 */
	@Nullable
	public CompoundTag serialize() {
		if (!isEmpty()) {
			CompoundTag aspectTag = new CompoundTag();
			aspectTag.putString(ASPECT_KEY, aspect.serialize());
			aspectTag.putLong(AMOUNT_KEY, amount);
			return aspectTag;
		}
		return null;
	}
	
	/**
	 * Puts this AspectStack into the given ListTag
	 * @return
	 */
	public ListTag writeToList(ListTag list) {
		if (list != null) {
			CompoundTag dat = this.serialize();
			if (dat != null) {
				list.add(dat);
			}
		}
		return list;
	}
	
	/**
	 * Puts this AspectStack into the proper list in the given CompoundTag <br>
	 * if the list doesnt exist, it will create it automatically
	 * @return null if something went wrong during serialization
	 */
	@Nullable
	public ListTag writeToTag(CompoundTag tag) {
		if (tag != null) {
			ListTag list = NBTHelper.Tags.getCompoundList(tag, TYPE_KEY, false);
			return writeToList(list);
		}
		return null;
	}
}
