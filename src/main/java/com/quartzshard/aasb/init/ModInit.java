package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.network.AASBNet;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModInit {
    public static void init(final FMLCommonSetupEvent event) {
    	AASBNet.register();
    }

    public static final String TAB_NAME = AsAboveSoBelow.MODID;
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ObjectInit.Items.PHILOSOPHERS_STONE.get());
        }
    };
}
