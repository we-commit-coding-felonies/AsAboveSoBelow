package com.quartzshard.aasb.api.alchemy.lab;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.stack.*;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

/**
 * Used for transfer of information between the LabTE and the LabProcess functions
 */
public class LabRecipeData {
	@Nullable
	public final ArrayList<ItemStack> items;
	@Nullable
	public final ArrayList<FluidStack> fluids;
	@Nullable
	public final ArrayList<WayStack> ways;
	@Nullable
	public final ArrayList<ShapeStack> shapes;
	@Nullable
	public final ArrayList<FormStack> forms;
	
	/**
	 * generics mean theres really no other way to do this, sorry
	 * @param items
	 * @param fluids
	 * @param ways
	 * @param shapes
	 * @param forms
	 */
	public LabRecipeData(
			@Nullable ArrayList<ItemStack> items,
			@Nullable ArrayList<FluidStack> fluids,
			@Nullable ArrayList<WayStack> ways,
			@Nullable ArrayList<ShapeStack> shapes,
			@Nullable ArrayList<FormStack> forms) {
		this.items = items;
		this.fluids = fluids;
		this.ways = ways;
		this.shapes = shapes;
		this.forms = forms;
	}
}
