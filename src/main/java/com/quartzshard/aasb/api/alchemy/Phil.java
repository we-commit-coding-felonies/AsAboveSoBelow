package com.quartzshard.aasb.api.alchemy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspect.*;
import com.quartzshard.aasb.config.AlchemyCfg;
import com.quartzshard.aasb.util.Logger;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This is Phil, short for Philosopher's Stone. He handles a large portion of the alchemy backend. <br>
 * Please be nice to Phil, he is trying his best and will be sad if you are mean to him.
 * <p>
 * TODO: Get Phil a job so he can fullPhil his destiny
 * @author quartzshard
 */
public class Phil {
	public static HashMap<ItemData,AlchData> THE_MAP = new HashMap<ItemData,AlchData>();
	public static NonNullList<NbtWhitelistData> MAPPER_NBTS = NonNullList.create();
	public static NonNullList<LivingItemData> LIVING_ITEMS = NonNullList.create();
	
	/**
	 * This is the default AlchData that gets returned when something can't be found in THE_MAP
	 */
	public static final AlchData UNMAPPED = new AlchData((WayAspect)null,(ShapeAspect)null,(FormAspect)null,ComplexityAspect.UNKNOWN);

	@Nullable
	private static ReloadData RELOAD_DAT;
	
	public static void regenMapperNbts() {
		regenMapperNbts(AlchemyCfg.MAPPER_NBTS_STR.get());
	}
	public static void regenMapperNbts(String dat) {
		if (!MAPPER_NBTS.isEmpty())
			MAPPER_NBTS.clear();
		String[] strs = dat.split(",");
		for (String str : strs) {
			MAPPER_NBTS.add(NbtWhitelistData.fromString(str));
		}
	}
	
	public static void regenLivingItems() {
		regenLivingItems(AlchemyCfg.LIVING_ITEMS_STR.get());
	}
	public static void regenLivingItems(String dat) {
		if (!LIVING_ITEMS.isEmpty())
			LIVING_ITEMS.clear();
		String[] strs = dat.split(",");
		for (String str : strs) {
			LIVING_ITEMS.add(LivingItemData.fromString(str));
		}
	}
	
	/**
	 * Tests an NBT to see if it matches as far as the mapper is concerned
	 * @param search The searched tag
	 * @param test The tag that we are trying to see if it matches
	 * @param iiq "item in question", used with the item-specific tag matching 
	 * @return if the test matched the search for all whitelisted tags
	 */
	public static boolean matchesNbtForMapper(CompoundTag search, CompoundTag test, ResourceLocation iiq) {
		for (NbtWhitelistData w : MAPPER_NBTS) {
			if (w.matches(iiq)) {
				Tag searchTag = search.get(w.tag()),
					testTag = test.get(w.tag());
				if ( (searchTag == null && testTag == null) // if (both == null) || (both != null && match)
					|| (searchTag != null && testTag != null && searchTag.equals(testTag)) ) {
					continue;
				}
				// one of the tags didnt match, so they are different
				return false;
			}
		}
		// they are the same
		return true;
	}

	@SuppressWarnings("null") // If an item has a null ResourceLocation, that seems very bad and its probably OK to crash
	public static boolean isItemAlive(ItemStack stack) {
		for (LivingItemData w : LIVING_ITEMS) {
			if (w.matches(ForgeRegistries.ITEMS.getKey(stack.getItem()).toString())) {
				if ( (w.isWildcard())
					|| (stack.hasTag() && stack.getTag().contains(w.tag())) ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * used in keeping track of the nbt whitelist
	 * @param rl the item resource location, "*" means all items
	 * @param tag the tag name
	 */
	public record NbtWhitelistData(String rl, String tag) {
		public boolean matches(ResourceLocation srl) {
			return matches(srl.toString());
		}
		public boolean matches(String srl) {
			return isWildcard() || rl.equals(srl);
		}
		
		public boolean isWildcard() {
			return rl.equals("*");
		}
		
		public static NbtWhitelistData fromString(String str) {
			int sepIdx = str.indexOf("|");
			return new NbtWhitelistData(str.substring(0, sepIdx), str.substring(sepIdx + 1));
		}
	}
	
	/**
	 * used in keeping track of what items are considered living
	 * @param rl the item resource location
	 * @param tag the tag name, or * to ignore nbt
	 */
	public record LivingItemData(String rl, String tag) {
		public boolean matches(ResourceLocation srl) {
			return matches(srl.toString());
		}
		public boolean matches(String srl) {
			return rl.equals(srl);
		}
		
		public boolean isWildcard() {
			return tag.equals("*");
		}
		
		public static LivingItemData fromString(String str) {
			int sepIdx = str.indexOf("|");
			return new LivingItemData(str.substring(0, sepIdx), str.substring(sepIdx + 1));
		}
	}
	
	public static void stashReloadData(ReloadableServerResources svRes, RegistryAccess regAccess, ResourceManager resMngr) {
		RELOAD_DAT = new ReloadData(svRes, regAccess, resMngr);
	}
	
	public static void mapAspects(TagsUpdatedEvent event) {
		if (RELOAD_DAT != null) {
			long start = System.currentTimeMillis();
			try {
				@SuppressWarnings("null") // eclipse is stupid
				Map<ResourceLocation, RecipeData> allRecipes = getAllRecipes(RELOAD_DAT.svRes());
				/*
				Map<ResourceLocation, RecipeData> searchResults = PhilosophersStone.searchRecipesFor(ItemData.fromItem(Items.DEBUG_STICK), allRecipes);
				
				String resultStr = "";
				if (searchResults != null) for (Entry<ResourceLocation,RecipeData> dat : searchResults.entrySet()) {
					resultStr += dat.getKey().toString() + ", ";
				}
				LogHelper.debug("AsAboveSoBelow.tagsUpdated()", "SearchResults", resultStr);
				*/
				Logger.info("Phil.mapAspects()", "CompletedMapping", "Alchemy mapping completed! (" + (System.currentTimeMillis() - start) + "ms)");
			} catch (Throwable t) {
				Logger.error("Phil.mapAspects()", "FailedToMap", "Failed to finish alchemy mapping! (" + (System.currentTimeMillis() - start) +"ms)");
				System.out.println(t.getLocalizedMessage()); // this is intended to be a println, please do not change it
				t.printStackTrace();
			}
			RELOAD_DAT = null;
		}
	}
	
	public static Map<ResourceLocation,ItemData> getAllItems() {
		Map<ResourceLocation,ItemData> allItems = new HashMap<>();
		for (Map.Entry<ResourceKey<Item>,Item> ri : ForgeRegistries.ITEMS.getEntries()) {
			Logger.debug("getAllItems()", "DiscoveredItem", ri.getKey().location().toString());
			allItems.put(ri.getKey().location(), ItemData.fromItem(ri.getValue()));
		}
		return allItems;
	}
	
	@SuppressWarnings("null") // if there are null recipes, then i think crashing is fine
	public static Map<ResourceLocation, RecipeData> getAllRecipes(ReloadableServerResources resources) {
		Map<ResourceLocation, RecipeData> allRecipes = new HashMap<>();
		for (RecipeType<?> recipeType : ForgeRegistries.RECIPE_TYPES) {
			List<? extends Recipe<?>> recipes = resources.getRecipeManager().getAllRecipesFor((RecipeType) recipeType);
			for (Recipe<?> recipe : recipes) {
				RecipeData got = RecipeData.fromRecipe(recipe, RELOAD_DAT.regAccess());
				if (got == null) {
					// TODO find an actual proper fix for this. the issue is with special recipes that arent properly data driven (such as suspicious stew or map cloning)
					// the janky fix here is to just ingore them but it would be good to actually map some of them
					Logger.warn("RecipeData.fromRecipe()", "SkippedRecipe", "Failed to resolve recipe " + recipe.getId().toString() +", skipping!");
				} else {
					Logger.debug("getAllRecipes()", "DiscoveredRecipe", recipe.getId().toString());
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
		for (Map.Entry<ResourceLocation, RecipeData> entry : allRecipes.entrySet()) {
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
		for (Map.Entry<ResourceLocation, RecipeData> entry : allRecipes.entrySet()) {
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
		for (Map.Entry<ResourceLocation, RecipeData> entry : allRecipes.entrySet()) {
			if (entry.getValue().containsInOutput(search))
				matches.put(entry.getKey(), entry.getValue());
		}
		return matches.isEmpty() ? null : matches;
	}
	
	// https://github.com/sinkillerj/ProjectE/blob/68fbb2dea0cf8a6394fa6c7c084063046d94cee5/src/main/java/moze_intel/projecte/PECore.java#L377
	public record ReloadData(ReloadableServerResources svRes, RegistryAccess regAccess, ResourceManager resMngr) {}
}
