package com.quartzshard.as_above_so_below.init;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;
import com.quartzshard.as_above_so_below.common.block.AirIceBlock;

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

public class ObjectInit {


    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Items.REG.register(bus);
    }
    
    public class Items {
        public static final DeferredRegister<Item> REG = DeferredRegister.create(ForgeRegistries.ITEMS, AsAboveSoBelow.MODID);
        //Common item properties
        public static final Item.Properties ITEM_PROPERTIES_GENERIC = new Item.Properties().tab(ModInit.ITEM_GROUP);

        //Items
        public static final RegistryObject<Item> PHILOSOPHERS_STONE = REG.register("philosophers_stone", () -> new Item(ITEM_PROPERTIES_GENERIC));
        public static final RegistryObject<Item> MINIUM_STONE = REG.register("minium_stone", () -> new Item(ITEM_PROPERTIES_GENERIC));

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
