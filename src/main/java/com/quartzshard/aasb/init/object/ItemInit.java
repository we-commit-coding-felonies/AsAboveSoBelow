package com.quartzshard.aasb.init.object;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.item.*;
import com.quartzshard.aasb.common.item.equipment.*;
import com.quartzshard.aasb.common.item.equipment.armor.jewellery.*;
import com.quartzshard.aasb.common.item.equipment.curio.*;
import com.quartzshard.aasb.common.item.equipment.tool.*;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// Deals with setting up items, how exciting
public class ItemInit {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AASB.MODID);
	public static void init(IEventBus bus) {
		ITEMS.register(bus);
	}
	
	// Shared properties
	private static final Item.Properties
		PROPS_GENERIC_64 = new Item.Properties(),
		PROPS_GENERIC_16 = new Item.Properties().stacksTo(16),
		PROPS_GENERIC_1 = new Item.Properties().stacksTo(1), // for some godforsaken reason minecraft will give dura to shit using GENERIC_1
		PROPS_GENERIC_TOOL = new Item.Properties().stacksTo(1) // unless we split tools off onto their own properties. why??????????????????
		;
	
	/** these are used for the creative tabs, and are emptied once the tabs are filled out */
	public static List<RegistryObject<? extends Item>>
		ALL_NATURAL_ITEMS = new ArrayList<>(),
		ALL_SYNTHETIC_ITEMS = new ArrayList<>();
	
	private enum Tab {
		NAT, SYN, BOTH, NONE;
	}
	
	// Normal items
	public static final RegistryObject<Item>
		// Junks
		ASH = basic("ash", Tab.BOTH),
		SOOT = basic("soot", Tab.SYN),
		SALT = basic("salt", Tab.SYN),
		SPUT = basic("sput", Tab.SYN),
		AETHER = basic("aether", Tab.SYN),
		
		// Materia
		MATERIA_1 = basic("materia_infirma", Tab.NAT),
		MATERIA_2 = basic("materia_minor", Tab.NAT),
		MATERIA_3 = basic("materia_modica", Tab.NAT),
		MATERIA_4 = basic("materia_major", Tab.NAT),
		MATERIA_5 = basic("materia_prima", Tab.NAT),
		QUINTESSENCE = basic("quintessential_condensate", Tab.SYN),
		
		// Metals
		LEAD_INGOT = basic("lead_ingot", Tab.NAT),
		TIN_INGOT = basic("tin_ingot", Tab.NAT),
		SILVER_INGOT = basic("silver_ingot", Tab.NAT),
		MERCURY_BOTTLE = basic("flask_of_hydrargyrum", Tab.NAT),
		BRONZE_INGOT = basic("bronze_ingot", Tab.SYN),
		BRASS_INGOT = basic("brass_ingot", Tab.SYN),
		
		// Flasks
		FLASK_EMPTY = smallstack("flask", Tab.SYN),
		FLASK_LIQUID = unstack("flask_of_liquid", Tab.NONE),
		FLASK_LEAD_EMPTY = smallstack("lead_flask", Tab.SYN),
		FLASK_LEAD_LIQUID = unstack("lead_flask_of_liquid", Tab.NONE),
		FLASK_LEAD_ASPECT = unstack("lead_flask_of_aspect", Tab.NONE),
		FLASK_GOLD_EMPTY = smallstack("gold_flask", Tab.SYN),
		FLASK_GOLD_LIQUID = unstack("gold_flask_of_liquid", Tab.NONE),
		FLASK_GOLD_ASPECT = unstack("gold_flask_of_aspect", Tab.NONE),
		FLASK_AETHER_EMPTY = smallstack("aetherglass_flask", Tab.SYN),
		FLASK_AETHER_LIQUID = unstack("aetherglass_flask_of_liquid", Tab.NONE),
		FLASK_AETHER_ASPECT = unstack("aetherglass_flask_of_aspect", Tab.NONE),
		
		// Philos
		ELIXIR_OF_LIFE = unstack("elixir_of_life", Tab.SYN),
		MINIUM_STONE = reg("minium_stone", () -> new MiniumStoneItem(PROPS_GENERIC_1), Tab.NAT),
		PHILOSOPHERS_STONE = unstack("philosophers_stone", Tab.NAT),
		
		// Tools
		OMNITOOL = reg("internal_omnitool", () -> new OmnitoolItem(Float.MAX_VALUE, Float.MAX_VALUE, Tier.HERMETIC, BlockTags.MINEABLE_WITH_PICKAXE, PROPS_GENERIC_TOOL), Tab.NONE),
		SWORD = reg("hermetic_blade", () -> new HermeticSwordItem(3, -2.2f, PROPS_GENERIC_TOOL), Tab.SYN),
		PICK = reg("hermetic_hammer", () -> new HermeticPickItem(1, -2.6f, PROPS_GENERIC_TOOL), Tab.SYN),
		SHOVEL = reg("hermetic_spade", () -> new HermeticShovelItem(2, -2.8f, PROPS_GENERIC_TOOL), Tab.SYN),
		AXE = reg("hermetic_hatchet", () -> new HermeticAxeItem(5, -2.8f, PROPS_GENERIC_TOOL), Tab.SYN),
		HOE = reg("hermetic_scythe", () -> new HermeticHoeItem(-4, 0.2f, PROPS_GENERIC_TOOL), Tab.SYN),
		
		// Trinkets
		GLOVE1 = reg("ornate_glove", () -> new GloveItem(1, PROPS_GENERIC_1), Tab.SYN),
		BRACELET1 = reg("ornate_bracelet", () -> new BraceletItem(1, PROPS_GENERIC_1), Tab.SYN),
		CHARM1 = reg("ornate_charm", () -> new CharmItem(1, PROPS_GENERIC_1), Tab.SYN),
		GLOVE2 = reg("hermeticized_glove", () -> new GloveItem(2, PROPS_GENERIC_1), Tab.SYN),
		BRACELET2 = reg("hermeticized_bracelet", () -> new BraceletItem(2, PROPS_GENERIC_1), Tab.SYN),
		CHARM2 = reg("hermeticized_charm", () -> new CharmItem(2, PROPS_GENERIC_1), Tab.SYN),
		
		// Armor
		HELMET = unstack("hermetic_armet", Tab.SYN),
		CHESTPLATE = unstack("hermetic_cuirass", Tab.SYN),
		LEGGINGS = unstack("hermetic_greaves", Tab.SYN),
		BOOTS = unstack("hermetic_sabatons", Tab.SYN),
		CIRCLET = reg("circlet_of_the_seer", () -> new CircletItem(PROPS_GENERIC_TOOL), Tab.BOTH),
		AMULET = reg("amulet_of_the_philosopher", () -> new AmuletItem(PROPS_GENERIC_TOOL), Tab.BOTH),
		POCKETWATCH = reg("watch_of_the_astrologer", () -> new PocketwatchItem(PROPS_GENERIC_TOOL), Tab.BOTH),
		ANKLET = reg("anklet_of_the_prophet", () -> new AnkletItem(PROPS_GENERIC_TOOL), Tab.BOTH),
		
		// Crafting items
		C_CIRCLET = unstack("ancient_verdant_rock", Tab.NAT),
		C_AMULET = unstack("ancient_glowing_sphere", Tab.NAT),
		C_POCKETWATCH = unstack("ancient_intricate_mechanism", Tab.NAT),
		C_ANKLET = unstack("ancient_weightless_loop", Tab.NAT),
		C_AMALGAM = basic("amalgam", Tab.SYN),
		
		// Misc
		LOOTBALL = reg("complex_mass", () -> new LootBallItem(PROPS_GENERIC_1), Tab.NONE),
		WAYSTONE = reg("waystone", () -> new WaystoneItem(PROPS_GENERIC_1), Tab.SYN),
		WAY_GRENADE = reg("cracked_waystone", () -> new WayGrenadeItem(PROPS_GENERIC_1), Tab.SYN),
		CHALK = unstack("chalk", Tab.NAT),
		AETHERCHALK = unstack("aetherchalk", Tab.NAT),
		
		// Cosmetics
		COS_GAUNTLET1 = unstack("silver_gauntlet", Tab.NAT),
		COS_GAUNTLET2 = unstack("hermetic_gauntlet", Tab.SYN),
		COS_BAND1 = unstack("silver_band", Tab.NAT),
		COS_BAND2 = unstack("hermetic_band", Tab.SYN),
		COS_TRINKET1 = unstack("silver_trinket", Tab.NAT),
		COS_TRINKET2 = unstack("hermetic_trinket", Tab.SYN)
		
		;

	// BlockItems
	public static final RegistryObject<BlockItem>
		TEST_BLOCK_ITEM = fromBlock(BlockInit.TEST_BLOCK, Tab.SYN);
	
	private static final RegistryObject<Item> basic(String name, Tab tab) {
		return reg(name, PROPS_GENERIC_64, tab);
	}
	private static final RegistryObject<Item> smallstack(String name, Tab tab) {
		return reg(name, PROPS_GENERIC_16, tab);
	}
	private static final RegistryObject<Item> unstack(String name, Tab tab) {
		return reg(name, PROPS_GENERIC_1, tab);
	}
	public static <B extends Block> RegistryObject<BlockItem> fromBlock(RegistryObject<B> block, Tab tab) {
		return reg(block.getId().getPath(), () -> new BlockItem(block.get(), PROPS_GENERIC_64), tab);
	}
	
	private static final RegistryObject<Item> reg(String name, Item.Properties props, Tab tab) {
		return reg(name, () -> new Item(props), tab);
	}
	private static final <I extends Item> RegistryObject<I> reg(String name, Supplier<I> sup, Tab tab) {
		RegistryObject<I> ro = ITEMS.register(name, sup);
		switch (tab) {
		case BOTH:
			ALL_SYNTHETIC_ITEMS.add(ro);
		case NAT:
			ALL_NATURAL_ITEMS.add(ro);
			break;
		case SYN:
			ALL_SYNTHETIC_ITEMS.add(ro);
		case NONE:
		default:
			break;
		}
		return ro;
	}
}
