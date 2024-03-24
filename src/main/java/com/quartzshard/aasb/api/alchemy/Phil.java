package com.quartzshard.aasb.api.alchemy;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.net.client.MapperPacket;
import com.quartzshard.aasb.util.ListUtil;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.primitives.Longs;
import com.quartzshard.aasb.api.alchemy.aspect.*;
import com.quartzshard.aasb.config.AlchemyCfg;
import com.quartzshard.aasb.util.Logger;

import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
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

	public static void debugTestChangeMap(Map<ItemData,AlchData> newMap, ServerLevel level) {
		// troled
		alchMap = newMap;
		NetInit.toAllClients(level, new MapperPacket(serializeMap()));
	}
	public static CompoundTag serializeMap() {
		CompoundTag tag = new CompoundTag();
		int i = 0;
		for (Map.Entry<ItemData,AlchData> entry : alchMap.entrySet()) {
			CompoundTag entryTag = new CompoundTag();
			CompoundTag itemTag = new CompoundTag();
			itemTag.putString("I", ForgeRegistries.ITEMS.getKey(entry.getKey().getItem()).toString());
			itemTag.put("N", entry.getKey().getNBT());
			entryTag.put("K", itemTag);
			entryTag.put("V", entry.getValue().serialize());
			tag.put(""+i, entryTag);
			i++;
		}
		return tag;
	}
	public static void deserializeMap(CompoundTag nbt) {
		Map<ItemData,AlchData> map = new HashMap<>();
		for (String str : nbt.getAllKeys()) {
			CompoundTag entryTag = nbt.getCompound(str);
			map.put(
				ItemData.fromItem(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(entryTag.getCompound("K").getString("I")))),
				new AlchData(entryTag.getCompound("V"))
			);
		}
		alchMap = map;
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
	 * Gets the WayAspect of an item multiplied by stack size <br>
	 * Capped at Long.MAX_VALUE
	 * @param stack the ItemStack
	 * @return WayAspect
	 */
	@Nullable
	public static WayAspect getWayTotal(ItemStack stack) {
		WayAspect base = getWay(stack);
		if (base != null)
			return new WayAspect(base.value() * stack.getCount());
		return null;
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
	 * Gets the raw numerical value of the item's Way.
	 * @param item
	 * @return Numerical value of Way, or -1 if null
	 * @apiNote Does not take stack size into consideration
	 */
	public static long getWaySimple(ItemStack item) {
		return getWaySimple(ItemData.fromStack(item));
	}

	public static long getWayTotalSimple(ItemStack stack) {
		long way = getWaySimple(stack);
		return way == -1 ? -1 : way * stack.getCount();
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
	 * Gets the ShapeAspect of an item.
	 * @param item
	 * @return ShapeAspect
	 * @apiNote Does not take stack size into consideration
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
	 * Gets the FormAspect of the item.
	 * @param item
	 * @return FormAspect
	 * @apiNote Does not take stack size into consideration
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

	public static boolean flowsIgnoreWay(AlchData from, AlchData to) {
		if (!from.complexity().allowsNull() && !to.complexity().allowsNull()) {
			assert from.way() != null && to.way() != null
				&& from.shape() != null && to.shape() != null
				&& from.form() != null && to.form() != null
				: String.format("FROM|%s|, TO|%s|", from, to);
			return from.shape().flowsTo(to.shape())
				&& from.form().flowsTo(to.form());
		}
		return false;
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

	public record TransmutationData(AlchData input, List<FlowData> targets) {}

	public record FlowData(ItemStack stack, float shapeVio, float formVio) {}

	public static AlchData resolveToAspects(List<ItemStack> stacks) {
		List<AlchData> dats = new ArrayList<>();
		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) continue;
			AlchData aspects = getAspects(stack);
			if (aspects.complexity().allowsNull()) {
				return UNMAPPED; // we dont deal with nulls round these parts
			}
			assert aspects.way() != null;
			aspects = new AlchData(aspects.way().value()*stack.getCount(), aspects.shape(), aspects.form(), aspects.complexity());
			assert aspects.way() != null
				&& aspects.shape() != null
				&& aspects.form() != null
				: aspects;
			if (dats.isEmpty())
				dats.add(aspects);
			else {
				boolean exists = false;
				for (int i = 0; i < dats.size(); i++) {
					AlchData dat = dats.get(i);
					if (aspects.shape() == dat.shape()
							&& aspects.form() == dat.form()
							&& aspects.complexity() == dat.complexity()) {
						assert dat.way() != null : dat;
						dats.set(i, new AlchData(dat.way().value()+aspects.way().value(), dat.shape(), dat.form(), dat.complexity()));
						exists = true;
						break; // found a match, were done
					}
				}
				if (!exists) {
					dats.add(aspects);
				}
			}
		}
		if (!dats.isEmpty()) {
			if (dats.size() > 1) {
				AlchData biggest = getBiggest(dats);
				CompoundTag alchTag = biggest.serialize();
				List<AlchData> paths = new ArrayList<>(),
					next = new ArrayList<>();
				paths.add(biggest);
				dats.remove(biggest);
				while (!dats.isEmpty()) {
					for (int i = 0; i < dats.size(); i++) {
						AlchData dat = dats.get(i);
						if (dat == null) continue;
						assert dat.way() != null;
						for (AlchData path : paths) {
							if (flowsIgnoreWay(dat, path)) {
								if (dat.complexity() == ComplexityAspect.SIMPLE)
									next.add(dat);
								alchTag.putLong(AlchData.TK_SERWAY, alchTag.getLong(AlchData.TK_SERWAY) + dat.way().value());
								dats.set(i, null);
								break;
							}
						}
					}
					ListUtil.purgeNulls(dats);
					if (dats.isEmpty()) {
						// everything has found a flow path :D
						return new AlchData(alchTag);
					} else if (!next.isEmpty()) {
						// some things have not found a flow path, but we have new paths to check
						// we clear the old paths and swap in the new paths
						paths.clear();
						List<AlchData> tmp = paths;
						paths = next;
						next = tmp;
					} else {
						// no new paths, but we still have stuff left to resolve
						// this is a dead end so we stop immediately and return UNMAPPED
						break;
					}
				}
			} else return dats.get(0); // size of 1 means only 1 option
		}
		return UNMAPPED;
	}

	@NotNull
	private static AlchData getBiggest(List<AlchData> dats) {
		AlchData biggest = null;
		for (AlchData dat : dats) {
			if (!dat.complexity().allowsNull()) {
				assert dat.way() != null && dat.shape() != null && dat.form() != null;
				if (biggest != null && biggest.way().value() >= dat.way().value()) {
					continue;
				}
				biggest = dat;
			}
		}
		return biggest == null ? UNMAPPED : biggest;
		/*CompoundTag bigTag = null;
		for (AlchData dat : dats) {
			if (!dat.complexity().allowsNull()) {
				assert dat.way() != null && dat.shape() != null && dat.form() != null;
				if (bigTag != null) {
					long oldVal = bigTag.getLong(AlchData.TK_SERWAY),
						way = dat.way().value();
					bigTag.putLong(AlchData.TK_SERWAY, oldVal + way);
					if (oldVal >= way) {
						continue;
					}
				}
				bigTag = dat.serialize();
			}
		}
		return bigTag == null ? UNMAPPED : new AlchData(bigTag);*/
	}

	/**
	 * Gets a the possible targets for transmutation from the given input, sorted from least violating to most violating <br>
	 * Specifically, the list is of Tuple(ItemData,Float). ItemData is the target item, Float is the violation. <br>
	 * If the list is empty, there were no targets.
	 * @param input Input aspects
	 * @param maxStack The maximum stack size of the outputs (how
	 * @return List
	 */
	public static TransmutationData getTransmutationTargets(AlchData input, int maxStack) {
		List<FlowData> targets = new ArrayList<>();
		int size = 1;
		// attempting calculation of transmutation targets for null aspects should never happen
		assert input.way() != null
			&& input.shape() != null
			&& input.form() != null
			: input;
		long way = input.way().value();
		while (size <= maxStack) {
			AlchData checkDat;
			for (Map.Entry<ItemData,AlchData> entry : alchMap.entrySet()) {
				ItemData outItem = entry.getKey();
				if (size > outItem.createStack().getMaxStackSize())
					continue;
				AlchData outAlch = entry.getValue();
				float shapeVio = input.shape().violationTo(outAlch.shape()),
					formVio = input.form().violationTo(outAlch.form());
				double fWay = (double)way * (1d - (shapeVio + formVio));
				long realWay = (long) Math.floor(fWay);
				if (realWay > 0 && realWay % size == 0) {
					checkDat = new AlchData(realWay/size, input.shape(), input.form(), input.complexity());
					if (checkDat.violationTo(outAlch) < Float.POSITIVE_INFINITY) {
						ItemStack stack = new ItemStack(outItem.getItem());
						stack.setCount(size);
						if (outItem.hasNBT()) {
							stack.setTag(outItem.getNBT().copy());
						}
						targets.add(new FlowData(stack, shapeVio, formVio));
					}
				}
			}
			size++;
		}
		targets.sort((a, b) -> {
			float av = a.shapeVio+a.formVio, bv = b.shapeVio+b.formVio;
			if (av < bv)
				return -1;
			else if (av == bv)
				return 0;
			return 1;
		});
		return new TransmutationData(input, targets);
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
