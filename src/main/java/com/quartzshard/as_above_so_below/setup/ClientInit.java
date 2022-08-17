package com.quartzshard.as_above_so_below.setup;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {
    public static void init(final FMLClientSetupEvent event) {
    }
}
