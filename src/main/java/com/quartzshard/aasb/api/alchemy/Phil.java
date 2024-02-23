package com.quartzshard.aasb.api.alchemy;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.primitives.Longs;
import com.quartzshard.aasb.api.alchemy.aspect.*;
import com.quartzshard.aasb.config.AlchemyCfg;
import com.quartzshard.aasb.util.Logger;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This is Phil, short for Philosopher's Stone. He handles a large portion of the alchemy backend. <br>
 * Please be nice to Phil, he is trying his best and will be sad if you are mean to him.
 * <p>
 * TODO: Get Phil a job so he can fullPhil his destiny
 * @author quartzshard
 */
public class Phil {
	private static Map<ItemData,AlchData> alchMap = new HashMap<>();
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
	public static void regenMapperNbts(@NotNull String dat) {
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
	public static void regenLivingItems(@NotNull String dat) {
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
	public static boolean matchesNbtForMapper(CompoundTag search, CompoundTag test, @NotNull ResourceLocation iiq) {
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
		
		public static NbtWhitelistData fromString(@NotNull String str) {
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
		
		public static LivingItemData fromString(@NotNull String str) {
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
				@SuppressWarnings("null") @NotNull // eclipse is stupid
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
	
	// FIXME this must be removed!!!!!
	public static void debugTestChangeMap(Map<ItemData,AlchData> newMap) {
		alchMap = newMap;
	}
	
	/**
	 * Gets aspects for The Philosopher's Stone from world seed
	 * @param level
	 * @return the current aspects of The Philosopher's Stone
	 */
	public static AlchData getPhilAspects(@NotNull ServerLevel level) {
		return getPhilAspects(level.getSeed());
	}

	/**
	 * Gets aspects for The Philosopher's Stone from a number
	 * @param seed The seed to use for generating aspects
	 * @return the aspects of The Philosopher's Stone for the given seed
	 */
	public static @NotNull AlchData getPhilAspects(long seed) {
		MessageDigest d;
		try {
			d = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new UnknownError("NoSuchAlgorithException caught in com.quartzshard.aasb.api.alchemy.Phil.getPhilAspects(long seed), but such an exception should be impossible to encounter!");
		}
		ByteBuffer hash = ByteBuffer.wrap(d.digest(Longs.toByteArray(seed)));
		long[] aspectSeeds = {
				hash.getLong(),
				hash.getLong(),
				hash.getLong(),
				hash.getLong()
		};
		return new AlchData(
					WayAspect.fromSeed(aspectSeeds[0]), // TODO configurable way range
					ShapeAspect.fromSeed(aspectSeeds[1]),
					FormAspect.fromSeed(aspectSeeds[2]),
					ComplexityAspect.PHIL
				);
	}

	/**
	 * Converts a UUID into aspects
	 * @param uuid UUID in int array format
	 * @return Corresponding AlchData with ComplexityAspect SEEDGEN
	 */
	public static @NotNull AlchData getUUIDAspects(int[] uuid) {
		return AlchData.fromSeeds(uuid[0],uuid[1],uuid[2]);
	}
	public static AlchData getUUIDAspects(UUID uuid) {
		return getUUIDAspects(UUIDUtil.uuidToIntArray(uuid));
	}
	public static AlchData getUUIDAspects(String uuid) {
		return getUUIDAspects(UUID.fromString(uuid));
	}
	public static AlchData getUUIDAspects(@NotNull IntArrayTag uuid) {
		return getUUIDAspects(uuid.getAsIntArray());
	}

	/**
	 * Checks if the alchemy map has aspects for the given ItemData
	 * @param item the ItemData
	 * @return True if it has aspects
	 */
	public static boolean hasAspects(ItemData item) {
		return alchMap.containsKey(item);
	}
	/**
	 * Checks if the alchemy map has aspects for the given ItemLike
	 * @param item the ItemLike
	 * @return True if it has aspects
	 */
	public static boolean hasAspects(ItemLike item) {
		return hasAspects(ItemData.fromItem(item));
	}
	/**
	 * Checks if the alchemy map has aspects for the given Item RegistryObject
	 * @param item the RegistryObject
	 * @return True if it has aspects
	 */
	public static boolean hasAspects(RegistryObject<? extends Item> item) {
		return hasAspects(item.get());
	}
	/**
	 * Checks if the alchemy map has aspects for the given ItemStack
	 * @param item the ItemStack
	 * @return True if it has aspects
	 * @apiNote Does not take stack size into consideration
	 */
	public static boolean hasAspects(ItemStack item) {
		return hasAspects(ItemData.fromStack(item));
	}
	
	/**
	 * Queries the alchemy map for the aspects of a specific ItemData
	 * @param item the ItemData
	 * @return Aspects for the item, or UNMAPPED if the they couldn't be found
	 */
	public static AlchData getAspects(ItemData item) {
		return alchMap.getOrDefault(item, UNMAPPED);
	}
	/**
	 * Queries the alchemy map for the aspects of an ItemLike
	 * @param item the ItemLike
	 * @return Aspects for the item, or UNMAPPED if they couldn't be found
	 */
	public static AlchData getAspects(ItemLike item) {
		return getAspects(ItemData.fromItem(item));
	}
	/**
	 * Queries the alchemy map for the aspects of an Item RegistryObject
	 * @param item the RegistryObject
	 * @return Aspects for the item, or UNMAPPED if they couldn't be found
	 */
	public static AlchData getAspects(RegistryObject<? extends Item> item) {
		return getAspects(item.get());
	}
	/**
	 * Queries the alchemy map for the aspects of an ItemStack <br>
	 * @param item the ItemStack
	 * @return Aspects for the item, or UNMAPPED if they couldn't be found
	 * @apiNote Does not take stack size into consideration
	 */
	public static AlchData getAspects(ItemStack item) {
		return getAspects(ItemData.fromStack(item));
	}
	
	/**
	 * Gets the WayAspect of an item
	 * @param item
	 * @return WayAspect
	 */
	@Nullable
	public static WayAspect getWay(ItemData item) {
		return getAspects(item).way();
	}
	@Nullable
	public static WayAspect getWay(ItemLike item) {
		return getAspects(item).way();
	}
	@Nullable
	public static WayAspect getWay(@NotNull RegistryObject<? extends Item> item) {
		return getAspects(item).way();
	}
	/**
	 * Gets the WayAspect of an item. Does not take stack size into consideration.
	 * @param item
	 * @return WayAspect
	 */
	@Nullable
	public static WayAspect getWay(ItemStack item) {
		return getAspects(item).way();
	}
	
	/**
	 * Gets the raw numerical value of the item's Way.
	 * @param item
	 * @return Numerical value of Way, or -1 if null
	 */
	public static long getWaySimple(ItemData item) {
		WayAspect way = getWay(item);
		return way == null ? -1 : way.value();
	}
	public static long getWaySimple(ItemLike item) {
		return getWaySimple(ItemData.fromItem(item));
	}
	public static long getWaySimple(RegistryObject<? extends Item> item) {
		return getWaySimple(item.get());
	}
	/**
	 * Gets the raw numerical value of the item's Way. Does not take stack size into consideration
	 * @param item
	 * @return Numerical value of Way, or -1 if null
	 */
	public static long getWaySimple(ItemStack item) {
		return getWaySimple(ItemData.fromStack(item));
	}

	/**
	 * Gets the ShapeAspect of an item
	 * @param item
	 * @return ShapeAspect
	 */
	@Nullable
	public static ShapeAspect getShape(ItemData item) {
		return getAspects(item).shape();
	}
	@Nullable
	public static ShapeAspect getShape(ItemLike item) {
		return getAspects(item).shape();
	}
	@Nullable
	public static ShapeAspect getShape(@NotNull RegistryObject<? extends Item> item) {
		return getAspects(item).shape();
	}
	/**
	 * Gets the ShapeAspect of an item. Does not take stack size into consideration
	 * @param item
	 * @return ShapeAspect
	 */
	@Nullable
	public static ShapeAspect getShape(ItemStack item) {
		return getAspects(item).shape();
	}
	

	/**
	 * Gets the FormAspect of the item
	 * @param item
	 * @return FormAspect
	 */
	@Nullable
	public static FormAspect getForm(ItemData item) {
		return getAspects(item).form();
	}
	@Nullable
	public static FormAspect getForm(ItemLike item) {
		return getAspects(item).form();
	}
	@Nullable
	public static FormAspect getForm(RegistryObject<? extends Item> item) {
		return getAspects(item).form();
	}
	/**
	 * Gets the FormAspect of the item. Does not take stack size into consideration
	 * @param item
	 * @return FormAspect
	 */
	@Nullable
	public static FormAspect getForm(@NotNull ItemStack item) {
		return getAspects(item).form();
	}

	/**
	 * Checks whether the given transmuation has perfect flow
	 * @param from ItemData input
	 * @param to ItemData output
	 * @return True if the transmutation does not violate at all
	 */
	public static boolean flows(ItemData from, ItemData to) {
		return getAspects(from).flowsTo(getAspects(to));
	}
	public static boolean flows(ItemLike from, ItemLike to) {
		return getAspects(from).flowsTo(getAspects(to));
	}
	public static boolean flows(RegistryObject<? extends Item> from, @NotNull RegistryObject<? extends Item> to) {
		return getAspects(from).flowsTo(getAspects(to));
	}
	public static boolean flows(ItemStack from, ItemStack to) {
		return getAspects(from).flowsTo(getAspects(to));
	}
	

	/**
	 * Gets how violating the given transmutation is
	 * @param from ItemData input
	 * @param to ItemData output
	 * @return % violation of the transmutation
	 */
	public static float violation(ItemData from, ItemData to) {
		return getAspects(from).violationTo(getAspects(to));
	}
	public static float violation(ItemLike from, ItemLike to) {
		return getAspects(from).violationTo(getAspects(to));
	}
	public static float violation(RegistryObject<? extends Item> from, RegistryObject<? extends Item> to) {
		return getAspects(from).violationTo(getAspects(to));
	}
	public static float violation(ItemStack from, ItemStack to) {
		return getAspects(from).violationTo(getAspects(to));
	}
	
	/**
	 * Gets a list of possible targets for transmutation from the given input, sorted from least violating to most violating <br>
	 * Specifically, the list is of Tuple(ItemData,Float). ItemData is the target item, Float is the violation. <br>
	 * If the list is empty, there were no targets.
	 * @param from Input aspects
	 * @return List
	 */
	public static @NotNull List<Tuple<ItemData,Float>> getTransmutationTargets(AlchData from) {
		//Map<ItemData,AlchData> fmap = Maps.filterEntries(alchMap, (to) -> from.violationTo(to.getValue()) < 1);
		@NotNull List<Tuple<ItemData,Float>> flowList = new ArrayList<>();
		for (Entry<ItemData,AlchData> entry : alchMap.entrySet()) {
			flowList.add(new Tuple<>(entry.getKey(), from.violationTo(entry.getValue())));
		}
		flowList.sort((t1,t2) -> {
			float a = t1.getB(), b = t2.getB();
			if (a < b) return -1;
			if (a > b) return 1;
			return 0;
		});
		return flowList;
	}
	public static List<Tuple<ItemData,Float>> getTransmutationTargets(ItemData from) {
		return getTransmutationTargets(getAspects(from));
	}
	public static List<Tuple<ItemData,Float>> getTransmutationTargets(ItemLike from) {
		return getTransmutationTargets(getAspects(from));
	}
	public static List<Tuple<ItemData,Float>> getTransmutationTargets(RegistryObject<? extends Item> from) {
		return getTransmutationTargets(getAspects(from));
	}
	public static List<Tuple<ItemData,Float>> getTransmutationTargets(ItemStack from) {
		return getTransmutationTargets(getAspects(from));
	}
	
	public static Map<ResourceLocation,ItemData> getAllItems() {
		@NotNull Map<ResourceLocation,ItemData> allItems = new HashMap<>();
		for (Map.@NotNull Entry<ResourceKey<Item>,Item> ri : ForgeRegistries.ITEMS.getEntries()) {
			Logger.debug("getAllItems()", "DiscoveredItem", ri.getKey().location().toString());
			allItems.put(ri.getKey().location(), ItemData.fromItem(ri.getValue()));
		}
		return allItems;
	}
	
	@SuppressWarnings("null") // if there are null recipes, then i think crashing is fine
	public static Map<ResourceLocation, RecipeData> getAllRecipes(@NotNull ReloadableServerResources resources) {
		Map<ResourceLocation, RecipeData> allRecipes = new HashMap<>();
		for (RecipeType<?> recipeType : ForgeRegistries.RECIPE_TYPES) {
			List<? extends Recipe<?>> recipes = resources.getRecipeManager().getAllRecipesFor((RecipeType) recipeType);
			for (Recipe<?> recipe : recipes) {
				@Nullable RecipeData got = RecipeData.fromRecipe(recipe, RELOAD_DAT.regAccess());
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
		for (Map.@NotNull Entry<ResourceLocation, RecipeData> entry : allRecipes.entrySet()) {
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
		for (Map.@NotNull Entry<ResourceLocation, RecipeData> entry : allRecipes.entrySet()) {
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
	public static Map<ResourceLocation, RecipeData> searchRecipesForOutput(@NotNull ItemData search, Map<ResourceLocation, RecipeData> allRecipes) {
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
