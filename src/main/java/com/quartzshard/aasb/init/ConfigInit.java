package com.quartzshard.aasb.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;

public class ConfigInit {
	
	public static void init() {
		regClient();
		regServer();
		regCommon();
	}

	private static void regClient() {
		ForgeConfigSpec.Builder CFG = new ForgeConfigSpec.Builder();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CFG.build());
	}
	private static void regServer() {
		ForgeConfigSpec.@NotNull Builder CFG = new ForgeConfigSpec.Builder();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CFG.build());
	}
	private static void regCommon() {
		ForgeConfigSpec.@NotNull Builder CFG = new ForgeConfigSpec.Builder();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CFG.build());
	}
}
