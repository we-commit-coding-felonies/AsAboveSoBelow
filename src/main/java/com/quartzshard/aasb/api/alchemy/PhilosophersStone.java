package com.quartzshard.aasb.api.alchemy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handles much of the backend for transmutation <br>
 * not to be confused with the in-game item, The Philosopher's Stone
 * @author quartzshard
 */
public class PhilosophersStone {
	public static final PhilosophersStone INSTANCE = new PhilosophersStone();
	
	private HashMap<ItemData,AlchemicProperties> itemAlchMap = new HashMap<ItemData,AlchemicProperties>();
	
	public static Map<ResourceLocation,Item> getAllItems() {
		Map<ResourceLocation,Item> allItems = new LinkedHashMap<>();
		for (Map.Entry<ResourceKey<Item>,Item> ri : ForgeRegistries.ITEMS.getEntries()) {
			//System.out.println(ri.toString());
			allItems.put(ri.getKey().location(), ri.getValue());
		}
		return allItems;
	}
}
