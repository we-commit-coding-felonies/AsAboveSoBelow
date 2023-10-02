package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import net.minecraftforge.common.data.LanguageProvider;

public class AASBLang {
	public static final AASBLang INSTANCE = new AASBLang();
	private static String id(String template) {
		return String.format(template, AsAboveSoBelow.MODID);
	}
	
	private static String f(String name) {
		return id("misc.%s.aspect.form."+name);
	}
	
	private static String dm(String template) {
		return dm(template, false);
	}
	private static String dm(String template, boolean suffix) {
		return dm(template, suffix, false);
	}
	private static String edm(String template, boolean suffix) {
		return dm(template, suffix, true);
	}
	private static String dm(String template, boolean suffix, boolean entity) {
		String id = "death.attack."+template;
		if (suffix) {
			id = id + (entity ? ".item" : ".player");
		}
		return id;
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
		SHAPE_WATER = id("misc.%s.aspect.shape.water"),
		SHAPE_EARTH = id("misc.%s.aspect.shape.earth"),
		SHAPE_FIRE = id("misc.%s.aspect.shape.fire"),
		SHAPE_AIR = id("misc.%s.aspect.shape.air"),
		SHAPE_QUINTESSENCE = id("misc.%s.aspect.shape.quintessence"),

		ASPECT_FORM = id("misc.%s.aspect.form"),
		FORM_MATERIA = f("materia"),
			FORM_TERRAIN = f("terrain"),
				FORM_SOIL = f("soil"),
				FORM_ROCK = f("rock"),
					FORM_ROUGH = f("rough"),
					FORM_SMOOTH = f("smooth"),
			FORM_MINERAL = f("mineral"),
				FORM_METAL = f("metal"),
					FORM_SUN = f("aurum"),
					FORM_MOON = f("argentum"),
					FORM_VENUS = f("cuprum"),
					FORM_MARS = f("ferrum"),
					FORM_JUPITER = f("stannum"),
					FORM_SATURN = f("plumbum"),
				FORM_CRYSTAL = f("crystal"),
					FORM_BRILLIANT = f("brilliant"),
					FORM_DULL = f("dull"),
			FORM_ORGANIC = f("organic"),
				FORM_DEAD = f("inanimate"),
				FORM_ALIVE = f("living"),
					FORM_PLANT = f("plant"),
						FORM_FOLIAGE = f("foliage"),
					FORM_BEAST = f("beast"),
						FORM_ANIMAL = f("animal"),
						FORM_MONSTER = f("monster"),
				FORM_IMMORTAL = f("immortal"),
			FORM_ARCANE = f("arcane"),
				FORM_ALCHEMY = f("alchemy"),
				FORM_ENCHANTING = f("enchanting"),
				FORM_WITCHCRAFT = f("witchcraft"),
			FORM_ETHEREAL = f("ethereal"),
				FORM_MIND = f("mind"),
				FORM_SOUL = f("soul"),
		
		KEY_HEADMODE = id("key.%s.headMode"),
		KEY_CHESTMODE = id("key.%s.chestMode"),
		KEY_LEGSMODE = id("key.%s.legsMode"),
		KEY_FEETMODE = id("key.%s.feetMode"),
		KEY_ITEMMODE = id("key.%s.itemMode"),
		KEY_ITEMFUNC_1 = id("key.%s.itemFunc.1"),
		KEY_ITEMFUNC_2 = id("key.%s.itemFunc.2"),
		KEY_EMPOWER = id("key.%s.empower"),
		
		CMD_ERROR_INVALID_NUMBER = id("command.%s.error.invalid_number"),
		
		TIP_GENERIC_ON = id("tip.%s.generic.on"),
		TIP_GENERIC_OFF = id("tip.%s.generic.off"),
		TIP_GENERIC_MODE = id("tip.%s.generic.mode"),
		TIP_GENERIC_MOREINFO = id("tip.%s.generic.moreInfo"),

		TIP_FLASK_ASPECTS = id("tip.%s.flask.aspects"),
		TIP_FLASK_ASPECTS_ONE = id("tip.%s.flask.aspects.one"),
		TIP_FLASK_EXPIRY = id("tip.%s.flask.expiry"),
		TIP_FLASK_BAD = id("tip.%s.flask.bad"),
		
		TIP_HERM_RUNE = id("tip.%s.herm.rune"),
		TIP_HERM_RUNE_MULTI = id("tip.%s.herm.rune.multi"),
		
		TIP_HERM_ARMOR_FLAVOR = id("tip.%s.herm.armor.flavor"),
		TIP_HERM_ARMOR_DESC_1 = id("tip.%s.herm.armor.desc.1"),
		TIP_HERM_ARMOR_DESC_2 = id("tip.%s.herm.armor.desc.2"),
		TIP_HERM_ARMOR_DESC_3 = id("tip.%s.herm.armor.desc.3"),
		TIP_HERM_ARMOR_DR = id("tip.%s.herm.armor.dr"),
		TIP_HERM_ARMOR_BURNOUT = id("tip.%s.herm.armor.burnout"),
		
		TIP_HERM_SWORD_FLAVOR = id("tip.%s.herm.sword.flavor"),
		TIP_HERM_SWORD_DESC = id("tip.%s.herm.sword.desc"),
		TIP_HERM_SWORD_KILLMODE = id("tip.%s.herm.sword.killMode"),
		TIP_HERM_SWORD_KILLMODE_DESC = id("tip.%s.herm.sword.killMode.desc"),
		TIP_HERM_SWORD_KILLMODE_HOSTILE = id("tip.%s.herm.sword.killMode.hostile"),
		TIP_HERM_SWORD_KILLMODE_HOSTILEPLAYER = id("tip.%s.herm.sword.killMode.hostilePlayer"),
		TIP_HERM_SWORD_KILLMODE_NOTPLAYER = id("tip.%s.herm.sword.killMode.notPlayer"),
		TIP_HERM_SWORD_KILLMODE_ALL = id("tip.%s.herm.sword.killMode.all"),
		
		TIP_HERM_PICKAXE_FLAVOR = id("tip.%s.herm.pickaxe.flavor"),
		TIP_HERM_PICKAXE_DESC = id("tip.%s.herm.pickaxe.desc"),
		
		TIP_HERM_SHOVEL_FLAVOR = id("tip.%s.herm.shovel.flavor"),
		TIP_HERM_SHOVEL_DESC = id("tip.%s.herm.shovel.desc"),
		
		TIP_HERM_AXE_FLAVOR = id("tip.%s.herm.axe.flavor"),
		TIP_HERM_AXE_DESC = id("tip.%s.herm.axe.desc"),
		
		TIP_HERM_HOE_FLAVOR = id("tip.%s.herm.hoe.flavor"),
		TIP_HERM_HOE_DESC = id("tip.%s.herm.hoe.desc"),
		TIP_HERM_HOE_MODE = id("tip.%s.herm.hoe.mode"),
		TIP_HERM_HOE_MODE_TILL = id("tip.%s.herm.hoe.mode.till"),
		TIP_HERM_HOE_MODE_TILL_LONG = id("tip.%s.herm.hoe.mode.till.long"),
		TIP_HERM_HOE_MODE_PATH = id("tip.%s.herm.hoe.mode.path"),
		TIP_HERM_HOE_MODE_PATH_LONG = id("tip.%s.herm.hoe.mode.path.long"),
		TIP_HERM_HOE_MODE_CULL = id("tip.%s.herm.hoe.mode.cull"),
		TIP_HERM_HOE_MODE_CULL_LONG = id("tip.%s.herm.hoe.mode.cull.long"),
		
		TIP_HERM_TOOL_ENCHBONUS_FLAVOR = id("tip.%s.herm.tool.enchBonus.flavor"),
		TIP_HERM_TOOL_ENCHBONUS_DESC = id("tip.%s.herm.enchBonus.desc"),
		TIP_HERM_TOOL_ENCHBONUS_VAL = id("tip.%s.herm.enchBonus.val"),

		TIP_HERM_TOOL_STATICDIG = id("tip.%s.herm.tool.staticDig"),
		TIP_HERM_TOOL_STATICDIG_DESC = id("tip.%s.herm.tool.staticDig.desc"),
		TIP_HERM_TOOL_STATICDIG_STATE =  id("tip.%s.herm.tool.staticDig.state"),
				
		TIP_HERM_TOOL_EMPOWER_DESC = id("tip.%s.herm.tool.empower.desc"),
		TIP_HERM_TOOL_EMPOWER_GUIDE = id("tip.%s.herm.tool.empower.guide"),
		
		// Death Messages
		DM_AUTOSLASH = "autoslash",
		DM_GOD = "divine",
		DM_MUSTANG = "mustang",
		DM_SURFACE_TENSION = "surface_tension",
		DM_TRANSMUTING = "transmutation",
		DM_TRANSMUTING_2 = "transmutation.strong",
		DM_TRANSMUTING_POT = "transmutation.indirect",
		DM_WAYBOMB = "waybomb",
		DM_WAYBOMB_OOPS = "waybomb.accident";

	public static MutableComponent tc(String key, Object... args) {
		return new TranslatableComponent(key, args);
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
			add(SHAPE_WATER, "Water");
			add(SHAPE_EARTH, "Earth");
			add(SHAPE_FIRE, "Fire");
			add(SHAPE_AIR, "Air");
			add(SHAPE_QUINTESSENCE, "Quintessence");

			add(ASPECT_FORM, "Form");
			add(FORM_MATERIA, "Materia");
				add(FORM_TERRAIN, "Terrain");
					add(FORM_SOIL, "Soil");
					add(FORM_ROCK, "Rock");
						add(FORM_ROUGH, "Rough");
						add(FORM_SMOOTH, "Smooth");
				add(FORM_MINERAL, "Mineral");
					add(FORM_METAL, "Metal");
						add(FORM_SUN, "Aurum");
						add(FORM_MOON, "Argentum");
						add(FORM_VENUS, "Cuprum");
						add(FORM_MARS, "Ferrum");
						add(FORM_JUPITER, "Stannum");
						add(FORM_SATURN, "Plumbum");
					add(FORM_CRYSTAL, "Crystal");
						add(FORM_BRILLIANT, "Brilliant");
						add(FORM_DULL, "Dull");
				add(FORM_ORGANIC, "Organic");
					add(FORM_DEAD, "Inanimate");
					add(FORM_ALIVE, "Living");
						add(FORM_PLANT, "Plant");
							add(FORM_FOLIAGE, "Foliage");
						add(FORM_BEAST, "Beast");
							add(FORM_ANIMAL, "Animal");
							add(FORM_MONSTER, "Monster");
					add(FORM_IMMORTAL, "Immortal");
				add(FORM_ARCANE, "Arcane");
					add(FORM_ALCHEMY, "Alchemy");
					add(FORM_ENCHANTING, "Enchanting");
					add(FORM_WITCHCRAFT, "Witchcraft");
				add(FORM_ETHEREAL, "Ethereal");
					add(FORM_MIND, "Mind");
					add(FORM_SOUL, "Soul");
			
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
			add(ObjectInit.Items.ASH.get(), "Ash");
			add(ObjectInit.Items.SOOT.get(), "Soot");
			add(ObjectInit.Items.SALT.get(), "Salt");
			add(ObjectInit.Items.SPUT.get(), "Sput");
			add(ObjectInit.Items.AETHER.get(), "Aether");
			add(ObjectInit.Items.QUINTESSENCE.get(), "Quintessential Condensate");
			add(ObjectInit.Items.MATERIA_1.get(), "Materia Infirma");
			add(ObjectInit.Items.MATERIA_2.get(), "Materia Minor");
			add(ObjectInit.Items.MATERIA_3.get(), "Materia Modica");
			add(ObjectInit.Items.MATERIA_4.get(), "Materia Major");
			add(ObjectInit.Items.MATERIA_5.get(), "Materia Prima");
			add(ObjectInit.Items.PHILOSOPHERS_STONE.get(), "The Philosopher's Stone");
			add(ObjectInit.Items.MINIUM_STONE.get(), "Minium Stone");
			add(ObjectInit.Items.ELIXIR_OF_LIFE.get(), "Elixir of Life");
			add(ObjectInit.Items.LOOT_BALL.get(), "Complex Mass");
			add(ObjectInit.Items.FLASK_LEAD.get(), "Lead Flask");
			add(ObjectInit.Items.FLASK_GOLD.get(), "Golden Flask");
			add(ObjectInit.Items.FLASK_AETHER.get(), "Aetherglass Flask");
			add(ObjectInit.Items.HERMETIC_HELMET.get(), "Hermetic Armet");
			add(ObjectInit.Items.HERMETIC_CHESTPLATE.get(), "Hermetic Cuirass");
			add(ObjectInit.Items.HERMETIC_LEGGINGS.get(), "Hermetic Greaves");
			add(ObjectInit.Items.HERMETIC_BOOTS.get(), "Hermetic Sabatons");
			add(ObjectInit.Items.HERMETIC_SWORD.get(), "Hermetic Blade");
			add(ObjectInit.Items.HERMETIC_PICKAXE.get(), "Hermetic Hammer");
			add(ObjectInit.Items.HERMETIC_SHOVEL.get(), "Hermetic Spade");
			add(ObjectInit.Items.HERMETIC_AXE.get(), "Hermetic Hatchet");
			add(ObjectInit.Items.HERMETIC_HOE.get(), "Hermetic Scythe");
			add(ObjectInit.Items.OMNITOOL.get(), "Test item, please ignore.");
			add(ObjectInit.Items.CIRCLET.get(), "Circlet of the Seer");
			add(ObjectInit.Items.AMULET.get(), "Amulet of the Philosopher");
			add(ObjectInit.Items.POCKETWATCH.get(), "Watch of the Astrologer");
			add(ObjectInit.Items.ANKLET.get(), "Anklet of the Prophet");
			
			// Blocks
			add(ObjectInit.Blocks.ASH_STONE.get(), "Ashen Stone");
			add(ObjectInit.Blocks.WAYSTONE.get(), "Waystone");
			add(ObjectInit.Blocks.AIR_ICE.get(), "Frozen Air");
			
			// Tooltips
			add(TIP_GENERIC_ON, "Enabled");
			add(TIP_GENERIC_OFF, "Disabled");
			add(TIP_GENERIC_MODE, "%s: %s");
			add(TIP_GENERIC_MOREINFO, "Hold %s for more information");

			add(TIP_FLASK_ASPECTS, "Solution of %s & %s");
			add(TIP_FLASK_ASPECTS_ONE, "Solution of pure %s");
			add(TIP_FLASK_EXPIRY, "Expires in %s");
			add(TIP_FLASK_BAD, "Expired!");
			
			add(TIP_HERM_RUNE, "Rune of %s");
			add(TIP_HERM_RUNE_MULTI, "Runes of %s & %s");
			
			add(TIP_HERM_ARMOR_FLAVOR, "Become a living fortress with the infalliable Ultrashield");
			add(TIP_HERM_ARMOR_DESC_1, "Provides enormous amounts of protection by absorbing damage");
			add(TIP_HERM_ARMOR_DESC_2, "Protection decreases with more damage absorbed, regenerates over time");
			add(TIP_HERM_ARMOR_DESC_3, "Will violently release all stored energy at once if it absorbs to much");
			add(TIP_HERM_ARMOR_DR, "Currently providing a %s damage reduction");
			add(TIP_HERM_ARMOR_BURNOUT, "Burnout: %s/%s");
			
			add(TIP_HERM_SWORD_FLAVOR, "Decimate your foes with the powerful Autoslash");
			add(TIP_HERM_SWORD_DESC, "Press %s to rapidly attack nearby creatures");
			add(TIP_HERM_SWORD_KILLMODE, "Target");
			add(TIP_HERM_SWORD_KILLMODE_DESC, "Currently targeting %s, change with %s");
			add(TIP_HERM_SWORD_KILLMODE_HOSTILE, "Hostile only");
			add(TIP_HERM_SWORD_KILLMODE_HOSTILEPLAYER, "Hostile & Players");
			add(TIP_HERM_SWORD_KILLMODE_NOTPLAYER, "All except Players");
			add(TIP_HERM_SWORD_KILLMODE_ALL, "Everything");
			
			add(TIP_HERM_PICKAXE_FLAVOR, "Make stripmining a breeze with the handy Proximine");
			add(TIP_HERM_PICKAXE_DESC, "Press %s to instantly collect all nearby ores");

			add(TIP_HERM_SHOVEL_FLAVOR, "Level entire mountains with the arguably safe Areablast");
			add(TIP_HERM_SHOVEL_DESC, "Press %s to violently excavate a large area");

			add(TIP_HERM_AXE_FLAVOR, "Turn forests into plains with the controversial Supercut");
			add(TIP_HERM_AXE_DESC, "Press %s to quickly harvest all nearby tree blocks");

			add(TIP_HERM_HOE_FLAVOR, "Expedite landscaping with the useful Hypersickle");
			add(TIP_HERM_HOE_DESC, "Press %s to %s, %s to change operation");
			add(TIP_HERM_HOE_MODE, "Operation");
			add(TIP_HERM_HOE_MODE_TILL, "Irrigation");
			add(TIP_HERM_HOE_MODE_TILL_LONG, "till soil");
			add(TIP_HERM_HOE_MODE_PATH, "Pathmaking");
			add(TIP_HERM_HOE_MODE_PATH_LONG, "create paths");
			add(TIP_HERM_HOE_MODE_CULL, "Weedkiller");
			add(TIP_HERM_HOE_MODE_CULL_LONG, "destroy foliage");
			
			add(TIP_HERM_TOOL_ENCHBONUS_FLAVOR, "Outclass everything else with the help of enchanting");
			add(TIP_HERM_TOOL_ENCHBONUS_DESC, "Gains large boosts to base stats when enchanted, scaling with charge");
			add(TIP_HERM_TOOL_ENCHBONUS_VAL, "Enchantment potency: %s");

			add(TIP_HERM_TOOL_STATICDIG, "Dig Stabilizer");
			add(TIP_HERM_TOOL_STATICDIG_DESC, "Dig Stabilizer is avaliable, allowing for precision block breaking");
			add(TIP_HERM_TOOL_STATICDIG_STATE, "It is currently %s, toggle with %s");
			
			add(TIP_HERM_TOOL_EMPOWER_DESC, "Abilities require empowerment to function");
			add(TIP_HERM_TOOL_EMPOWER_GUIDE, "Hold %s to empower with Way");
			
			// Death Messages
			add(dm(DM_AUTOSLASH), "%s was decimated by %s");
			add(edm(DM_AUTOSLASH, true), "%s got butchered by %s's %s");
			add(dm(DM_GOD), "%s became a work of fiction");
			add(dm(DM_GOD, true), "%s was retconned after losing a fight with %s");
			add(dm(DM_MUSTANG), "%s was phlogistonated by %s");
			add(edm(DM_MUSTANG, true), "%s was reduced to ash by %s's %s");
			add(dm(DM_SURFACE_TENSION), "%s learned a painful lesson about surface tension");
			add(dm(DM_SURFACE_TENSION, true), "%s was proven guilty of witchcraft by %s");
			add(dm(DM_TRANSMUTING), "%s was turned into an eldritch horror by %s");
			add(edm(DM_TRANSMUTING, true), "%s was inhumanely transmuted by %s using %s");
			add(dm(DM_TRANSMUTING_2), "%s was told the story of King Midas by %s");
			add(edm(DM_TRANSMUTING_2, true), "%s underwent chrysopoeia because of %s's %s");
			add(dm(DM_TRANSMUTING_POT), "%s lost their Way");
			add(dm(DM_TRANSMUTING_POT, true), "%s became unrecognizable whilst fighting %s");
			add(dm(DM_WAYBOMB), "%s's aspects were annihilated at the hands of %s");
			add(edm(DM_WAYBOMB, true), "%s's aspects were annihilated at the hands of %s");
			add(dm(DM_WAYBOMB_OOPS), "%s was vaporized when %s underwent alchemical fission");
			add(edm(DM_WAYBOMB_OOPS, true), "%s was vaporized when %s underwent alchemical fission");
			
			// Subtitles
		}
		
		


		@Override
		public String getName() {
			return AsAboveSoBelow.DISPLAYNAME + " | Localization";
		}
	}
}
