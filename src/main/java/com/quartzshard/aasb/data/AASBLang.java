package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import net.minecraftforge.common.data.LanguageProvider;

public class AASBLang {
	public static final AASBLang INSTANCE = new AASBLang();
	private static String id(String template) {
		return String.format(template, AsAboveSoBelow.MODID);
	}
	
	/**
	 * text component containing a single space character <br>
	 * works like a newline in tooltips
	 */
	public static final Component NL = new TextComponent(" ");
	
	public static final String
		CREATIVE_TAB = id("itemGroup.%s"),

		ASPECT_WAY = id("misc.%s.aspect.way"),
		ASPECT_SHAPE = id("misc.%s.aspect.shape"),
		ASPECT_SHAPE_WATER = id("misc.%s.aspect.shape.water"),
		ASPECT_SHAPE_EARTH = id("misc.%s.aspect.shape.earth"),
		ASPECT_SHAPE_FIRE = id("misc.%s.aspect.shape.fire"),
		ASPECT_SHAPE_AIR = id("misc.%s.aspect.shape.air"),
		ASPECT_FORM = id("misc.%s.aspect.form"),
		
		KEY_HEADMODE = id("key.%s.headMode"),
		KEY_CHESTMODE = id("key.%s.chestMode"),
		KEY_LEGSMODE = id("key.%s.legsMode"),
		KEY_FEETMODE = id("key.%s.feetMode"),
		KEY_ITEMMODE = id("key.%s.itemMode"),
		KEY_ITEMFUNC_1 = id("key.%s.itemFunc.1"),
		KEY_ITEMFUNC_2 = id("key.%s.itemFunc.2"),
		KEY_EMPOWER = id("key.%s.empower"),
		
		TIP_GENERIC_ON = id("tip.%s.generic.on"),
		TIP_GENERIC_OFF = id("tip.%s.generic.off"),
		TIP_GENERIC_MODE = id("tip.%s.generic.mode"),
		
		TIP_DM_RUNE = id("tip.%s.dm.rune"),
		TIP_DM_RUNE_MULTI = id("tip.%s.dm.rune.multi"),
		
		TIP_DM_ARMOR_FLAVOR = id("tip.%s.dm.armor.flavor"),
		TIP_DM_ARMOR_DESC_1 = id("tip.%s.dm.armor.desc.1"),
		TIP_DM_ARMOR_DESC_2 = id("tip.%s.dm.armor.desc.2"),
		TIP_DM_ARMOR_DESC_3 = id("tip.%s.dm.armor.desc.3"),
		TIP_DM_ARMOR_DR = id("tip.%s.dm.armor.dr"),
		TIP_DM_ARMOR_BURNOUT = id("tip.%s.dm.armor.burnout"),
		
		TIP_DM_SWORD_FLAVOR = id("tip.%s.dm.sword.flavor"),
		TIP_DM_SWORD_DESC = id("tip.%s.dm.sword.desc"),
		TIP_DM_SWORD_KILLMODE = id("tip.%s.dm.sword.killMode"),
		TIP_DM_SWORD_KILLMODE_DESC = id("tip.%s.dm.sword.killMode.desc"),
		TIP_DM_SWORD_KILLMODE_HOSTILE = id("tip.%s.dm.sword.killMode.hostile"),
		TIP_DM_SWORD_KILLMODE_HOSTILEPLAYER = id("tip.%s.dm.sword.killMode.hostilePlayer"),
		TIP_DM_SWORD_KILLMODE_NOTPLAYER = id("tip.%s.dm.sword.killMode.notPlayer"),
		TIP_DM_SWORD_KILLMODE_ALL = id("tip.%s.dm.sword.killMode.all"),
		
		TIP_DM_PICKAXE_FLAVOR = id("tip.%s.dm.pickaxe.flavor"),
		TIP_DM_PICKAXE_DESC = id("tip.%s.dm.pickaxe.desc"),
		
		TIP_DM_SHOVEL_FLAVOR = id("tip.%s.dm.shovel.flavor"),
		TIP_DM_SHOVEL_DESC = id("tip.%s.dm.shovel.desc"),
		
		TIP_DM_AXE_FLAVOR = id("tip.%s.dm.axe.flavor"),
		TIP_DM_AXE_DESC = id("tip.%s.dm.axe.desc"),
		
		TIP_DM_HOE_FLAVOR = id("tip.%s.dm.hoe.flavor"),
		TIP_DM_HOE_DESC = id("tip.%s.dm.hoe.desc"),
		TIP_DM_HOE_MODE = id("tip.%s.dm.hoe.mode"),
		TIP_DM_HOE_MODE_TILL = id("tip.%s.dm.hoe.mode.till"),
		TIP_DM_HOE_MODE_TILL_LONG = id("tip.%s.dm.hoe.mode.till.long"),
		TIP_DM_HOE_MODE_PATH = id("tip.%s.dm.hoe.mode.path"),
		TIP_DM_HOE_MODE_PATH_LONG = id("tip.%s.dm.hoe.mode.path.long"),
		TIP_DM_HOE_MODE_CULL = id("tip.%s.dm.hoe.mode.cull"),
		TIP_DM_HOE_MODE_CULL_LONG = id("tip.%s.dm.hoe.mode.cull.long"),
		
		TIP_DM_TOOL_ENCHBONUS_FLAVOR = id("tip.%s.dm.tool.enchBonus.flavor"),
		TIP_DM_TOOL_ENCHBONUS_DESC = id("tip.%s.dm.enchBonus.desc"),
		TIP_DM_TOOL_ENCHBONUS_VAL = id("tip.%s.dm.enchBonus.val"),

		TIP_DM_TOOL_STATICDIG = id("tip.%s.dm.tool.staticDig"),
		TIP_DM_TOOL_STATICDIG_DESC = id("tip.%s.dm.tool.staticDig.desc"),
		TIP_DM_TOOL_STATICDIG_STATE =  id("tip.%s.dm.tool.staticDig.state"),
				
		TIP_DM_TOOL_EMPOWER_DESC = id("tip.%s.dm.tool.empower.desc"),
		TIP_DM_TOOL_EMPOWER_GUIDE = id("tip.%s.dm.tool.empower.guide");

	public static Component tc(String key, Object... args) {
		return new TranslatableComponent(key, args);
	}
	
	/**
	 * blank, useful as a newline in tooltips
	 * @param key
	 * @param args
	 * @return
	 */
	public static Component nl(String key, Object... args) {
		return new TextComponent(" ");
	}
	
	public class Provider extends LanguageProvider {
		public Provider(DataGenerator gen, String locale) {
			super(gen, AsAboveSoBelow.MODID, locale);
		}

		@Override
		protected void addTranslations() {
			// Mod stuff
			add(CREATIVE_TAB, "As Above, So Below");
			
			add(ASPECT_WAY, "Way");
			
			add(ASPECT_SHAPE, "Shape");
			add(ASPECT_SHAPE_WATER, "Water");
			add(ASPECT_SHAPE_EARTH, "Earth");
			add(ASPECT_SHAPE_FIRE, "Fire");
			add(ASPECT_SHAPE_AIR, "Air");
			
			add(ASPECT_FORM, "Form");
			
			// Keybinds
			add(KEY_HEADMODE, "Circlet mode");
			add(KEY_CHESTMODE, "Amulet mode");
			add(KEY_LEGSMODE, "Timepiece mode");
			add(KEY_FEETMODE, "Anklet mode");
			add(KEY_ITEMMODE, "Item mode");
			add(KEY_ITEMFUNC_1, "Primary ability");
			add(KEY_ITEMFUNC_2, "Secondary ability");
			add(KEY_EMPOWER, "Empower item");
			
			// Items
			add(ObjectInit.Items.PHILOSOPHERS_STONE.get(), "The Philosopher's Stone");
			add(ObjectInit.Items.MINIUM_STONE.get(), "Minium Stone");
			add(ObjectInit.Items.ELIXIR_OF_LIFE.get(), "Elixir of Life");
			add(ObjectInit.Items.DARK_MATTER_HELMET.get(), "Dark-Matter Helmet");
			add(ObjectInit.Items.DARK_MATTER_CHESTPLATE.get(), "Dark-Matter Chestplate");
			add(ObjectInit.Items.DARK_MATTER_LEGGINGS.get(), "Dark-Matter Leggings");
			add(ObjectInit.Items.DARK_MATTER_BOOTS.get(), "Dark-Matter Boots");
			add(ObjectInit.Items.DARK_MATTER_SWORD.get(), "Dark-Matter Sword");
			
			// Blocks
			add(ObjectInit.Blocks.WAYSTONE.get(), "Waystone");
			add(ObjectInit.Blocks.AIR_ICE.get(), "Frozen Air");
			
			// Tooltips
			add(TIP_GENERIC_ON, "Enabled");
			add(TIP_GENERIC_OFF, "Disabled");
			add(TIP_GENERIC_MODE, "%s: %s");

			add(TIP_DM_RUNE, "Rune of %s");
			add(TIP_DM_RUNE_MULTI, "Runes of %s & %s");
			
			add(TIP_DM_ARMOR_FLAVOR, "Become a living fortress with the infalliable Ultrashield");
			add(TIP_DM_ARMOR_DESC_1, "Provides enormous amounts of protection by absorbing damage");
			add(TIP_DM_ARMOR_DESC_2, "Protection decreases with more damage absorbed, regenerates over time");
			add(TIP_DM_ARMOR_DESC_3, "Will violently release all stored energy at once if it absorbs to much");
			add(TIP_DM_ARMOR_DR, "Currently providing a %s damage reduction");
			add(TIP_DM_ARMOR_BURNOUT, "Burnout: %s/%s");
			
			add(TIP_DM_SWORD_FLAVOR, "Decimate your foes with the powerful Autoslash");
			add(TIP_DM_SWORD_DESC, "Press %s to rapidly attack nearby creatures");
			add(TIP_DM_SWORD_KILLMODE, "Target");
			add(TIP_DM_SWORD_KILLMODE_DESC, "Currently targeting %s, change with %s");
			add(TIP_DM_SWORD_KILLMODE_HOSTILE, "Hostile only");
			add(TIP_DM_SWORD_KILLMODE_HOSTILEPLAYER, "Hostile & Players");
			add(TIP_DM_SWORD_KILLMODE_NOTPLAYER, "All except Players");
			add(TIP_DM_SWORD_KILLMODE_ALL, "Everything");
			
			add(TIP_DM_PICKAXE_FLAVOR, "Make stripmining a breeze with the handy Proximine");
			add(TIP_DM_PICKAXE_DESC, "Press %s to instantly collect all nearby ores");

			add(TIP_DM_SHOVEL_FLAVOR, "Level entire mountains with the arguably safe Areablast");
			add(TIP_DM_SHOVEL_DESC, "Press %s to violently excavate a large area");

			add(TIP_DM_AXE_FLAVOR, "Turn forests into plains with the controversial Supercut");
			add(TIP_DM_AXE_DESC, "Press %s to quickly harvest all nearby tree blocks");

			add(TIP_DM_HOE_FLAVOR, "Expedite landscaping with the useful Hyperscythe");
			add(TIP_DM_HOE_DESC, "Press %s to %s, %s to change operation");
			add(TIP_DM_HOE_MODE, "Operation");
			add(TIP_DM_HOE_MODE_TILL, "Irrigation");
			add(TIP_DM_HOE_MODE_TILL_LONG, "till soil");
			add(TIP_DM_HOE_MODE_PATH, "Pathmaking");
			add(TIP_DM_HOE_MODE_PATH_LONG, "create paths");
			add(TIP_DM_HOE_MODE_CULL, "Weedkiller");
			add(TIP_DM_HOE_MODE_CULL_LONG, "destroy foliage");
			
			add(TIP_DM_TOOL_ENCHBONUS_FLAVOR, "Outclass everything else with the help of enchanting");
			add(TIP_DM_TOOL_ENCHBONUS_DESC, "Gains large boosts to base stats when enchanted");
			add(TIP_DM_TOOL_ENCHBONUS_VAL, "Enchantment potency: %s");

			add(TIP_DM_TOOL_STATICDIG, "Dig Stabilizer");
			add(TIP_DM_TOOL_STATICDIG_DESC, "Dig Stabilizer is avaliable, allowing for precision block breaking");
			add(TIP_DM_TOOL_STATICDIG_STATE, "It is currently %s, toggle with %s");
			
			add(TIP_DM_TOOL_EMPOWER_DESC, "Abilities require empowerment to function");
			add(TIP_DM_TOOL_EMPOWER_GUIDE, "Hold %s to empower with Way");
			
			// Death Messages
			
			// Subtitles
		}
		
		


		@Override
		public String getName() {
			return AsAboveSoBelow.DISPLAYNAME + " | Localization";
		}
	}
}
