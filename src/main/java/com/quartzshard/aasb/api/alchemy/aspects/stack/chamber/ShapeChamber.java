package com.quartzshard.aasb.api.alchemy.aspects.stack.chamber;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

/**
 * basic implementation of IAspectChamber for ShapeStacks
 */
public class ShapeChamber implements IAspectChamber<AspectShape, ShapeStack>, IHandleShape {
	private final int capacity;
	private final Predicate<AspectShape> test;
	protected ShapeStack stack = ShapeStack.EMPTY;
	
	/**
	 * @param capacity the maximum stack size of this chamber
	 * @param test a simple test function for validating incoming ShapeStacks
	 */
	public ShapeChamber(int capacity, Predicate<AspectShape> test) {
		this.capacity = capacity;
		this.test = test;
	}
	
	/**
	 * no validation test (always valid)
	 * @param capacity the maximum stack size of this chamber
	 */
	public ShapeChamber(int capacity) {
		this(capacity, (s) -> true);
	}
	
	
	@Override
	public ShapeStack getContents() {
		return stack;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public int spaceLeft() {
		return capacity - stack.getAmount();
	}
	
	@Override
	public boolean isValid(AspectShape aspect) {
		return aspect != null && test.test(aspect);
	}

	@Override
	public boolean canFit(ShapeStack query) {
		return query != null && !query.isEmpty()
				&& isValid(query.getAspect())
				&& query.getAspect() == stack.getAspect()
				&& query.getAmount() <= spaceLeft();
	}
	
	/**
	 * @implNote When this function is run,
	 * it will also change its contents to EMPTY if the following is true <ul>
	 * <li> contents.isEmpty() </li>
	 * <li> contents != EMPTY </li> </ul>
	 * This is simply to correct an invalid empty state, and it will print a WARN if this happens
	 */
	@Override
	public boolean isEmpty() {
		boolean empty = stack == ShapeStack.EMPTY;
		if (!empty && stack.isEmpty()) {
			LogHelper.warn("ShapeChamber.isEmpty()", "WrongEmptyState", "A chamber was found to have not been emptied properly. This has been corrected, but it is still a bug and should be reported!");
			stack = ShapeStack.EMPTY; // we dont call the onChanged() function here, because this isnt a "normal" change of the contents
			return true;
		}
		return empty;
	}

	@Override
	public int insert(ShapeStack query, AspectAction action) {
		if (canFit(query)) {
			int q = query.getAmount();
			int add = q <= spaceLeft() ? q : spaceLeft();
			if (action.execute() && add > 0) {
				stack.grow(add);
				onChanged();
			}
			return q - add;
		} else if (stack.isEmpty()) {
			int amount = query.getAmount(); 
			if (amount <= capacity) {
				if (action.execute())
					stack = query;
				return 0;
			} else {
				if (action.execute())
					stack = new ShapeStack(query.getAspect(), capacity);
				return amount - capacity;
			}
		}
		return query.getAmount();
	}
	
	/**
	 * Attempts to extract the given stack from the chamber <br>
	 * @implNote Remember that the aspect system is PUSH ONLY! Please be respectful of that when using this function!
	 * @param stack a requested stack. will fail if aspects dont match
	 * @param action if SIMULATE, will not actually extract anything
	 * @return The actual extracted stack, or EMPTY if nothing
	 */
	@Override
	public ShapeStack extract(ShapeStack request, AspectAction action) {
		if (request != null && !request.isEmpty() && request.getAspect() == stack.getAspect()) {
			return extract(request.getAmount(), action);
		}
		return ShapeStack.EMPTY;
	}
	
	/**
	 * Attempts to extract an amount from the chamber <br>
	 * @implNote Remember that the aspect system is PUSH ONLY! Please be respectful of that when using this function!
	 * @param amount requested amount, aspect agnostic
	 * @param action if SIMULATE, will not actually extract anything
	 * @return The actual extracted stack, or EMPTY if nothing
	 */
	@Override
	public ShapeStack extract(int want, AspectAction action) {
		if (!stack.isEmpty()) {
			int taken = want;
			int have = stack.getAmount();
			if (taken > have) {
				taken = have;
			}
			ShapeStack extracted = new ShapeStack(stack.getAspect(), taken);
			if (action.execute() && taken > 0) {
				int left = have - taken;
				if (left <= 0) {
					forceClear();
				} else {
					stack.shrink(taken);
					onChanged();
				}
			}
			return extracted;
		} else forceClear();
		return ShapeStack.EMPTY;
	}

	@Override
	public boolean clear() {
		if (isEmpty())
			return true;
		return false;
	}

	@Override
	public void forceClear() {
		if (stack != ShapeStack.EMPTY) {
			stack = ShapeStack.EMPTY;
			onChanged();
		}
	}
	
	/**
	 * Gets run whenever this chamber has its contents modified. <br>
	 * Gets run *after* the change is done. Does nothing by default.
	 */
	protected void onChanged() {}
	
	/**
	 * Creates an NBT representation of this chamber, used when saving <br>
	 * Doesnt save any data that is unchanging (such as capacity), only stuff that can vary (such as stored aspects) is saved!
	 * @return tag, or null if this chamber doesnt need to be saved
	 */
	@Nullable
	public CompoundTag serialize() {
		return stack.serialize();
	}
	
	/**
	 * Loads variable chamber data (such as stacks held) from NBT, used when loading
	 * @param chamberTag
	 * @return itself
	 */
	public ShapeChamber deserialize(CompoundTag chamberTag) {
		ShapeStack readStack = ShapeStack.deserialize(chamberTag);
		if (readStack != null) {
			stack = readStack.isEmpty() ? ShapeStack.EMPTY : readStack;
		}
		return this;
	}
	
	
	
	
	
	/////////////
	// HANDLER //
	/////////////

	
	
	
	
	@Override
	public int getChamberCount() {
		return 1;
	}

	@Override
	public int getChamberCapacity(int idx) {
		return getCapacity();
	}

	@Override
	public boolean canAccept() {
		return isEmpty() || spaceLeft() > 0;
	}

	@Override
	public boolean canAccept(Direction side) {
		return true;
	}

	@Override
	public boolean canPush() {
		return false;
	}

	@Override
	public boolean canPushTo(Direction side) {
		return canPush();
	}

	@Override
	public boolean isPushing() {
		return false;
	}

	@Override
	public boolean attemptPush() {
		return false;
	}

	@Override
	public boolean isPipe() {
		return false;
	}

	@Override
	public ShapeStack getChamberContents(int idx) {
		return getContents().dupe();
	}
	
	@Override
	public int receiveFrom(ShapeStack stack, Direction side) {
		return this.insert(stack, AspectAction.EXECUTE);
	}
}
