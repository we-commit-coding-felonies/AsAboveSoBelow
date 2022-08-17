package com.quartzshard.as_above_so_below;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.quartzshard.as_above_so_below.setup.ClientInit;
import com.quartzshard.as_above_so_below.setup.ItemInit;
import com.quartzshard.as_above_so_below.setup.ModInit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AsAboveSoBelow.MODID)
public class AsAboveSoBelow
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "as_above_so_below";
    public static final String DISPLAYNAME = "As Above, So Below";

    public AsAboveSoBelow()
    {
        ItemInit.init();

        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModInit::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientInit::init));

        MinecraftForge.EVENT_BUS.register(this);
    }
}
