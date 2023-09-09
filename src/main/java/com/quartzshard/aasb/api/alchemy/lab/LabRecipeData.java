package com.quartzshard.aasb.api.alchemy.lab;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.NonNullList;
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





}