package com.quartzshard.aasb.data;

import java.util.List;
import java.util.function.Supplier;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.AlchData;
import com.quartzshard.aasb.api.alchemy.aspect.IAspect;
import com.quartzshard.aasb.client.Keybinds;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.util.ClientUtil;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

public class LangData extends LanguageProvider {
	public LangData(@NotNull PackOutput out) {
		super(out, AASB.MODID, "en_us");
	}
	
	private static String id(String template) {
		return String.format(template, AASB.MODID);
	}
	private static String f(String formName) {
		return id("alchemy.%s.aspect.form."+formName);
	}
	private static String l(String processName) {
		return id("alchemy.%s.labProcess."+processName);
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
	private static String edm(String template) {
		return dm(template, false, true);
	}
	private static String dm(String template, boolean suffix, boolean environmental) {
		String id = id("death.attack.%s."+template);
		if (suffix) {
			id = id + (environmental ? ".player" : ".item");
		}
		return id;
	}
	private static String sub(Supplier<SoundEvent> ro) {
		return SoundData.subFor(ro);
	}
	
	public static final Component NL = Component.literal(" ");
	public static MutableComponent tc(String key, Object... args) {
		return Component.translatable(key, args);
	}
	
	public static final String
		CTAB_NATURAL = id("itemGroup.%s.natural"),
		CTAB_SYNTHETIC = id("itemGroup.%s.synthetic"),
		
		ASPECT_WAY = id("alchemy.%s.aspect.way"),
		
		ASPECT_SHAPE = id("alchemy.%s.aspect.shape"),
		SHAPE_WATER = id("alchemy.%s.aspect.shape.water"),
		SHAPE_EARTH = id("alchemy.%s.aspect.shape.earth"),
		SHAPE_FIRE = id("alchemy.%s.aspect.shape.fire"),
		SHAPE_AIR = id("alchemy.%s.aspect.shape.air"),
		SHAPE_QUINTESSENCE = id("alchemy.%s.aspect.shape.quintessence"),

		ASPECT_FORM = id("alchemy.%s.aspect.form"),
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

		LAB_EVAPORATION = l("evaporation"),
		LAB_FIXATION = l("fixation"),
		LAB_AMALGAMATION = l("amalgamation"),
		LAB_HOMOGENIZATION = l("homogenization"),
		
		LAB_DESICCATION = l("desiccation"),
		LAB_OXIDATION = l("oxidation"),
		LAB_CONGELATION = l("amalgamation"),
		LAB_CERATION = l("homogenization"),
		LAB_DEHYDRATION = l("dehydration"),
		LAB_EXALTATION = l("exaltation"),
		LAB_CONDEMNATION = l("condemnation"),
		
		LAB_SUBLIMATION = l("sublimation"),
		LAB_CONJUNCTION = l("conjunction"),
		LAB_STAGNATION = l("stagnation"),
		LAB_SEPARATION = l("separation"),
		LAB_FILTRATION = l("filtration"),

		LAB_PROJECTION = l("projection"),
		LAB_CONDENSATION = l("condensation"),
		LAB_DISTILLATION = l("distillation"),
		LAB_COHOBATION = l("cohobation"),
		LAB_SOLUTION = l("solution"),
		LAB_DIGESTION = l("digestion"),
		LAB_MULTIPLICATION = l("multiplication"),

		KEYCAT_INGAME = "key.categories.%s.inGame",
		KEYCAT_GUI = "key.categories.%s.gui",
		
		KEY_HEAD = id("key.%s.headMode"),
		KEY_CHEST = id("key.%s.chestMode"),
		KEY_LEGS = id("key.%s.legsMode"),
		KEY_FEET = id("key.%s.feetMode"),
		KEY_EMPOWER = id("key.%s.empower"),
		KEY_MODE = id("key.%s.itemMode"),
		KEY_FUNC_1 = id("key.%s.itemFunc.1"),
		KEY_FUNC_2 = id("key.%s.itemFunc.2"),
		KEY_HANDSWAP = id("key.%s.trinket.swapHand"),
		KEY_GLOVE = id("key.%s.trinket.triggerGlove"),
		KEY_BRACELET = id("key.%s.trinket.triggerBracelet"),
		KEY_CHARM = id("key.%s.trinket.triggerCharm"),
		KEY_SEE_ASPECTS = id("key.%s.client.seeAspects"),
		
		ITEM_GLOVE_RUNED = id("item.%s.runed.glove"),
		ITEM_BRACELET_RUNED = id("item.%s.runed.bracelet"),
		ITEM_CHARM_RUNED = id("item.%s.runed.charm"),
		ITEM_RUNED_1 = id("item.%s.runed.1"),
		ITEM_RUNED_2 = id("item.%s.runed.2"),
		
		TIP_GENERIC_ON = id("toolTip.%s.generic.on"),
		TIP_GENERIC_OFF = id("toolTip.%s.generic.off"),
		TIP_GENERIC_MODE = id("toolTip.%s.generic.mode"),
		TIP_GENERIC_MOREINFO = id("toolTip.%s.generic.moreInfo"),

		TIP_FLASK_ASPECTS = id("toolTip.%s.flask.aspects"),
		TIP_FLASK_ASPECTS_ONE = id("toolTip.%s.flask.aspects.one"),
		TIP_FLASK_EXPIRY = id("toolTip.%s.flask.expiry"),
		TIP_FLASK_BAD = id("toolTip.%s.flask.bad"),
		
		TIP_RUNE = id("toolTip.%s.rune"),
		TIP_RUNE_MULTI = id("toolTip.%s.rune.multi"),
		
		TIP_ARMOR_FLAVOR = id("toolTip.%s.armor.flavor"),
		TIP_ARMOR_DESC_1 = id("toolTip.%s.armor.desc.1"),
		TIP_ARMOR_DESC_2 = id("toolTip.%s.armor.desc.2"),
		TIP_ARMOR_DESC_3 = id("toolTip.%s.armor.desc.3"),
		TIP_ARMOR_ABSORBMAX = id("toolTip.%s.armor.absorbMax"),
		TIP_ARMOR_SATURATION = id("toolTip.%s.armor.saturation"),
		
		TIP_SWORD_FLAVOR = id("toolTip.%s.sword.flavor"),
		TIP_SWORD_DESC = id("toolTip.%s.sword.desc"),
		TIP_SWORD_MODE = id("toolTip.%s.sword.mode"),
		TIP_SWORD_MODE_DESC = id("toolTip.%s.sword.mode.desc"),
		TIP_SWORD_MODE_HOSTILE = id("toolTip.%s.sword.mode.hostile"),
		TIP_SWORD_MODE_HOSTILEPLAYER = id("toolTip.%s.sword.mode.hostilePlayer"),
		TIP_SWORD_MODE_NOTPLAYER = id("toolTip.%s.sword.mode.notPlayer"),
		TIP_SWORD_MODE_ALL = id("toolTip.%s.sword.mode.all"),
		
		TIP_PICK_FLAVOR = id("toolTip.%s.pick.flavor"),
		TIP_PICK_DESC = id("toolTip.%s.pick.desc"),
		
		TIP_SHOVEL_FLAVOR = id("toolTip.%s.shovel.flavor"),
		TIP_SHOVEL_DESC = id("toolTip.%s.shovel.desc"),
		
		TIP_AXE_FLAVOR = id("toolTip.%s.axe.flavor"),
		TIP_AXE_DESC = id("toolTip.%s.axe.desc"),
		
		TIP_HOE_FLAVOR = id("toolTip.%s.hoe.flavor"),
		TIP_HOE_DESC = id("toolTip.%s.hoe.desc"),
		TIP_HOE_MODE = id("toolTip.%s.hoe.mode"),
		TIP_HOE_MODE_TILL = id("toolTip.%s.hoe.mode.till"),
		TIP_HOE_MODE_TILL_LONG = id("toolTip.%s.hoe.mode.till.long"),
		TIP_HOE_MODE_PATH = id("toolTip.%s.hoe.mode.path"),
		TIP_HOE_MODE_PATH_LONG = id("toolTip.%s.hoe.mode.path.long"),
		TIP_HOE_MODE_CULL = id("toolTip.%s.hoe.mode.cull"),
		TIP_HOE_MODE_CULL_LONG = id("toolTip.%s.hoe.mode.cull.long"),
		
		TIP_TOOL_ENCHBONUS_FLAVOR = id("toolTip.%s.tool.enchBonus.flavor"),
		TIP_TOOL_ENCHBONUS_DESC = id("toolTip.%s.enchBonus.desc"),
		TIP_TOOL_ENCHBONUS_DESC_NOCHARGE = id("toolTip.%s.enchBonus.desc.noCharge"),
		TIP_TOOL_ENCHBONUS_VAL = id("toolTip.%s.enchBonus.val"),

		TIP_TOOL_STATICDIG = id("toolTip.%s.tool.staticDig"),
		TIP_TOOL_STATICDIG_DESC = id("toolTip.%s.tool.staticDig.desc"),
		TIP_TOOL_STATICDIG_STATE =  id("toolTip.%s.tool.staticDig.state"),
				
		TIP_TOOL_EMPOWER_DESC = id("toolTip.%s.tool.empower.desc"),
		TIP_TOOL_EMPOWER_GUIDE = id("toolTip.%s.tool.empower.guide"),
		
		TIP_PHIL_1 = id("toolTip.%s.phil.1"),
		TIP_PHIL_2 = id("toolTip.%s.phil.2"),
		TIP_PHIL_3 = id("toolTip.%s.phil.3"),
		TIP_PHIL_4 = id("toolTip.%s.phil.4"),
		TIP_PHIL_5 = id("toolTip.%s.phil.5"),

		TIP_ASPECTS = id("toolTip.%s.aspects"),
		TIP_ASPECTS_UNMAPPED_1 = id("toolTip.%s.aspects.unmapped.1"),
		TIP_ASPECTS_UNMAPPED_2 = id("toolTip.%s.aspects.unmapped.2"),
		
		// Death Messages
		DIE_AUTOSLASH = "autoslash",
		DIE_MUSTANG = "mustang",
		DIE_SURFACE_TENSION_ENV = "surface_tension_env",
		DIE_TRANSMUTE = "transmute",
		DIE_TRANSMUTE_ENV = "transmute_env",
		DIE_WAYBOMB = "waybomb",
		DIE_WAYBOMB_ENV = "waybomb_env",
		DIE_ARROWSWARM = "arrow_swarm",
		DIE_YONDU = "sentient_arrow";
		

	@Override
	protected void addTranslations() {
		// Mod stuff
		add(CTAB_NATURAL, "Natural Alchemy");
		add(CTAB_SYNTHETIC, "Synthetic Alchemy");
		
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
		add(KEY_HEAD, "Circlet mode");
		add(KEY_CHEST, "Amulet mode");
		add(KEY_LEGS, "Timepiece mode");
		add(KEY_FEET, "Anklet mode");
		add(KEY_MODE, "Item mode");
		add(KEY_FUNC_1, "Primary ability");
		add(KEY_FUNC_2, "Secondary ability");
		add(KEY_EMPOWER, "Empower item");
		
		// Items
		add(ItemInit.ASH.get(), "Ash");
		add(ItemInit.SOOT.get(), "Soot");
		add(ItemInit.SALT.get(), "Salt");
		add(ItemInit.SPUT.get(), "Sput");
		add(ItemInit.AETHER.get(), "Aether");

		add(ItemInit.MATERIA_NEG2.get(), "Exalted Redstone");
		add(ItemInit.MATERIA_NEG1.get(), "Homogenized Glowstone");
		add(ItemInit.MATERIA_1.get(), "Materia Infirma");
		add(ItemInit.MATERIA_2.get(), "Materia Minor");
		add(ItemInit.MATERIA_3.get(), "Materia Modica");
		add(ItemInit.MATERIA_4.get(), "Materia Major");
		add(ItemInit.MATERIA_5.get(), "Materia Prima");
		add(ItemInit.QUINTESSENCE.get(), "Quintessential Condensate");

		add(ItemInit.LEAD_INGOT.get(), "Lead Ingot");
		add(ItemInit.TIN_INGOT.get(), "Tin Ingot");
		add(ItemInit.SILVER_INGOT.get(), "Silver Ingot");
		add(ItemInit.MERCURY_BOTTLE.get(), "Flask of Hydrargyrum");
		add(ItemInit.BRONZE_INGOT.get(), "Bronze Ingot");
		add(ItemInit.BRASS_INGOT.get(), "Brass Ingot");
		
		add(ItemInit.FLASK_EMPTY.get(), "Flask");
		add(ItemInit.FLASK_LIQUID.get(), "Flask of %s");
		add(ItemInit.FLASK_LEAD_EMPTY.get(), "Lead Flask");
		add(ItemInit.FLASK_LEAD_LIQUID.get(), "Lead Flask of %s");
		add(ItemInit.FLASK_LEAD_ASPECT.get(), "Lead Flask of %s");
		add(ItemInit.FLASK_GOLD_EMPTY.get(), "Golden Flask");
		add(ItemInit.FLASK_GOLD_LIQUID.get(), "Golden Flask of %s");
		add(ItemInit.FLASK_GOLD_ASPECT.get(), "Golden Flask of %s");
		add(ItemInit.FLASK_AETHER_EMPTY.get(), "Aetherglass Flask");
		add(ItemInit.FLASK_AETHER_LIQUID.get(), "Aetherglass Flask of %s");
		add(ItemInit.FLASK_AETHER_ASPECT.get(), "Aetherglass Flask of %s");
		
		add(ItemInit.ELIXIR_OF_LIFE.get(), "Elixir of Life");
		add(ItemInit.MINIUM_STONE.get(), "Minium Stone");
		
		add(ItemInit.THE_PHILOSOPHERS_STONE.get(), "The Philosopher's Stone");
		add(ItemInit.SWORD.get(), "Hermetic Blade");
		add(ItemInit.PICK.get(), "Hermetic Hammer");
		add(ItemInit.SHOVEL.get(), "Hermetic Spade");
		add(ItemInit.AXE.get(), "Hermetic Hatchet");
		add(ItemInit.HOE.get(), "Hermetic Scythe");

		add(ItemInit.GLOVE1.get(), "Ornate Glove");
		add(ItemInit.BRACELET1.get(), "Ornate Bracelet");
		add(ItemInit.CHARM1.get(), "Ornate Charm");
		add(ItemInit.GLOVE2.get(), "Hermeticized Glove");
		add(ItemInit.BRACELET2.get(), "Hermeticized Bracelet");
		add(ItemInit.CHARM2.get(), "Hermeticized Charm");
		add(ITEM_GLOVE_RUNED, "Glove of %s");
		add(ITEM_BRACELET_RUNED, "Bracelet of %s");
		add(ITEM_CHARM_RUNED, "Charm of %s");
		add(ITEM_RUNED_1, "%s");
		add(ITEM_RUNED_2, "%s & %s");
		
		add(ItemInit.HELMET.get(), "Hermetic Armet");
		add(ItemInit.CHESTPLATE.get(), "Hermetic Cuirass");
		add(ItemInit.LEGGINGS.get(), "Hermetic Greaves");
		add(ItemInit.BOOTS.get(), "Hermetic Sabatons");
		add(ItemInit.CIRCLET.get(), "Circlet of the Seer");
		add(ItemInit.AMULET.get(), "Amulet of the Philosopher");
		add(ItemInit.POCKETWATCH.get(), "Watch of the Astrologer");
		add(ItemInit.ANKLET.get(), "Anklet of the Prophet");
		
		add(ItemInit.C_CIRCLET.get(), "Shattered Verdant Rock");
		add(ItemInit.C_AMULET.get(), "Cracked Glowing Sphere");
		add(ItemInit.C_POCKETWATCH.get(), "Broken Intricate Mechanism");
		add(ItemInit.C_ANKLET.get(), "Rusted Weightless Loop");
		add(ItemInit.C_AMALGAM.get(), "Metallic Amalgam");

		add(ItemInit.LOOTBALL.get(), "Complex Mass");
		add(ItemInit.WAYSTONE.get(), "Waystone");
		add(ItemInit.WAY_GRENADE.get(), "Cracked Waystone");
		add(ItemInit.CHALK.get(), "Chalk");
		add(ItemInit.AETHERCHALK.get(), "Aetherchalk");

		add(ItemInit.COS_GAUNTLET1.get(), "Silver Gauntlet");
		add(ItemInit.COS_GAUNTLET2.get(), "Hermetic Gauntlet");
		add(ItemInit.COS_BAND1.get(), "Silver Band");
		add(ItemInit.COS_BAND2.get(), "Hermetic Band");
		add(ItemInit.COS_TRINKET1.get(), "Silver Trinket");
		add(ItemInit.COS_TRINKET2.get(), "Hermetic Trinket");
		
		// Blocks
		/*add(ObjectInit.Blocks.ASH_STONE.get(), "Ashen Stone");
		add(ObjectInit.Blocks.WAYSTONE.get(), "Waystone");
		add(ObjectInit.Blocks.AIR_ICE.get(), "Frozen Air");*/
		
		// Tooltips
		add(TIP_GENERIC_ON, "Enabled");
		add(TIP_GENERIC_OFF, "Disabled");
		add(TIP_GENERIC_MODE, "%s: %s");
		add(TIP_GENERIC_MOREINFO, "Hold %s for more information");

		add(TIP_FLASK_ASPECTS, "Solution of %s & %s");
		add(TIP_FLASK_ASPECTS_ONE, "Solution of pure %s");
		add(TIP_FLASK_EXPIRY, "Expires in %s");
		add(TIP_FLASK_BAD, "Expired!");
		
		add(TIP_RUNE, "Inscribed with %s");
		add(TIP_RUNE_MULTI, "Inscribed with %s & %s");
		
		add(TIP_ARMOR_FLAVOR, "Become a living fortress with the infalliable Ultrashield");
		add(TIP_ARMOR_DESC_1, "Provides enormous amounts of protection by soaking up damage like a sponge");
		add(TIP_ARMOR_DESC_2, "Reduced protection as saturation increases, saturation dissipates over time");
		add(TIP_ARMOR_DESC_3, "Will violently release all stored energy at once if overloaded");
		add(TIP_ARMOR_ABSORBMAX, "Can currently absorb up to %s damage in a single hit");
		add(TIP_ARMOR_SATURATION, "Saturation: %s/%s");
		
		add(TIP_SWORD_FLAVOR, "Decimate your foes with the powerful Autoslash");
		add(TIP_SWORD_DESC, "Press %s to rapidly attack nearby creatures");
		add(TIP_SWORD_MODE, "Target");
		add(TIP_SWORD_MODE_DESC, "Currently targeting %s, change with %s");
		add(TIP_SWORD_MODE_HOSTILE, "Hostile only");
		add(TIP_SWORD_MODE_HOSTILEPLAYER, "Hostile & Players");
		add(TIP_SWORD_MODE_NOTPLAYER, "All except Players");
		add(TIP_SWORD_MODE_ALL, "Everything");
		
		add(TIP_PICK_FLAVOR, "Make stripmining a breeze with the handy Proximine");
		add(TIP_PICK_DESC, "Press %s to instantly collect all nearby ores");

		add(TIP_SHOVEL_FLAVOR, "Level entire mountains with the arguably safe Areablast");
		add(TIP_SHOVEL_DESC, "Press %s to violently excavate a large area");

		add(TIP_AXE_FLAVOR, "Turn forests into plains with the controversial Supercut");
		add(TIP_AXE_DESC, "Press %s to quickly harvest all nearby tree blocks");

		add(TIP_HOE_FLAVOR, "Expedite landscaping with the useful Hypersickle");
		add(TIP_HOE_DESC, "Press %s to %s, %s to change operation");
		add(TIP_HOE_MODE, "Operation");
		add(TIP_HOE_MODE_TILL, "Irrigation");
		add(TIP_HOE_MODE_TILL_LONG, "till soil");
		add(TIP_HOE_MODE_PATH, "Pathmaking");
		add(TIP_HOE_MODE_PATH_LONG, "create paths");
		add(TIP_HOE_MODE_CULL, "Weedkiller");
		add(TIP_HOE_MODE_CULL_LONG, "destroy foliage");
		
		add(TIP_TOOL_ENCHBONUS_FLAVOR, "Outclass everything else with the help of enchanting");
		add(TIP_TOOL_ENCHBONUS_DESC, "Gains large boosts to base stats when enchanted, scaling with charge");
		add(TIP_TOOL_ENCHBONUS_DESC_NOCHARGE, "Gains large boosts to base stats when enchanted");
		add(TIP_TOOL_ENCHBONUS_VAL, "Enchantment potency: %s");

		add(TIP_TOOL_STATICDIG, "Dig Stabilizer");
		add(TIP_TOOL_STATICDIG_DESC, "Dig Stabilizer is avaliable, allowing for precision block breaking");
		add(TIP_TOOL_STATICDIG_STATE, "It is currently %s, toggle with %s");
		
		add(TIP_TOOL_EMPOWER_DESC, "Abilities require empowerment to function");
		add(TIP_TOOL_EMPOWER_GUIDE, "Hold %s to empower with Way");

		add(TIP_PHIL_1, "WARNING: THIS IS A DEBUG ITEM AND IT MAY BE UNSTABLE!!!");
		add(TIP_PHIL_2, "This item is not normally obtainable.");
		add(TIP_PHIL_3, "It is intended for testing / debugging by devs,");
		add(TIP_PHIL_4, "and may cause strange effects, crashes, or worse!");
		add(TIP_PHIL_5, "BUG REPORTS RELATED TO THIS ITEM WILL BE IGNORED, YOU HAVE BEEN WARNED");

		add(TIP_ASPECTS_UNMAPPED_1, "This item cannot be");
		add(TIP_ASPECTS_UNMAPPED_2, "described by alchemy");

		add(TIP_ASPECTS, "Aspects of %s:");
		
		// Death Messages
		add(dm(DIE_AUTOSLASH), "%s was decimated by %s");
		add(dm(DIE_AUTOSLASH, true), "%s got butchered by %s using %s");
		add(dm(DIE_MUSTANG), "%s was phlogistonated by %s");
		add(dm(DIE_MUSTANG, true), "%s was reduced to ash by %s's %s");
		add(edm(DIE_SURFACE_TENSION_ENV), "%s has discovered surface tension");
		add(edm(DIE_SURFACE_TENSION_ENV, true), "%s was proven guilty of witchcraft by %s");
		add(dm(DIE_TRANSMUTE), "%s lost their Way at the hands of %s");
		add(dm(DIE_TRANSMUTE, true), "%s fueled %s's transmutation via %s");
		add(edm(DIE_TRANSMUTE_ENV), "%s became abominable");
		add(edm(DIE_TRANSMUTE_ENV, true), "%s became unrecognizable whilst fighting %s");
		add(dm(DIE_WAYBOMB), "%s was aspectually atomized due to %s");
		add(dm(DIE_WAYBOMB, true), "%s underwent alchemical fission because of %s whilst wielding %s");
		add(edm(DIE_WAYBOMB_ENV), "%s was disintegrated by a catastrophic ventilation of Way");
		add(edm(DIE_WAYBOMB_ENV, true), "%s was totally obliterated because of %s");
		add(dm(DIE_ARROWSWARM), "%s is more arrow than flesh thanks to %s");
		add(dm(DIE_ARROWSWARM, true), "%s got turned into a pincushion by %s using %s");
		add(dm(DIE_YONDU), "%s was skewered by Sentient Arrow, with a bit of help from %s");
		add(dm(DIE_YONDU, true), "%s experienced impalement thanks to Sentient Arrow, assisted by %s and their %s");
		
		// Subtitles
		add(sub(FxInit.SND_SENTIENT_WHISPERS), "Sentient whispers");
		
		add(sub(FxInit.SND_TRANSMUTE_GENERIC), "Transmutation");
		add(sub(FxInit.SND_TRANSMUTE_WATER), "Water transmutation");
		add(sub(FxInit.SND_TRANSMUTE_EARTH), "Earth transmutation");
		add(sub(FxInit.SND_TRANSMUTE_FIRE), "Fire transmutation");
		add(sub(FxInit.SND_TRANSMUTE_AIR), "Air transmutation");
		add(sub(FxInit.SND_TRANSMUTE_ALIVE), "Living transmutation");
		add(sub(FxInit.SND_TRANSMUTE_IMPOSSIBLE), "Impossible transmutation");
		add(sub(FxInit.SND_TRANSMUTE_FAIL), "Failed transmutation");

		add(sub(FxInit.SND_WAY_WASTE), "Way dispersed");
		add(sub(FxInit.SND_WAY_LEAK), "Way leaks");
		add(sub(FxInit.SND_WAY_EXPLODE), "Way violently dispersed");
		add(sub(FxInit.SND_WAY_CHARGE), "Way empowers");
		add(sub(FxInit.SND_WAY_SLASH), "Way discharged");

		add(sub(FxInit.SND_BARRIER_AMBIENT), "Barrier hums");
		add(sub(FxInit.SND_BARRIER_PROTECT), "Barrier blocks");
		add(sub(FxInit.SND_BARRIER_FAIL), "Barrier breaks");

		add(sub(FxInit.SND_TRINKET_GLOVE), "Finger snap");
		add(sub(FxInit.SND_TRINKET_RING), "Hand wave");
		add(sub(FxInit.SND_TRINKET_CHARM), "Charm triggered");

		add(sub(FxInit.SND_TARGETLOCK), "Arrow finds target");
		add(sub(FxInit.SND_WHISTLE), "Whistling");
		add(sub(FxInit.SND_MUSTANG), "Atmosphere ignites");
		add(sub(FxInit.SND_TICK), "Ticking");
		add(sub(FxInit.SND_ELIXIR), "Drinking Elixir of Life");
		add(sub(FxInit.SND_VANISH), "Something vanishes");
	}


	/**
	 * Appends a generic "Hold shift for more info" tooltip
	 * @param stack
	 * @param level
	 * @param tips
	 * @param flags
	 */
	public static void appendMoreInfoText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(NL);
		Component shiftKeyText = Keybinds.fLoc(ClientUtil.mc().options.keyShift);
		tips.add(tc(TIP_GENERIC_MOREINFO, shiftKeyText)); // Key help
	}

	public record AspectTooltip(AlchData alchData) implements TooltipComponent {}
	public record AspectTextComponent(IAspect<?> aspect) {}
}
