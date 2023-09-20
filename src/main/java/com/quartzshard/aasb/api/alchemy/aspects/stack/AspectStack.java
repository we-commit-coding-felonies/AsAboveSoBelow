package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;
import com.quartzshard.aasb.api.capability.aspect.IAspectHandler.AspectType;
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
	
	public final String TYPE_KEY;
	protected A aspect;
	protected int amount;
	
	public AspectStack(String typeKey, A aspect, int amount) {
		TYPE_KEY = typeKey;
		this.aspect = aspect;
		this.amount = amount;
	}
	
	/**
	 * Checks if this stack is empty
	 * @apiNote This function will also run a check on the static EMPTY stack
	 * to make sure that it is still empty, and has not been tampered with.
	 * @return
	 */
	public abstract boolean isEmpty();
	
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
	
	public AspectType getType() {
		if (aspect == null)
			return AspectType.EMPTY;
		return aspect.type();
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void grow(int by) {
		this.amount += by;
	}
	
	public void shrink(int by) {
		this.amount -= by;
	}
	
	public abstract <S extends AspectStack<A>> S dupe();
	
	public <S extends AspectStack<A>> boolean sameAs(S other) {
		if (this.isEmpty() && other.isEmpty()) {
			return true;
		}
		return this.amount == other.amount
				&& this.aspect.serialize().equals(other.aspect.serialize());
	}
	
	public <S extends AspectStack<A>> void become(S other) {
		if (this.isEmpty()) {
			LogHelper.warn("AspectStack.become()", "EmptyBecoming", "An empty AspectStack tried to become a different stack, supressing...");
			return; // prevents morons like myself from changing the static emptystacks on accident
		}
		this.aspect = other.aspect;
		this.amount = other.amount;
		if (!this.sameAs(other)) {
			LogHelper.warn("AspectStack.become()", "ImperfectCopy", "An AspectStack tried to become another, but failed the sameAs() check!");
		}
	}
	
	public abstract void clear();
	
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
	
	@Deprecated
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

	@Deprecated
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
