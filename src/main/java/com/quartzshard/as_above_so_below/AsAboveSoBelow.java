package com.quartzshard.as_above_so_below;

import com.quartzshard.as_above_so_below.init.ClientInit;
import com.quartzshard.as_above_so_below.init.ObjectInit;
import com.quartzshard.as_above_so_below.init.ModInit;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AsAboveSoBelow.MODID)
public class AsAboveSoBelow {
	public static final String MODID = "as_above_so_below";
	public static final String DISPLAYNAME = "As Above, So Below";

	public AsAboveSoBelow() {
		ObjectInit.init();

		IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		modbus.addListener(ModInit::init);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientInit::init));

		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public static ResourceLocation rl(String rl) {
		return new ResourceLocation(MODID, rl);
	}
}
