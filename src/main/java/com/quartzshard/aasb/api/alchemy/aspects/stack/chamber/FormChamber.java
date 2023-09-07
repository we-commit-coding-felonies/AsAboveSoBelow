package com.quartzshard.aasb.api.alchemy.aspects.stack.chamber;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

/**
 * basic implementation of IAspectChamber for FormStacks
 */
public class FormChamber implements IAspectChamber<AspectForm, FormStack>, IHandleForm {
	private final int capacity;
	private final Predicate<AspectForm> test;
	protected FormStack stack = FormStack.EMPTY;
	
	/**
	 * @param capacity the maximum stack size of this chamber
	 * @param test a simple test function for validating incoming FormStacks
	 */
	public FormChamber(int capacity, Predicate<AspectForm> test) {
		this.capacity = capacity;
		this.test = test;
	}
	
	/**
	 * no validation test (always valid)
	 * @param capacity the maximum stack size of this chamber
	 */
	public FormChamber(int capacity) {
		this(capacity, (s) -> true);
	}
	
	
	@Override
	public FormStack getContents() {
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
	public boolean isValid(AspectForm aspect) {
		return aspect != null && test.test(aspect);
	}

	@Override
	public boolean canFit(FormStack query) {
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
		boolean empty = stack == FormStack.EMPTY;
		if (!empty && stack.isEmpty()) {
			LogHelper.warn("FormChamber.isEmpty()", "WrongEmptyState", "A chamber was found to have not emptied properly. This has been corrected, but it is still a bug and should be reported!");
			stack = FormStack.EMPTY; // we dont call the onChanged() function here, because this isnt a "normal" change of the contents
			return true;
		}
		return empty;
	}

	@Override
	public int insert(FormStack query, AspectAction action) {
		if (canFit(query)) {
			int q = query.getAmount();
			int add = q <= spaceLeft() ? q : spaceLeft();
			if (action.execute() && add > 0) {
				stack.grow(add);
				onChanged();
			}
			return q - add;
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
	public FormStack extract(FormStack request, AspectAction action) {
		if (request != null && !request.isEmpty() && request.getAspect() == stack.getAspect()) {
			return extract(request.getAmount(), action);
		}
		return FormStack.EMPTY;
	}
	
	/**
	 * Attempts to extract an amount from the chamber <br>
	 * @implNote Remember that the aspect system is PUSH ONLY! Please be respectful of that when using this function!
	 * @param amount requested amount, aspect agnostic
	 * @param action if SIMULATE, will not actually extract anything
	 * @return The actual extracted stack, or EMPTY if nothing
	 */
	@Override
	public FormStack extract(int want, AspectAction action) {
		if (!stack.isEmpty()) {
			int taken = want;
			int have = stack.getAmount();
			if (taken > have) {
				taken = have;
			}
			FormStack extracted = new FormStack(stack.getAspect(), taken);
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
		return FormStack.EMPTY;
	}

	@Override
	public boolean clear() {
		if (isEmpty())
			return true;
		return false;
	}

	@Override
	public void forceClear() {
		if (stack != FormStack.EMPTY) {
			stack = FormStack.EMPTY;
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
	public FormChamber deserialize(CompoundTag chamberTag) {
		FormStack readStack = FormStack.deserialize(chamberTag);
		if (readStack != null) {
			stack = readStack.isEmpty() ? FormStack.EMPTY : readStack;
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
	public FormStack getChamberContents(int idx) {
		return getContents().dupe();
	}
	
	@Override
	public int receiveFrom(FormStack stack, Direction side) {
		return this.insert(stack, AspectAction.EXECUTE);
	}
}
