package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.block.AirIceBlock;
import com.quartzshard.aasb.common.item.equipment.BandOfArcana;
import com.quartzshard.aasb.common.item.equipment.armor.DarkMatterArmor;
import com.quartzshard.aasb.common.item.equipment.armor.DarkMatterArmor.DarkMatterArmorMaterial;

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
        public static final Item.Properties ITEM_PROPERTIES_GENERIC = new Item.Properties().tab(ModInit.ITEM_GROUP);
        public static final Item.Properties ITEM_PROPERTIES_DM_GEAR = new Item.Properties().tab(ModInit.ITEM_GROUP).durability(0).fireResistant().stacksTo(1);//.rarity(MGTKRarity.CRIMSON.get());

        //Items
        public static final RegistryObject<Item>
    		MINIUM_STONE = REG.register("minium_stone", () -> new Item(ITEM_PROPERTIES_GENERIC)),
        	PHILOSOPHERS_STONE = REG.register("philosophers_stone", () -> new Item(ITEM_PROPERTIES_GENERIC)),
        
        	DARK_MATTER_HELMET = REG.register("dark_matter_helmet", () -> new DarkMatterArmor(DarkMatterArmorMaterial.MAT, EquipmentSlot.HEAD, ITEM_PROPERTIES_DM_GEAR, 0.17f)),
    		DARK_MATTER_CHESTPLATE = REG.register("dark_matter_chestplate", () -> new DarkMatterArmor(DarkMatterArmorMaterial.MAT, EquipmentSlot.CHEST, ITEM_PROPERTIES_DM_GEAR, 0.4f)),
    		DARK_MATTER_LEGGINGS = REG.register("dark_matter_leggings", () -> new DarkMatterArmor(DarkMatterArmorMaterial.MAT, EquipmentSlot.LEGS, ITEM_PROPERTIES_DM_GEAR, 0.3f)),
    		DARK_MATTER_BOOTS = REG.register("dark_matter_boots", () -> new DarkMatterArmor(DarkMatterArmorMaterial.MAT, EquipmentSlot.FEET, ITEM_PROPERTIES_DM_GEAR, 0.13f)),
    		
    		BAND_OF_ARCANA = REG.register("band_of_arcana", () -> new BandOfArcana(ITEM_PROPERTIES_GENERIC));

        // BlockItems
        public static final RegistryObject<Item> WAYSTONE_BLOCKITEM = fromBlock(Blocks.WAYSTONE);
        
        // Conveniance function: Take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
        public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
            return REG.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES_GENERIC));
        }
    }
    
    public class Blocks {
    	private static final DeferredRegister<Block> REG = DeferredRegister.create(ForgeRegistries.BLOCKS, AsAboveSoBelow.MODID);
        public static final BlockBehaviour.Properties BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
        public static final BlockBehaviour.Properties TEMP_BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.ICE).noDrops().instabreak();
        
        // Simple
        public static final RegistryObject<Block> WAYSTONE = REG.register("waystone", () -> new Block(BLOCK_PROPERTIES));
        
        
        public static final RegistryObject<AirIceBlock> AIR_ICE = REG.register("air_ice", () -> new AirIceBlock(TEMP_BLOCK_PROPERTIES.friction(0.9f).randomTicks().sound(SoundType.GLASS).noOcclusion()));
        
    }

}
