package com.quartzshard.aasb.api.alchemy.lab;

import java.util.List;
import java.util.ListIterator;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;
import com.quartzshard.aasb.api.alchemy.aspects.stack.AspectStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.WayStack;

/**
 * A simple class used to wrap recipe information for processing <br>
 * You can substitute null for any lists that aren't needed, but the lists themselves cannot contain null
 */
public class LabRecipeData {
	public static final String
		TAGKEY_ITEMS = "ItemList",
		TAGKEY_FLUIDS = "FluidList",
		TAGKEY_WAYS = "WayList",
		TAGKEY_SHAPES = "ShapeList",
		TAGKEY_FORMS = "FormList",
			TAGKEY_NUMSLOTS = "Size",
			TAGKEY_STACKS = "Contents",
				TAGKEY_SLOT = "Slot";
	
	/**
	 * generics mean theres really no other way to do this, sorry
	 * @param items
	 * @param fluids
	 * @param ways
	 * @param shapes
	 * @param forms
	 */
	public LabRecipeData(
			@Nullable NonNullList<ItemStack> items,
			@Nullable NonNullList<FluidStack> fluids,
			@Nullable NonNullList<WayStack> ways,
			@Nullable NonNullList<ShapeStack> shapes,
			@Nullable NonNullList<FormStack> forms) {
		this.items = items;
		this.fluids = fluids;
		this.ways = ways;
		this.shapes = shapes;
		this.forms = forms;
	}
	@Nullable public final NonNullList<ItemStack> items;
	@Nullable public final NonNullList<FluidStack> fluids;
	@Nullable public final NonNullList<WayStack> ways;
	@Nullable public final NonNullList<ShapeStack> shapes;
	@Nullable public final NonNullList<FormStack> forms;
	
	private static boolean hasStacks(@Nullable NonNullList<?> input) {
		return input != null && !input.isEmpty();
	}
	
	public static <A extends IAlchemicalFlow<A>, S extends AspectStack<A>> boolean hasAspectStacks(@Nullable NonNullList<S> input) {
		if (hasStacks(input)) {
			for (S stack : input) {
				if (!stack.isEmpty())
					return true;
			}
		}
		return false;
	}
	
	public static boolean hasItemStacks(@Nullable NonNullList<ItemStack> input) {
		if (hasStacks(input)) {
			for (ItemStack stack : input) {
				if (!stack.isEmpty())
					return true;
			}
		}
		return false;
	}
	
	public static boolean hasFluidStacks(@Nullable NonNullList<FluidStack> input) {
		if (hasStacks(input)) {
			for (FluidStack stack : input) {
				if (!stack.isEmpty())
					return true;
			}
		}
		return false;
	}

	/** item list */
	public static NonNullList<ItemStack> il(int size) {
		return NonNullList.withSize(size, ItemStack.EMPTY);
	}
	
	/** liquid list */
	public static NonNullList<FluidStack> ll(int size) {
		return NonNullList.withSize(size, FluidStack.EMPTY);
	}
	
	/** way list */
	public static NonNullList<WayStack> wl(int size) {
		return NonNullList.withSize(size, WayStack.EMPTY);
	}
	
	/** shape list */
	public static NonNullList<ShapeStack> sl(int size) {
		return NonNullList.withSize(size, ShapeStack.EMPTY);
	}
	
	/** form list */
	public static NonNullList<FormStack> fl(int size) {
		return NonNullList.withSize(size, FormStack.EMPTY);
	}
	
	public boolean isEmpty() {
		return hasItemStacks(items)
				|| hasFluidStacks(fluids)
				|| hasAspectStacks(ways)
				|| hasAspectStacks(shapes)
				|| hasAspectStacks(forms);
	}
	
	public CompoundTag serialize() {
		CompoundTag lrdTag = new CompoundTag(); // the root tag for the labrecipedata
		boolean didDo = false;
		
		if (hasStacks(items)) {
			CompoundTag clTag = new CompoundTag(); // compound of list size and list contents
			clTag.putInt(TAGKEY_NUMSLOTS, items.size());
			ListTag listTag = new ListTag(); // the contents
			int i = 0;
			for (ItemStack stack : items) {
				if (!stack.isEmpty()) {
					CompoundTag stackTag = stack.serializeNBT();
					stackTag.putInt(TAGKEY_SLOT, i);
					listTag.add(stackTag);
				}
				i++;
			}
			if (!listTag.isEmpty()) {
				clTag.put(TAGKEY_STACKS, listTag);
				lrdTag.put(TAGKEY_ITEMS, clTag);
				didDo = true;
			}
		}
		
		if (hasStacks(fluids)) {
			CompoundTag clTag = new CompoundTag();
			clTag.putInt(TAGKEY_NUMSLOTS, fluids.size());
			ListTag listTag = new ListTag();
			int i = 0;
			for (FluidStack stack : fluids) {
				if (!stack.isEmpty()) {
					CompoundTag stackTag = new CompoundTag();
					stack.writeToNBT(stackTag);
					stackTag.putInt(TAGKEY_SLOT, i);
					listTag.add(stackTag);
				}
				i++;
			}
			if (!listTag.isEmpty()) {
				clTag.put(TAGKEY_STACKS, listTag);
				lrdTag.put(TAGKEY_FLUIDS, clTag);
				didDo = true;
			}
		}
		
		if (hasStacks(ways)) {
			CompoundTag clTag = new CompoundTag();
			clTag.putInt(TAGKEY_NUMSLOTS, ways.size());
			ListTag listTag = new ListTag();
			int i = 0;
			for (WayStack stack : ways) {
				CompoundTag stackTag = stack.serialize();
				if (stackTag != null) {
					stackTag.putInt(TAGKEY_SLOT, i);
					listTag.add(stackTag);
				}
				i++;
			}
			if (!listTag.isEmpty()) {
				clTag.put(TAGKEY_STACKS, listTag);
				lrdTag.put(TAGKEY_WAYS, clTag);
				didDo = true;
			}
		}
		
		if (hasStacks(shapes)) {
			CompoundTag clTag = new CompoundTag();
			clTag.putInt(TAGKEY_NUMSLOTS, shapes.size());
			ListTag listTag = new ListTag();
			int i = 0;
			for (ShapeStack stack : shapes) {
				CompoundTag stackTag = stack.serialize();
				if (stackTag != null) {
					stackTag.putInt(TAGKEY_SLOT, i);
					listTag.add(stackTag);
				}
				i++;
			}
			if (!listTag.isEmpty()) {
				clTag.put(TAGKEY_STACKS, listTag);
				lrdTag.put(TAGKEY_SHAPES, clTag);
				didDo = true;
			}
		}
		
		if (hasStacks(forms)) {
			CompoundTag clTag = new CompoundTag();
			clTag.putInt(TAGKEY_NUMSLOTS, forms.size());
			ListTag listTag = new ListTag();
			int i = 0;
			for (FormStack stack : forms) {
				CompoundTag stackTag = stack.serialize();
				if (stackTag != null) {
					stackTag.putInt(TAGKEY_SLOT, i);
					listTag.add(stackTag);
				}
				i++;
			}
			if (!listTag.isEmpty()) {
				clTag.put(TAGKEY_STACKS, listTag);
				lrdTag.put(TAGKEY_FORMS, clTag);
				didDo = true;
			}
		}
		
		return lrdTag;
	}
	
	public static LabRecipeData deserialize(CompoundTag lrdTag) {
		CompoundTag itemsTag = lrdTag.getCompound(TAGKEY_ITEMS),
				fluidsTag = lrdTag.getCompound(TAGKEY_FLUIDS),
				waysTag = lrdTag.getCompound(TAGKEY_WAYS),
				shapesTag = lrdTag.getCompound(TAGKEY_SHAPES),
				formsTag = lrdTag.getCompound(TAGKEY_FORMS);
		@Nullable NonNullList<ItemStack> items = null;
		@Nullable NonNullList<FluidStack> fluids = null;
		@Nullable NonNullList<WayStack> ways = null;
		@Nullable NonNullList<ShapeStack> shapes = null;
		@Nullable NonNullList<FormStack> forms = null;
		
		CompoundTag ct = itemsTag;
		int size = ct.getInt(TAGKEY_NUMSLOTS);
		ListTag list = ct.getList(TAGKEY_STACKS, ListTag.TAG_COMPOUND);
		if (size > 0 && !list.isEmpty()) {
			items = il(size);
			for (Tag t : list) {
				if (t instanceof CompoundTag stackTag) {
					int slot = stackTag.getInt(TAGKEY_SLOT);
					ItemStack stack = ItemStack.of(stackTag);
					if (-1 < slot && slot < size && !stack.isEmpty()) {
						items.set(slot, stack);
					}
				}
			}
		}

		ct = fluidsTag;
		size = ct.getInt(TAGKEY_NUMSLOTS);
		list = ct.getList(TAGKEY_STACKS, ListTag.TAG_COMPOUND);
		if (size > 0 && !list.isEmpty()) {
			fluids = ll(size);
			for (Tag t : list) {
				if (t instanceof CompoundTag stackTag) {
					int slot = stackTag.getInt(TAGKEY_SLOT);
					@Nullable FluidStack stack = FluidStack.loadFluidStackFromNBT(stackTag);
					if (-1 < slot && slot < size && stack != null && !stack.isEmpty()) {
						fluids.set(slot, stack);
					}
				}
			}
		}

		ct = waysTag;
		size = ct.getInt(TAGKEY_NUMSLOTS);
		list = ct.getList(TAGKEY_STACKS, ListTag.TAG_COMPOUND);
		if (size > 0 && !list.isEmpty()) {
			ways = wl(size);
			for (Tag t : list) {
				if (t instanceof CompoundTag stackTag) {
					int slot = stackTag.getInt(TAGKEY_SLOT);
					WayStack stack = WayStack.deserialize(stackTag);
					if (-1 < slot && slot < size && stack != null && !stack.isEmpty()) {
						ways.set(slot, stack);
					}
				}
			}
		}

		ct = shapesTag;
		size = ct.getInt(TAGKEY_NUMSLOTS);
		list = ct.getList(TAGKEY_STACKS, ListTag.TAG_COMPOUND);
		if (size > 0 && !list.isEmpty()) {
			shapes = sl(size);
			for (Tag t : list) {
				if (t instanceof CompoundTag stackTag) {
					int slot = stackTag.getInt(TAGKEY_SLOT);
					ShapeStack stack = ShapeStack.deserialize(stackTag);
					if (-1 < slot && slot < size && stack != null && !stack.isEmpty()) {
						shapes.set(slot, stack);
					}
				}
			}
		}

		ct = formsTag;
		size = ct.getInt(TAGKEY_NUMSLOTS);
		list = ct.getList(TAGKEY_STACKS, ListTag.TAG_COMPOUND);
		if (size > 0 && !list.isEmpty()) {
			forms = fl(size);
			for (Tag t : list) {
				if (t instanceof CompoundTag stackTag) {
					int slot = stackTag.getInt(TAGKEY_SLOT);
					FormStack stack = FormStack.deserialize(stackTag);
					if (-1 < slot && slot < size && stack != null && !stack.isEmpty()) {
						forms.set(slot, stack);
					}
				}
			}
		}
		
		return new LabRecipeData(items, fluids, ways, shapes, forms);
	}

	/**
	 * This function should be used sparingly, as checking equality here is fairly expensive for large or complicated LabRecipeDatas
	 * @param data
	 * @return
	 */
	public boolean sameAs(@Nullable LabRecipeData data) {
		if (data != null) {
			// doesnt run if both are null (meaning they are the same)
			while (this.items != null || data.items != null) {
				if (this.items == data.items)
					break;
					
				ListIterator<ItemStack>
					self = this.items.listIterator(),
					them = data.items.listIterator();
				while (self.hasNext() && them.hasNext()) {
					ItemStack mine = self.next();
					ItemStack theirs = them.next();
					if (!ItemStack.matches(mine, theirs))
						return false;
				}
				break;
			}
			
			// doesnt run if both are null (meaning they are the same)
			while (this.fluids != null || data.fluids != null) {
				if (this.fluids == data.fluids)
					break;
					
				ListIterator<FluidStack>
					self = this.fluids.listIterator(),
					them = data.fluids.listIterator();
				while (self.hasNext() && them.hasNext()) {
					FluidStack mine = self.next();
					FluidStack theirs = them.next();
					if (!mine.isFluidStackIdentical(theirs))
						return false;
				}
				break;
			}
			
			// doesnt run if both are null (meaning they are the same)
			while (this.ways != null || data.ways != null) {
				if (this.ways == data.ways)
					break;
					
				ListIterator<WayStack>
					self = this.ways.listIterator(),
					them = data.ways.listIterator();
				while (self.hasNext() && them.hasNext()) {
					WayStack mine = self.next();
					WayStack theirs = them.next();
					if (!mine.sameAs(theirs))
						return false;
				}
				break;
			}
			
			// doesnt run if both are null (meaning they are the same)
			while (this.shapes != null || data.shapes != null) {
				if (this.shapes == data.shapes)
					break;
					
				ListIterator<ShapeStack>
					self = this.shapes.listIterator(),
					them = data.shapes.listIterator();
				while (self.hasNext() && them.hasNext()) {
					ShapeStack mine = self.next();
					ShapeStack theirs = them.next();
					if (!mine.sameAs(theirs))
						return false;
				}
				break;
			}
			
			// doesnt run if both are null (meaning they are the same)
			while (this.forms != null || data.forms != null) {
				if (this.forms == data.forms)
					break;
					
				ListIterator<FormStack>
					self = this.forms.listIterator(),
					them = data.forms.listIterator();
				while (self.hasNext() && them.hasNext()) {
					FormStack mine = self.next();
					FormStack theirs = them.next();
					if (!mine.sameAs(theirs))
						return false;
				}
				break;
			}
			return true;
		}
		return false;
	}
	

}