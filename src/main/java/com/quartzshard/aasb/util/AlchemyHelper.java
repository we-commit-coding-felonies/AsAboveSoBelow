package com.quartzshard.aasb.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.quartzshard.aasb.config.AlchemyCfg;

public class AlchemyHelper {
	public static NonNullList<NbtWhitelistData> MAPPER_NBTS = NonNullList.create();
	public static NonNullList<LivingItemData> LIVING_ITEMS = NonNullList.create();

	
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
	
	public static boolean isItemAlive(ItemStack stack) {
		for (LivingItemData w : LIVING_ITEMS) {
			if (w.matches(stack.getItem().getRegistryName().toString())) {
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
}
