package com.quartzshard.aasb.init.object;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.item.*;
import com.quartzshard.aasb.common.item.ItemTraits.Rarity;
import com.quartzshard.aasb.common.item.ItemTraits.Tier;
import com.quartzshard.aasb.common.item.equipment.*;
import com.quartzshard.aasb.common.item.equipment.armor.jewellery.*;
import com.quartzshard.aasb.common.item.equipment.curio.*;
import com.quartzshard.aasb.common.item.equipment.tool.*;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

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
		PROPS_GENERIC_1 = new Item.Properties().stacksTo(1),
		
		// for some godforsaken reason minecraft will give dura to shit
		// unless we split tools off onto their own properties. why????
		PROPS_GENERIC_TOOL = new Item.Properties().stacksTo(1).rarity(Rarity.QUINTESSENTIAL.get()),
		PROPS_CURIO_2 = new Item.Properties().stacksTo(1).rarity(Rarity.QUINTESSENTIAL.get()),
		
		PROPS_MATERIA_NEG2 = new Item.Properties().rarity(Rarity.MATERIA_NEG2.get()),
		PROPS_MATERIA_NEG1 = new Item.Properties().rarity(Rarity.MATERIA_NEG1.get()),
		PROPS_MATERIA_0 = new Item.Properties().rarity(Rarity.MATERIA_0.get()),
		PROPS_MATERIA_1 = new Item.Properties().rarity(Rarity.MATERIA_1.get()),
		PROPS_MATERIA_2 = new Item.Properties().rarity(Rarity.MATERIA_2.get()),
		PROPS_MATERIA_3 = new Item.Properties().rarity(Rarity.MATERIA_3.get()),
		PROPS_MATERIA_4 = new Item.Properties().rarity(Rarity.MATERIA_4.get()),
		PROPS_MATERIA_5 = new Item.Properties().rarity(Rarity.MATERIA_5.get()),
		PROPS_MATERIA_6 = new Item.Properties().rarity(Rarity.MATERIA_6.get()),
		PROPS_QUINTESSENCE = new Item.Properties().rarity(Rarity.QUINTESSENTIAL.get()),
		PROPS_AETHER = new Item.Properties().rarity(Rarity.NULLIFIED.get()),
		PROPS_IMPOSSIBLE_64 = new Item.Properties().rarity(Rarity.IMPOSSIBLE.get()),
		PROPS_IMPOSSIBLE_1 = new Item.Properties().rarity(Rarity.IMPOSSIBLE.get()),
		PROPS_ELIXIR = new Item.Properties().rarity(Rarity.IMPOSSIBLE.get()).stacksTo(1).food(new FoodProperties.Builder().alwaysEat().build())
		;
	
	/** these are used for the creative tabs, and are emptied once the tabs are filled out */
	public static @NotNull List<RegistryObject<? extends Item>>
		ALL_NATURAL_ITEMS = new ArrayList<>(),
		ALL_SYNTHETIC_ITEMS = new ArrayList<>();
	
	private enum Tab {
		NAT, SYN, BOTH, NONE;
	}
	
	// Normal items
	@SuppressWarnings("null") // null RegistryObject should never show up here under any circumstances
	public static final RegistryObject<Item>
		// Junks
		ASH = basic("ash", Tab.BOTH),
		SOOT = nulled("soot"),
		SALT = nulled("salt"),
		SPUT = nulled("sput"),
		AETHER = nulled("aether"),
		
		// Materia
		MATERIA_NEG2 = materia("exalted_redstone", -2),
		MATERIA_NEG1 = materia("homogenized_glowstone", -1),
		MATERIA_1 = materia("materia_infirma", 1),
		MATERIA_2 = materia("materia_minor", 2),
		MATERIA_3 = materia("materia_modica", 3),
		MATERIA_4 = materia("materia_major", 4),
		MATERIA_5 = materia("materia_prima", 5),
		QUINTESSENCE = reg("quintessential_condensate", PROPS_QUINTESSENCE, Tab.SYN),
		
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
		ELIXIR_OF_LIFE = reg("elixir_of_life", () -> new ElixirOfLifeItem(PROPS_ELIXIR), Tab.SYN),
		MINIUM_STONE = reg("minium_stone", () -> new MiniumStoneItem(PROPS_IMPOSSIBLE_1), Tab.NAT),
		
		// Tools
		THE_PHILOSOPHERS_STONE = reg("the_philosophers_stone", () -> new OmnitoolItem(Float.MAX_VALUE, Float.MAX_VALUE, Tier.HERMETIC, BlockTags.MINEABLE_WITH_PICKAXE, PROPS_GENERIC_TOOL), Tab.NONE),
		SWORD = reg("hermetic_blade", () -> new HermeticSwordItem(3, -2.2f, PROPS_GENERIC_TOOL), Tab.SYN),
		PICK = reg("hermetic_hammer", () -> new HermeticPickItem(1, -2.6f, PROPS_GENERIC_TOOL), Tab.SYN),
		SHOVEL = reg("hermetic_spade", () -> new HermeticShovelItem(2, -2.8f, PROPS_GENERIC_TOOL), Tab.SYN),
		AXE = reg("hermetic_hatchet", () -> new HermeticAxeItem(5, -2.8f, PROPS_GENERIC_TOOL), Tab.SYN),
		HOE = reg("hermetic_scythe", () -> new HermeticHoeItem(-4, 0.2f, PROPS_GENERIC_TOOL), Tab.SYN),
		
		// Trinkets
		GLOVE1 = reg("ornate_glove", () -> new GloveItem(1, PROPS_GENERIC_1), Tab.SYN),
		BRACELET1 = reg("ornate_bracelet", () -> new BraceletItem(1, PROPS_GENERIC_1), Tab.SYN),
		CHARM1 = reg("ornate_charm", () -> new CharmItem(1, PROPS_GENERIC_1), Tab.SYN),
		GLOVE2 = reg("hermeticized_glove", () -> new GloveItem(2, PROPS_CURIO_2), Tab.SYN),
		BRACELET2 = reg("hermeticized_bracelet", () -> new BraceletItem(2, PROPS_CURIO_2), Tab.SYN),
		CHARM2 = reg("hermeticized_charm", () -> new CharmItem(2, PROPS_CURIO_2), Tab.SYN),
		
		// Armor
		HELMET = unstack("hermetic_armet", Tab.SYN),
		CHESTPLATE = unstack("hermetic_cuirass", Tab.SYN),
		LEGGINGS = unstack("hermetic_greaves", Tab.SYN),
		BOOTS = unstack("hermetic_sabatons", Tab.SYN),
		CIRCLET = reg("circlet_of_the_seer", () -> new CircletItem(PROPS_IMPOSSIBLE_1), Tab.NAT),
		AMULET = reg("amulet_of_the_philosopher", () -> new AmuletItem(PROPS_IMPOSSIBLE_1), Tab.NAT),
		POCKETWATCH = reg("watch_of_the_astrologer", () -> new PocketwatchItem(PROPS_IMPOSSIBLE_1), Tab.NAT),
		ANKLET = reg("anklet_of_the_prophet", () -> new AnkletItem(PROPS_IMPOSSIBLE_1), Tab.NAT),
		
		// Crafting items
		C_CIRCLET = unstack("ancient_verdant_rock", Tab.NAT),
		C_AMULET = unstack("ancient_glowing_sphere", Tab.NAT),
		C_POCKETWATCH = unstack("ancient_intricate_mechanism", Tab.NAT),
		C_ANKLET = unstack("ancient_weightless_loop", Tab.NAT),
		C_AMALGAM = basic("metallic_amalgam", Tab.SYN),
		
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
		TEST_BLOCK_ITEM = fromBlock(BlockInit.CRUMBLING_STONE, Tab.SYN);
	
	private static final RegistryObject<Item> basic(String name, Tab tab) {
		return reg(name, PROPS_GENERIC_64, tab);
	}
	private static final RegistryObject<Item> smallstack(String name, Tab tab) {
		return reg(name, PROPS_GENERIC_16, tab);
	}
	private static final RegistryObject<Item> unstack(String name, Tab tab) {
		return reg(name, PROPS_GENERIC_1, tab);
	}
	public static <B extends Block> RegistryObject<BlockItem> fromBlock(RegistryObject<B> block, @NotNull Tab tab) {
		return reg(block.getId().getPath(), () -> new BlockItem(block.get(), PROPS_GENERIC_64), tab);
	}
	
	private static RegistryObject<Item> materia(String name, int tier) {
		Item.@NotNull Properties props = PROPS_GENERIC_64;
		switch (tier) {
			case -2:
				props = PROPS_MATERIA_NEG2;
				break;
			case -1:
				props = PROPS_MATERIA_NEG1;
				break;
			case 0:
				props = PROPS_MATERIA_0;
				break;
			case 1:
				props = PROPS_MATERIA_1;
				break;
			case 2:
				props = PROPS_MATERIA_2;
				break;
			case 3:
				props = PROPS_MATERIA_3;
				break;
			case 4:
				props = PROPS_MATERIA_4;
				break;
			case 5:
				props = PROPS_MATERIA_5;
				break;
			case 6:
				props = PROPS_MATERIA_6;
				break;
				
			default:
				break;
		}
		return reg(name, props, tier < 1 ? Tab.SYN : Tab.NAT);
	}
	private static RegistryObject<Item> impossible(String name, boolean stacks) {
		Item.@NotNull Properties props = stacks ? PROPS_IMPOSSIBLE_64 : PROPS_IMPOSSIBLE_1;
		return reg(name, props, Tab.NAT);
	}
	private static RegistryObject<Item> nulled(String name) {
		Item.Properties props = PROPS_AETHER;
		return reg(name, props, Tab.SYN);
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
