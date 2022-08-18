package com.quartzshard.as_above_so_below.setup;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AsAboveSoBelow.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
    }

    //Common item properties
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModInit.ITEM_GROUP);

    //Items
    public static final RegistryObject<Item> PHILOSOPHERS_STONE = ITEMS.register("philosophers_stone", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> WHITE_STONE = ITEMS.register("white_stone", () -> new Item(ITEM_PROPERTIES));

}
