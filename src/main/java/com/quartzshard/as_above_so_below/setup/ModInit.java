package com.quartzshard.as_above_so_below.setup;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModInit {
    public static void init(final FMLCommonSetupEvent event) {
    }

    public static final String TAB_NAME = AsAboveSoBelow.DISPLAYNAME;
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.PHILOSOPHERS_STONE.get());
        }
    };
}
