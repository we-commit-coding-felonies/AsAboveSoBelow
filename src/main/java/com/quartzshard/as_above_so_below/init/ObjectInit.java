package com.quartzshard.as_above_so_below.init;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraft.world.item.Item;
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
        public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModInit.ITEM_GROUP);

        //Items
        public static final RegistryObject<Item> PHILOSOPHERS_STONE = REG.register("philosophers_stone", () -> new Item(ITEM_PROPERTIES));
        public static final RegistryObject<Item> MINIUM_STONE = REG.register("minium_stone", () -> new Item(ITEM_PROPERTIES));
    }

}
