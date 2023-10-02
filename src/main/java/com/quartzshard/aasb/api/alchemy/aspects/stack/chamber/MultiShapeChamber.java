package com.quartzshard.aasb.api.alchemy.aspects.stack.chamber;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/**
 * ShapeChamber with multiple slots
 */
public class MultiShapeChamber implements IAspectChamber<AspectShape, ShapeStack>, IHandleShape {
	private final int capacity;
	private final Predicate<AspectShape> test;
	protected NonNullList<ShapeStack> storedShapes;
	
	/**
	 * @param capacity the maximum stack size of this chamber
	 * @param test a simple test function for validating incoming ShapeStacks
	 */
	public MultiShapeChamber(int capacity, int slots, Predicate<AspectShape> test) {
		this.capacity = capacity;
		storedShapes = LabRecipeData.sl(slots);
		this.test = test;
	}
	
	/**
	 * no validation test (always valid)
	 * @param capacity the maximum stack size of this chamber
	 */
	public MultiShapeChamber(int capacity, int slots) {
		this(capacity, slots, (s) -> true);
	}

	/**
	 * @Deprecated please use the slot aware version
	 */
	@Deprecated
	@Override
	public ShapeStack getContents() {
		return getContents(0);
	}
	
	public ShapeStack getContents(int slot) {
		return storedShapes.get(slot);
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @Deprecated please use the slot aware version
	 */
	@Deprecated
	@Override
	public int spaceLeft() {
		return spaceLeft(0);
	}

	public int spaceLeft(int slot) {
		return capacity - storedShapes.get(slot).getAmount();
	}
	
	@Override
	public boolean isValid(AspectShape aspect) {
		return aspect != null && test.test(aspect);
	}

	@Override
	public boolean canFit(ShapeStack query) {
		if (canMaybeFit(query)) {
			for (int i = 0; i < storedShapes.size(); i++) {
				if (query.getAspect() == storedShapes.get(i).getAspect()
					&& query.getAmount() <= spaceLeft(i)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean canMaybeFit(ShapeStack query) {
		return query != null && !query.isEmpty() && isValid(query.getAspect());
	}

	protected boolean canMaybeFitInSlot(ShapeStack query, int slot) {
		return query.getAspect() == storedShapes.get(slot).getAspect()
				&& query.getAmount() <= spaceLeft(slot);
	}
	
	public boolean canFit(ShapeStack query, int slot) {
		return canMaybeFit(query)
				&& query.getAspect() == storedShapes.get(slot).getAspect()
				&& query.getAmount() <= spaceLeft(slot);
	}

	/**
	 * checks if ALL slots are empty
	 */
	@Override
	public boolean isEmpty() {
		for (int i = 0; i < storedShapes.size(); i++) {
			if (!isEmpty(i))
				return false;
		}
		return true;
	}
	
	/**
	 * @implNote When this function is run,
	 * it will also change its contents to EMPTY if the following is true <ul>
	 * <li> contents.isEmpty() </li>
	 * <li> contents != EMPTY </li> </ul>
	 * This is simply to correct an invalid empty state, and it will print a WARN if this happens
	 */
	public boolean isEmpty(int slot) {
		ShapeStack stack = storedShapes.get(slot);
		boolean empty = stack == ShapeStack.EMPTY;
		if (!empty && stack.isEmpty()) {
			LogHelper.warn("MultiShapeChamber.isEmpty()", "WrongEmptyState", "A chamber was found to have not been emptied properly. This has been corrected, but it is still a bug and should be reported!");
			stack = ShapeStack.EMPTY; // we dont call the onChanged() function here, because this isnt a "normal" change of the contents
			return true;
		}
		return empty;
	}

	@Override
	public int insert(ShapeStack query, AspectAction action) {
		int qAmt = query.getAmount();
		if (canMaybeFit(query)) {
			for (int i = 0; i < storedShapes.size(); i++) {
				int rem = insert(query, action, i);
				if (rem != qAmt) {
					return rem;
				}
			}
			
		}
		return qAmt;
	}

	/**
	 * Attempts to insert a shape into the specified chamber slot
	 * @param query
	 * @param action
	 * @param slot
	 * @return
	 */
	public int insert(ShapeStack query, AspectAction action, int slot) {
		ShapeStack stack = storedShapes.get(slot);
		if (canMaybeFitInSlot(query, slot)) {
			int q = query.getAmount();
			int add = q <= spaceLeft(slot) ? q : spaceLeft(slot);
			if (action.execute() && add > 0) {
				stack.grow(add);
				onChanged();
			}
			return q - add;
		} else if (stack.isEmpty()) {
			int amount = query.getAmount(); 
			if (amount <= capacity) {
				if (action.execute()) {
					storedShapes.set(slot, query.dupe());
				}
				return 0;
			} else {
				if (action.execute()) {
					storedShapes.set(slot, new ShapeStack(query.getAspect(), capacity));
				}
				return amount - capacity;
			}
		}
		// unable to insert
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
		if (request != null && !request.isEmpty()) {
			for (int i = 0; i < storedShapes.size(); i++) {
				ShapeStack stack = storedShapes.get(i);
				if (request.getAspect() == stack.getAspect()) {
					ShapeStack extracted = extract(request.getAmount(), action, i);
					if (!extracted.isEmpty()) {
						return extracted;
					}
				}
			}
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
		if (want > 0) {
			for (int i = 0; i < storedShapes.size(); i++) {
				ShapeStack extracted = extract(want, action, i);
				if (!extracted.isEmpty()) {
					return extracted;
				}
			}
		}
		return ShapeStack.EMPTY;
	}
	
	/**
	 * Attempts to extract an amount from the given slot in the chamber <br>
	 * @implNote Remember that the aspect system is PUSH ONLY! Please be respectful of that when using this function!
	 * @param amount requested amount, aspect agnostic
	 * @param action if SIMULATE, will not actually extract anything
	 * @param slot the slot to extract from
	 * @return The actual extracted stack, or EMPTY if nothing
	 */
	public ShapeStack extract(int want, AspectAction action, int slot) {
		ShapeStack stack = storedShapes.get(slot);
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
		} else forceClearSlot(slot);
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
		for (int i = 0; i < storedShapes.size(); i++) {
			forceClearSlot(i);
		}
	}

	public void forceClearSlot(int slot) {
		ShapeStack stack = storedShapes.get(slot);
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
	
	public static final String
		TK_SLOTNUM = "SlotCount",
		TK_DAT = "StorageData",
			TK_SLOT = "Slot";
	
	/**
	 * Creates an NBT representation of this chamber, used when saving <br>
	 * Doesnt save any data that is unchanging (such as capacity), only stuff that can vary (such as stored aspects) is saved!
	 * @return tag, or null if this chamber doesnt need to be saved
	 */
	@Nullable
	public CompoundTag serialize() {
		if (!this.isEmpty()) {
			ListTag storageTag = new ListTag();
			for (int i = 0; i < storedShapes.size(); i++) {
				ShapeStack stack = storedShapes.get(i);
				if (!stack.isEmpty()) {
					CompoundTag stackTag = stack.serialize();
					if (stackTag != null) {
						stackTag.putInt(TK_SLOT, i);
						storageTag.add(stackTag);
					}
				}
			}
			CompoundTag chamberTag = new CompoundTag();
			chamberTag.putInt(TK_SLOTNUM, storedShapes.size());
			chamberTag.put(TK_DAT, storageTag);
			return chamberTag;
		}
		return null;
	}
	
	/**
	 * Loads variable chamber data (such as stacks held) from NBT, used when loading
	 * @param chamberTag
	 * @return itself
	 */
	public MultiShapeChamber deserialize(CompoundTag chamberTag) {
		if (chamberTag.contains(TK_DAT, Tag.TAG_LIST)) {
			ListTag storageTag = chamberTag.getList(TK_DAT, Tag.TAG_COMPOUND);
			if (!storageTag.isEmpty() && chamberTag.contains(TK_SLOTNUM, Tag.TAG_ANY_NUMERIC)) {
				
				// list size compatability checking. creates a new list if sizes incompatible (and warns)
				int size = chamberTag.getInt(TK_SLOTNUM);
				if (storedShapes == null && storedShapes.size() < size) {
					LogHelper.warn("MultiShapeChamber.deserialize()", "IncompatibleSlotCounts", "Something went wrong while trying to load a MultiShapeChamber from NBT. Loaded data has a larger slot count than the existing initialized list. Existing list will be replaced with a properly-sized one!");
					storedShapes = LabRecipeData.sl(size);
				}
				
				// actual deserialization of stacks
				for (Tag t : storageTag) {
					if (t instanceof CompoundTag stackTag && stackTag.contains(TK_SLOT, Tag.TAG_ANY_NUMERIC)) {
						int slot = stackTag.getInt(TK_SLOT);
						@Nullable ShapeStack stack = ShapeStack.deserialize(stackTag);
						if (stack != null && !stack.isEmpty()) {
							storedShapes.set(slot, stack);
						}
					}
				}
				
			}
		}
		
		return this;
	}
	
	
	
	
	
	/////////////
	// HANDLER //
	/////////////

	
	
	
	
	@Override
	public int getChamberCount() {
		return storedShapes.size();
	}

	@Override
	public int getChamberCapacity(int idx) {
		return getCapacity();
	}

	@Override
	public boolean canAccept() {
		for (int i = 0; i < storedShapes.size(); i++) {
			if (isEmpty(i) || spaceLeft(i) > 0) {
				return true;
			}
		}
		return false;
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
		return getContents(idx).dupe();
	}
	
	@Override
	public int receiveFrom(ShapeStack stack, Direction side) {
		return this.insert(stack, AspectAction.EXECUTE);
	}
}
