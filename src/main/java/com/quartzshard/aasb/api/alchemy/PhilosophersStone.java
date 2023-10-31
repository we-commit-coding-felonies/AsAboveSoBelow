package com.quartzshard.aasb.api.alchemy;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public static Map<ResourceLocation, NonNullList<RecipeData>> getAllRecipes(ReloadableServerResources resources) {
		Map<ResourceLocation, NonNullList<RecipeData>> allRecipes = new HashMap<>();
		for (RecipeType<?> recipeType : Registry.RECIPE_TYPE) {
			List<? extends Recipe<?>> recipes = resources.getRecipeManager().getAllRecipesFor((RecipeType) recipeType);
			for (Recipe<?> recipe : recipes) {
				LogHelper.debug("getAllRecipes()", "RecipeGot", recipe.getId().toString());
				allRecipes.put(recipe.getId(), RecipeData.fromRecipe(recipe));
			}
		}
		
		return allRecipes;
	}
	
	
}
