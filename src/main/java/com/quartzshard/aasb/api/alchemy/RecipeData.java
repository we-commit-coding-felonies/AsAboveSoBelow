package com.quartzshard.aasb.api.alchemy;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.util.AlchemyHelper;
import com.quartzshard.aasb.util.CalcHelper;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeData {
	public NonNullList<Ingredient> inputs;
	public Tuple<ItemData,Integer> output;
	
	public RecipeData(NonNullList<Ingredient> inputs, Tuple<ItemData,Integer> output) {
		this.inputs = inputs;
		this.output = output;
	}
	
	@Nullable
	public static RecipeData fromRecipe(Recipe<?> recipe) {
		Tuple<ItemData,Integer> output = new Tuple<>(ItemData.fromStack(recipe.getResultItem()), recipe.getResultItem().getCount());
		NonNullList<Ingredient> input;
		int
			numIngredients = recipe.getIngredients().size(),
			totalCombos = 1;
		List<Ingredient> ings = new ArrayList<>();
		for (Ingredient ing : recipe.getIngredients()) if (!ing.isEmpty()) {
			ings.add(ing); // makes a pseudocopy of the ingredients list with anything empty taken out
		}
		if (!ings.isEmpty()) {
			input = NonNullList.of(Ingredient.EMPTY, ings.toArray(new Ingredient[0]));
			return new RecipeData(input, output);
		}
		// TODO find an actual proper fix for this. the issue is with special recipes that arent properly data driven (such as suspicious stew)
		// the janky fix here is to just ingore them but it would be good to actually map them
		return null;
	}
	
	@Nullable
	public static NonNullList<RecipeData> fromRecipeOld(Recipe<?> recipe) {
		Tuple<ItemData,Integer> output = new Tuple<>(ItemData.fromStack(recipe.getResultItem()), recipe.getResultItem().getCount());
		NonNullList<Ingredient> input;
		int
			numIngredients = recipe.getIngredients().size(),
			totalCombos = 1;
		List<Ingredient> ings = new ArrayList<>();
		for (Ingredient ing : recipe.getIngredients()) if (!ing.isEmpty()) {
			ings.add(ing); // makes a pseudocopy of the ingredients list with anything empty taken out
			
			/* old code for exhaustive expansion, kept for reference in case its needed later
			totalCombos *= ing.getItems().length;
			List<ItemStack> ingVals = new ArrayList<>(ing.getItems().length);
			boolean didDo = false;
			for (ItemStack stack : ing.getItems()) if (!stack.isEmpty()) {
				ingVals.add(stack);
				didDo = true;
			}
			if (didDo) input.add(ingVals);
			*/
		}
		if (!ings.isEmpty()) {
			input = NonNullList.of(Ingredient.EMPTY, ings.toArray(new Ingredient[0]));
			NonNullList<RecipeData> recipes = NonNullList.createWithCapacity(totalCombos);
			/* old code for exhaustive expansion, kept for reference in case its needed later
			for (List<ItemStack> combo : CalcHelper.getAllCombos(new ArrayList<>(), new ArrayList<>(), input))
				if (!combo.isEmpty()) {
					NonNullList<Ingredient> inputData = NonNullList.create();
					for (ItemStack stack : combo) {
						inputData.add(ItemData.fromStack(stack));
					}
					RecipeData rDat = new RecipeData(inputData, output);
					// uncomment the line below to die instantly
					//LogHelper.LOGGER.debug(rDat.toString());
					recipes.add(new RecipeData(inputData, output));
				}
			*/
			if (!recipes.isEmpty())
				return recipes;
		}
		return null;
	}
	
	public boolean contains(ItemData dat) {
		return containsInOutput(dat) || containsInInput(dat);
		/*
		ResourceLocation searchRl = dat.getItem().getRegistryName(),
						testRl = output.getA().getItem().getRegistryName();
		if (searchRl.equals(testRl)) {
			CompoundTag datNbt = dat.getNBT(),
						outNbt = output.getA().getNBT();
			if ( (datNbt == null && outNbt == null)
				|| (datNbt != null && outNbt != null && AlchemyHelper.matchesNbtForMapper(datNbt, outNbt, searchRl)) ) {
				return true; // we have a match!
			}
		}
		// did not match output, checking inputs
		for (Ingredient ing : inputs) {
			for (ItemStack ingStack : ing.getItems()) {
				testRl = ingStack.getItem().getRegistryName();
				if (searchRl.equals(testRl)) {
					CompoundTag datNbt = dat.getNBT(),
								outNbt = ingStack.getTag();
					if ( (datNbt == null && outNbt == null)
						|| (datNbt != null && outNbt != null && AlchemyHelper.matchesNbtForMapper(datNbt, outNbt, searchRl)) ) {
						return true; // we have a match!
					}
				}
			}
		}
		return false; // no match
		*/
	}
	
	public boolean containsInOutput(ItemData dat) {
		ResourceLocation searchRl = dat.getItem().getRegistryName(),
						testRl = output.getA().getItem().getRegistryName();
		if (searchRl.equals(testRl)) {
			CompoundTag datNbt = dat.getNBT(),
						outNbt = output.getA().getNBT();
			if ( (datNbt == null && outNbt == null)
				|| (datNbt != null && outNbt != null && AlchemyHelper.matchesNbtForMapper(datNbt, outNbt, searchRl)) ) {
				return true; // we have a match!
			}
		}
		return false;
	}
	
	public boolean containsInInput(ItemData dat) {
		ResourceLocation searchRl = dat.getItem().getRegistryName(),
						testRl = null;
		for (Ingredient ing : inputs) {
			for (ItemStack ingStack : ing.getItems()) {
				testRl = ingStack.getItem().getRegistryName();
				if (searchRl.equals(testRl)) {
					CompoundTag datNbt = dat.getNBT(),
								outNbt = ingStack.getTag();
					if ( (datNbt == null && outNbt == null)
						|| (datNbt != null && outNbt != null && AlchemyHelper.matchesNbtForMapper(datNbt, outNbt, searchRl)) ) {
						return true; // we have a match!
					}
				}
			}
		}
		return false; // no match
	}
	
	@Override
	public String toString() {
		String str = "[";
		boolean first = true;
		for (Ingredient ing : inputs) {
			if (!first)
				str += ", ";
			first = false;
			ItemStack[] stacks = ing.getItems();
			if (stacks.length == 1) {
				str += ItemData.fromStack(stacks[0]).toString();
			} else str += ing.toString();
		}
		return str+"] -> " + output.toString();
	}
	
	/*
	public static RecipeData fromRecipe(Recipe<?> recipe) {
		ArrayList<ItemData> inputs = new ArrayList<ItemData>();
		int i = 0;
		for (Ingredient ing : recipe.getIngredients()) {
			ItemStack[] stacks = ing.getItems();
			System.out.println(stacks.length);
			for (ItemStack stack: stacks) {
				inputs.add(ItemData.fromStack(stack));
			}
			
		}
		String allObjs = "";
		for (ItemData dat : inputs) {
		    allObjs = allObjs + obj.toString();
		}

		for (int i = 0; i < ingredients.size(); i++) {
			Ingredient ingredient = ingredients.get(i);
			ItemStack[] stacks = ingredient.getItems();
			System.out.println(stacks.length);
			for (ItemStack stack: stacks) {
				inputs.add(ItemData.fromStack(stack));
			}
		}
		LogHelper.info("fromRecipe()", "ingredientParsing", inputs.stream().map(in -> in.toString()).reduce("", (s, e) -> e + ", " + s));	
		ItemData output = ItemData.fromStack(recipe.getResultItem());
		return new RecipeData(inputs, output);
	}
	*/

}
