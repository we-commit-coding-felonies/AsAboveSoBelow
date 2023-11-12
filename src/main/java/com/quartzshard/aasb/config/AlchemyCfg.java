package com.quartzshard.aasb.config;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class AlchemyCfg {
	
	/**
	 * modify this to change the default mapper NBT whitelist
	 */
	public static final String[] DEFAULT_MAPPER_NBTS = {
			"*|Unbreakable",								// vanilla unbreakable item tag
			"*|Items",										// vanilla stored items tag (shulkers, bundles)
			"*|Enchantments",								// generic enchanted item tag
			"minecraft:enchanted_book|StoredEnchantments",	// enchants in a book
			"minecraft:potion|Potion",						// drinkable potion effect
			"minecraft:splash_potion|Potion",				// splash potion effect
			"minecraft:lingering_potion|Potion",			// lingering potion effect
	};
	
	/**
	 * modify this to change the default living items list
	 */
	public static final String[] DEFAULT_LIVING_ITEMS = {
			"minecraft:cod_bucket|*",						// Cod bucket, always
			"minecraft:salmon_bucket|*",					// Salmon bucket, always
			"minecraft:pufferfish_bucket|*",				// Pufferfish bucket, always
			"minecraft:tropical_fish_bucket|*",				// Clownfish bucket, always
			"minecraft:bee_nest|BlockEntityTag",			// Natural beehive, with TE data
			"minecraft:beehive|BlockEntityTag",				// Artificial beehive, with TE data
	};
	
	// Server
	public static ConfigValue<String> MAPPER_NBTS_STR;
	public static ConfigValue<String> LIVING_ITEMS_STR;
	public static void server(Builder cfg) {
		cfg.comment("Settings related to the underlying alchemy system.").push("alch");
		MAPPER_NBTS_STR = cfg
				.comment("A string defining what NBT tags the alchemy mapper should pay attention to (whitelist)",
						"Expects a specific format, and will likely cause problems if not formatted properly. Example is as follows:",
						"*|Tag1,minecraft:dirt|Tag2,minecraft:dirt|Tag3",
						"This will whitelist Tag1 for all items, and Tag2 & Tag3 only for the item 'minecraft:dirt",
						"Each whitelist entry is comma separated. If you want the mapper to totally ignore NBT, simply make the string empty.")
				.define("mapperNbtWhitelist", strArrayCat(DEFAULT_MAPPER_NBTS));
		LIVING_ITEMS_STR = cfg
				.comment("A string defining certain items as 'alive', giving consequences for using them in transmutation",
						"Expects a specific format, similar to that of the mapper NBT whitelist. Example:",
						"minecraft:grass|*,minecraft:dirt|AliveTag",
						"This will define all minecraft:grass as living, and any minecraft:dirt that has an AliveTag nbt",
						"Each entry is comma separated. If you want no items treated as alive, simply make the string empty.")
				.define("livingItemWhitelist", strArrayCat(DEFAULT_LIVING_ITEMS));
		cfg.pop();
	}
	
	
	
	public static String strArrayCat(String[] strs) {
		String cat = "";
		for (int i = 0; i < strs.length; i++) {
			if (i != 0)
				cat += ",";
			cat += strs[i];
		}
		return cat;
	}
}
