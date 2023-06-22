package com.quartzshard.aasb.init;

import java.util.function.Supplier;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.alchemy.AspectForm;
import com.quartzshard.aasb.common.block.AirIceBlock;
import com.quartzshard.aasb.common.item.AASBRarity;
import com.quartzshard.aasb.common.item.LootBallItem;
import com.quartzshard.aasb.common.item.equipment.armor.HermeticArmorItem;
import com.quartzshard.aasb.common.item.equipment.armor.HermeticArmorItem.HermeticArmorMaterial;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.AmuletItem;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.AnkletItem;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.CircletItem;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.PocketwatchItem;
import com.quartzshard.aasb.common.item.equipment.tool.AASBToolTier;
import com.quartzshard.aasb.common.item.equipment.tool.herm.HermeticAxeItem;
import com.quartzshard.aasb.common.item.equipment.tool.herm.HermeticHoeItem;
import com.quartzshard.aasb.common.item.equipment.tool.herm.HermeticPickaxeItem;
import com.quartzshard.aasb.common.item.equipment.tool.herm.HermeticShovelItem;
import com.quartzshard.aasb.common.item.equipment.tool.herm.HermeticSwordItem;
import com.quartzshard.aasb.common.item.equipment.tool.herm.InternalOmnitool;
import com.quartzshard.aasb.common.item.equipment.trinket.AbilityTrinket;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.shape.FireRune;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.shape.WaterRune;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

/**
 * anything tangible, such as blocks items or entities
 * @author solunareclipse1
 */
public class ObjectInit {


	public static void init(IEventBus bus) {
		TrinketRunes.REG.register(bus);
		Blocks.REG.register(bus);
		Items.REG.register(bus);
		
	}
	
	public class Items {
		public static final DeferredRegister<Item> REG = DeferredRegister.create(ForgeRegistries.ITEMS, AsAboveSoBelow.MODID);
		//Common item properties
		public static final Item.Properties PROPS_GENERIC = new Item.Properties().tab(ModInit.ITEM_GROUP);
		public static final Item.Properties PROPS_UNSTACKABLE = new Item.Properties().tab(ModInit.ITEM_GROUP).stacksTo(1);
		public static final Item.Properties PROPS_GENERIC_T1 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER1.get());
		public static final Item.Properties PROPS_GENERIC_T2 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER2.get());
		public static final Item.Properties PROPS_GENERIC_T3 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER3.get());
		public static final Item.Properties PROPS_GENERIC_T4 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER4.get());
		public static final Item.Properties PROPS_GENERIC_T5 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER5.get());
		public static final Item.Properties PROPS_MINIUM = makeProps(AASBRarity.SPECIAL.get(), 1, false);
		public static final Item.Properties PROPS_HERM_GEAR = makeProps(AASBRarity.TIER4.get(), 1, true);
		public static final Item.Properties PROPS_JEWELRY = makeProps(AASBRarity.TIER5.get(), 1, true);

		// Items

		public static final RegistryObject<Item>
				// Stackable items / materials
				ASH = REG.register("ash", () -> new Item(PROPS_GENERIC)),
				SOOT = REG.register("soot", () -> new Item(PROPS_GENERIC)),
				SALT = REG.register("salt", () -> new Item(PROPS_GENERIC)),
				SUBLIT = REG.register("sublit", () -> new Item(PROPS_GENERIC)),
				AETHER = REG.register("aether", () -> new Item(makeProps(AASBRarity.SPECIAL.get(), 64, false))),
				MATERIA_1 = REG.register("materia_infirma", () -> new Item(PROPS_GENERIC_T1)),
				MATERIA_2 = REG.register("materia_minor", () -> new Item(PROPS_GENERIC_T2)),
				MATERIA_3 = REG.register("materia_modica", () -> new Item(PROPS_GENERIC_T3)),
				MATERIA_4 = REG.register("materia_major", () -> new Item(PROPS_GENERIC_T4)),
				MATERIA_5 = REG.register("materia_prima", () -> new Item(PROPS_GENERIC_T5)),
				QUINTESSENCE = REG.register("quintessential_condensate", () -> new Item(PROPS_GENERIC_T4)),
			
				// Unstackable items
				PHILOSOPHERS_STONE = REG.register("philosophers_stone", () -> new Item(makeProps(AASBRarity.SPECIAL.get(), 1, true))),
				MINIUM_STONE = REG.register("minium_stone", () -> new Item(PROPS_MINIUM)),
				ELIXIR_OF_LIFE = REG.register("elixir_of_life", () -> new Item(PROPS_MINIUM)),
				LOOT_BALL = REG.register("complex_mass", () -> new LootBallItem(PROPS_UNSTACKABLE)),
			
				// Hermetic Stuff
				HERMETIC_HELMET = REG.register("hermetic_armet", () -> new HermeticArmorItem(EquipmentSlot.HEAD, PROPS_HERM_GEAR, 0.2f)),
				HERMETIC_CHESTPLATE = REG.register("hermetic_cuirass", () -> new HermeticArmorItem(EquipmentSlot.CHEST, PROPS_HERM_GEAR, 0.4f)),
				HERMETIC_LEGGINGS = REG.register("hermetic_greaves", () -> new HermeticArmorItem(EquipmentSlot.LEGS, PROPS_HERM_GEAR, 0.3f)),
				HERMETIC_BOOTS = REG.register("hermetic_sabatons", () -> new HermeticArmorItem(EquipmentSlot.FEET, PROPS_HERM_GEAR, 0.1f)),
				HERMETIC_SWORD = REG.register("hermetic_blade", () -> new HermeticSwordItem(3, -2.2f, PROPS_HERM_GEAR)),
				HERMETIC_PICKAXE = REG.register("hermetic_hammer", () -> new HermeticPickaxeItem(1, -2.6f, PROPS_HERM_GEAR)),
				HERMETIC_SHOVEL = REG.register("hermetic_spade", () -> new HermeticShovelItem(2, -2.8f, PROPS_HERM_GEAR)),
				HERMETIC_AXE = REG.register("hermetic_hatchet", () -> new HermeticAxeItem(5, -2.8f, PROPS_HERM_GEAR)),
				HERMETIC_HOE = REG.register("hermetic_scythe", () -> new HermeticHoeItem(-4, 0.2f, PROPS_HERM_GEAR)),
			
				// Misc stuff
				OMNITOOL = REG.register("internal_omnitool", () -> new InternalOmnitool(9999, 9999, AASBToolTier.HERMETIC, BlockTags.MINEABLE_WITH_PICKAXE, PROPS_UNSTACKABLE)),
			
				CIRCLET = REG.register("seer_circlet", () -> new CircletItem(PROPS_JEWELRY)),
				AMULET = REG.register("philosopher_amulet", () -> new AmuletItem(PROPS_JEWELRY)),
				POCKETWATCH = REG.register("astrologer_pocketwatch", () -> new PocketwatchItem(PROPS_JEWELRY)),
				ANKLET = REG.register("prophet_anklet", () -> new AnkletItem(PROPS_JEWELRY)),

				// BlockItems
				ASH_STONE_BLOCKITEM = fromBlock(Blocks.ASH_STONE),
				WAYSTONE_BLOCKITEM = fromBlock(Blocks.WAYSTONE);
		
		
		// constructs a new Item Properties using an existing base
		private static Item.Properties makeProps(Rarity rarity, int maxStack, boolean fireRes) {
			Item.Properties props = new Item.Properties()
					.tab(ModInit.ITEM_GROUP)
					.rarity(rarity)
					.stacksTo(maxStack);
			if (fireRes) {
				props.fireResistant();
			}
			return props;
		}
		
		// Conveniance function: Take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
		public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
			return REG.register(block.getId().getPath(), () -> new BlockItem(block.get(), PROPS_GENERIC));
		}
	}
	
	public class Blocks {
		private static final DeferredRegister<Block> REG = DeferredRegister.create(ForgeRegistries.BLOCKS, AsAboveSoBelow.MODID);
		public static final BlockBehaviour.Properties BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
		public static final BlockBehaviour.Properties PROPS_TEMPBLOCK = BlockBehaviour.Properties.of(Material.ICE).noDrops().instabreak();
		
		// Simple
		public static final RegistryObject<Block> ASH_STONE = REG.register("ashen_stone", () -> new Block(BLOCK_PROPERTIES));
		public static final RegistryObject<Block> WAYSTONE = REG.register("waystone", () -> new Block(BLOCK_PROPERTIES));
		
		
		public static final RegistryObject<AirIceBlock> AIR_ICE = REG.register("air_ice", () -> new AirIceBlock(PROPS_TEMPBLOCK.friction(0.9f).randomTicks().sound(SoundType.GLASS).noOcclusion()));
		
	}
	public class TrinketRunes {
	    private static final DeferredRegister<TrinketRune> REG = DeferredRegister.create(AsAboveSoBelow.rl("trinket_runes"), AsAboveSoBelow.MODID);
	    public static final Supplier<IForgeRegistry<TrinketRune>> REGISTRY_SUPPLIER = REG.makeRegistry(TrinketRune.class, () -> {
	            	RegistryBuilder<TrinketRune> builder = new RegistryBuilder<>();
	            	return builder;
	            }
	    );
		public static final RegistryObject<TrinketRune>
				WATER = REG.register("water", () -> new WaterRune()),
				FIRE = REG.register("fire", () -> new FireRune());
	}
	
	

}
