package com.quartzshard.aasb.api.alchemy;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handles much of the backend for transmutation <br>
 * not to be confused with the in-game item, The Philosopher's Stone
 * @author quartzshard
 */
public class PhilosophersStone {
	public static final PhilosophersStone INSTANCE = new PhilosophersStone();
		
	public HashMap<ItemData,AlchemicProperties> itemAlchMap = new HashMap<ItemData,AlchemicProperties>();
	
	public static Map<ResourceLocation,ItemData> getAllItems() {
		Map<ResourceLocation,ItemData> allItems = new HashMap<>();
		for (Map.Entry<ResourceKey<Item>,Item> ri : ForgeRegistries.ITEMS.getEntries()) {
			LogHelper.debug("getAllItems()", "ItemGot", ri.getKey().location()+"");
			allItems.put(ri.getKey().location(), ItemData.fromItem(ri.getValue()));
		}
		return allItems;
	}
	
	public static Map<ResourceLocation, RecipeData> getAllRecipes(ReloadableServerResources resources) {
		Map<ResourceLocation, RecipeData> allRecipes = new HashMap<>();
		for (RecipeType<?> recipeType : Registry.RECIPE_TYPE) {
			List<? extends Recipe<?>> recipes = resources.getRecipeManager().getAllRecipesFor((RecipeType) recipeType);
			for (Recipe<?> recipe : recipes) {
				RecipeData got = RecipeData.fromRecipe(recipe);
				if (got == null) {
					// TODO find an actual proper fix for this. the issue is with special recipes that arent properly data driven (such as suspicious stew or map cloning)
					// the janky fix here is to just ingore them but it would be good to actually map some of them
					LogHelper.warn("RecipeData.fromRecipe()", "SkippedRecipe", "Failed to resolve recipe " + recipe.getId().toString() +", skipping!");
				} else {
					LogHelper.debug("getAllRecipes()", "RecipeGot", recipe.getId().toString());
					allRecipes.put(recipe.getId(), got);
				}
			}
		}
		return allRecipes;
	}
	
	@Nullable
	public static Map<ResourceLocation, NonNullList<RecipeData>> searchRecipesFor(ItemData search) {
		// FIXME implement this overload using some static cache of all recipes
		throw new RuntimeException("Not yet implemented: PhilosophersStone.searchRecipesFor(ItemData)");
	}
	
	@Nullable
	public static Map<ResourceLocation, RecipeData> searchRecipesFor(ItemData search, Map<ResourceLocation, RecipeData> allRecipes) {
		Map<ResourceLocation, RecipeData> matches = new HashMap<>();
		for (Entry<ResourceLocation, RecipeData> entry : allRecipes.entrySet()) {
			if (entry.getValue() == null) {
				System.out.println(entry.getKey().toString());
			}
			if (entry.getValue().contains(search))
				matches.put(entry.getKey(), entry.getValue());
		}
		return matches.isEmpty() ? null : matches;
	}

	
	@Nullable
	public static Map<ResourceLocation, NonNullList<RecipeData>> searchRecipesForInput(ItemData search) {
		// FIXME implement this overload using some static cache of all recipes
		throw new RuntimeException("Not yet implemented: PhilosophersStone.searchRecipesForInput(ItemData)");
	}
	@Nullable
	public static Map<ResourceLocation, RecipeData> searchRecipesForInput(ItemData search, Map<ResourceLocation, RecipeData> allRecipes) {
		Map<ResourceLocation, RecipeData> matches = new HashMap<>();
		for (Entry<ResourceLocation, RecipeData> entry : allRecipes.entrySet()) {
			if (entry.getValue().containsInInput(search))
				matches.put(entry.getKey(), entry.getValue());
		}
		return matches.isEmpty() ? null : matches;
	}

	@Nullable
	public static Map<ResourceLocation, NonNullList<RecipeData>> searchRecipesForOutput(ItemData search) {
		// FIXME implement this overload using some static cache of all recipes
		throw new RuntimeException("Not yet implemented: PhilosophersStone.searchRecipesForOutput(ItemData)");
	}
	@Nullable
	public static Map<ResourceLocation, RecipeData> searchRecipesForOutput(ItemData search, Map<ResourceLocation, RecipeData> allRecipes) {
		Map<ResourceLocation, RecipeData> matches = new HashMap<>();
		for (Entry<ResourceLocation, RecipeData> entry : allRecipes.entrySet()) {
			if (entry.getValue().containsInOutput(search))
				matches.put(entry.getKey(), entry.getValue());
		}
		return matches.isEmpty() ? null : matches;
	}
}
