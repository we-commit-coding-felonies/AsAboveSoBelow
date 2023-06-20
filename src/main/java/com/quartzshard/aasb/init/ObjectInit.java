package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.block.AirIceBlock;
import com.quartzshard.aasb.common.item.LootBallItem;
import com.quartzshard.aasb.common.item.equipment.BandOfArcana;
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

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * anything tangible, such as blocks items or entities
 * @author solunareclipse1
 */
public class ObjectInit {


	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		Items.REG.register(bus);
		Blocks.REG.register(bus);
	}
	
	public class Items {
		public static final DeferredRegister<Item> REG = DeferredRegister.create(ForgeRegistries.ITEMS, AsAboveSoBelow.MODID);
		//Common item properties
		public static final Item.Properties PROPS_GENERIC = new Item.Properties().tab(ModInit.ITEM_GROUP);
		public static final Item.Properties PROPS_EXPENSIVE = new Item.Properties().tab(ModInit.ITEM_GROUP).fireResistant();
		public static final Item.Properties PROPS_GENERIC_UNSTACKABLE = new Item.Properties().tab(ModInit.ITEM_GROUP).stacksTo(1);
		public static final Item.Properties PROPS_HERM_GEAR = new Item.Properties().tab(ModInit.ITEM_GROUP).fireResistant().stacksTo(1);//.rarity(MGTKRarity.CRIMSON.get());

		// Items
		public static final RegistryObject<Item>
			PHILOSOPHERS_STONE = REG.register("philosophers_stone", () -> new Item(PROPS_GENERIC)),
			MINIUM_STONE = REG.register("minium_stone", () -> new Item(PROPS_GENERIC)),
			ELIXIR_OF_LIFE = REG.register("elixir_of_life", () -> new Item(PROPS_GENERIC)),
			QUINTESSENCE = REG.register("quintessence", () -> new Item(PROPS_GENERIC)),
			LOOT_BALL = REG.register("loot_ball", () -> new LootBallItem(PROPS_GENERIC_UNSTACKABLE)),
			
			HERMETIC_HELMET = REG.register("hermetic_armet", () -> new HermeticArmorItem(HermeticArmorMaterial.MAT, EquipmentSlot.HEAD, PROPS_HERM_GEAR, 0.2f)),
			HERMETIC_CHESTPLATE = REG.register("hermetic_cuirass", () -> new HermeticArmorItem(HermeticArmorMaterial.MAT, EquipmentSlot.CHEST, PROPS_HERM_GEAR, 0.4f)),
			HERMETIC_LEGGINGS = REG.register("hermetic_greaves", () -> new HermeticArmorItem(HermeticArmorMaterial.MAT, EquipmentSlot.LEGS, PROPS_HERM_GEAR, 0.3f)),
			HERMETIC_BOOTS = REG.register("hermetic_sabatons", () -> new HermeticArmorItem(HermeticArmorMaterial.MAT, EquipmentSlot.FEET, PROPS_HERM_GEAR, 0.1f)),
			HERMETIC_SWORD = REG.register("hermetic_blade", () -> new HermeticSwordItem(AASBToolTier.HERMETIC, 3, -2.2f, PROPS_HERM_GEAR)),
			HERMETIC_PICKAXE = REG.register("hermetic_hammer", () -> new HermeticPickaxeItem(AASBToolTier.HERMETIC, 1, -2.6f, PROPS_HERM_GEAR)),
			HERMETIC_SHOVEL = REG.register("hermetic_spade", () -> new HermeticShovelItem(AASBToolTier.HERMETIC, 2, -2.8f, PROPS_HERM_GEAR)),
			HERMETIC_AXE = REG.register("hermetic_hatchet", () -> new HermeticAxeItem(AASBToolTier.HERMETIC, 5, -2.8f, PROPS_HERM_GEAR)),
			HERMETIC_HOE = REG.register("hermetic_scythe", () -> new HermeticHoeItem(AASBToolTier.HERMETIC, -4, 0.2f, PROPS_HERM_GEAR)),
			
			OMNITOOL = REG.register("internal_omnitool", () -> new InternalOmnitool(9999, 9999, AASBToolTier.HERMETIC, BlockTags.MINEABLE_WITH_PICKAXE, PROPS_GENERIC_UNSTACKABLE)),
			
			CIRCLET = REG.register("seer_circlet", () -> new CircletItem(PROPS_GENERIC_UNSTACKABLE)),
			AMULET = REG.register("philosopher_amulet", () -> new AmuletItem(PROPS_GENERIC_UNSTACKABLE)),
			POCKETWATCH = REG.register("astrologer_pocketwatch", () -> new PocketwatchItem(PROPS_GENERIC_UNSTACKABLE)),
			ANKLET = REG.register("prophet_anklet", () -> new AnkletItem(PROPS_GENERIC_UNSTACKABLE)),
			BAND_OF_ARCANA = REG.register("band_of_arcana", () -> new BandOfArcana(PROPS_GENERIC_UNSTACKABLE));

		// BlockItems
		public static final RegistryObject<Item> WAYSTONE_BLOCKITEM = fromBlock(Blocks.WAYSTONE);
		
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
		public static final RegistryObject<Block> WAYSTONE = REG.register("waystone", () -> new Block(BLOCK_PROPERTIES));
		
		
		public static final RegistryObject<AirIceBlock> AIR_ICE = REG.register("air_ice", () -> new AirIceBlock(PROPS_TEMPBLOCK.friction(0.9f).randomTicks().sound(SoundType.GLASS).noOcclusion()));
		
	}

}
