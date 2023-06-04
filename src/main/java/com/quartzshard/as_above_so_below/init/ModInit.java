package com.quartzshard.as_above_so_below.init;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModInit {
    public static void init(final FMLCommonSetupEvent event) {
    }

    public static final String TAB_NAME = AsAboveSoBelow.MODID;
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ObjectInit.Items.PHILOSOPHERS_STONE.get());
        }
    };
}