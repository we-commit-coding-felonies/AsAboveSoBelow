package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.client.AASBKeys;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {
    public static void init(final FMLClientSetupEvent event) {
    	AASBKeys.register();
    }
}
