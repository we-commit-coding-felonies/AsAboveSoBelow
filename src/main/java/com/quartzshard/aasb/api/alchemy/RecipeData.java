package com.quartzshard.aasb.api.alchemy;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.util.CalcHelper;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeData {
	public NonNullList<ItemData> inputs;
	public ItemData output;
	
	public RecipeData(NonNullList<ItemData> inputs, ItemData output) {
		this.inputs = inputs;
		this.output = output;
	}
	
	@Nullable
	public static NonNullList<RecipeData> fromRecipe(Recipe<?> recipe) {
		ItemData output = ItemData.fromStackWithCount(recipe.getResultItem());
		List<List<ItemStack>> input = new ArrayList<>();
		int
			numIngredients = recipe.getIngredients().size(),
			totalCombos = 1;
		for (Ingredient ing : recipe.getIngredients()) if (!ing.isEmpty()) {
			totalCombos *= ing.getItems().length;
			List<ItemStack> ingVals = new ArrayList<>(ing.getItems().length);
			boolean didDo = false;
			for (ItemStack stack : ing.getItems()) if (!stack.isEmpty()) {
				ingVals.add(stack);
				didDo = true;
			}
			if (didDo) input.add(ingVals);
		}
		if (!input.isEmpty()) {
			NonNullList<RecipeData> recipes = NonNullList.createWithCapacity(totalCombos);
			for (List<ItemStack> combo : CalcHelper.getAllCombos(new ArrayList<>(), new ArrayList<>(), input))
				if (!combo.isEmpty()) {
					NonNullList<ItemData> inputData = NonNullList.create();
					for (ItemStack stack : combo) {
						inputData.add(ItemData.fromStack(stack));
					}
					RecipeData rDat = new RecipeData(inputData, output);
					// uncomment the line below to die instantly
					//LogHelper.LOGGER.debug(rDat.toString());
					recipes.add(new RecipeData(inputData, output));
				}
			if (!recipes.isEmpty())
				return recipes;
		}
		return null;
	}
	
	@Override
	public String toString() {
		String str = "[";
		boolean first = true;
		for (ItemData data : inputs) {
			if (!first)
				str += ", ";
			first = false;
			str += data.toString();
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
