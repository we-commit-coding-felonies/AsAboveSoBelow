package com.quartzshard.aasb.api.alchemy;

import java.util.ArrayList;
import java.util.List;

import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeData {
	public ArrayList<ItemData> inputs;
	public ItemData output;
	
	public RecipeData(ArrayList<ItemData> inputs, ItemData output) {
		this.inputs = inputs;
		this.output = output;
	}
	
	public static RecipeData fromRecipe(Recipe<?> recipe) {
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		ArrayList<ItemData> inputs = new ArrayList<ItemData>();
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

}
