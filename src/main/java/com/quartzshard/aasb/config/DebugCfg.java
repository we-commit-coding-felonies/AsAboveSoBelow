package com.quartzshard.aasb.config;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class DebugCfg {
	// Client
	public static BooleanValue
					HITBOX_CLIENT;
	public static ConfigValue<String>
					HALO_UUID;
	public static void client(Builder cfg) {
		cfg.comment("Settings used for debugging. Enable these at your own risk.").push("debug");
		HITBOX_CLIENT = cfg
				.comment("Draw hitboxes with particles")
				.define("debugClientSideHitboxes", false);
		HALO_UUID = cfg
				.comment("Jewellery halo UUID override, only works in development environments")
				.define("debugCustomHaloFor", "nobody");
		cfg.pop();
	}
	
	// Server
	public static BooleanValue
					HITBOX_SERVER;
	public static void server(Builder cfg) {
		cfg.comment("Settings used for debugging. Enable these at your own risk.").push("debug");
		HITBOX_SERVER = cfg
				.comment("Draws server-only hitboxes using particles")
				.define("debugServerSideHitboxes", false);
		cfg.pop();
	}
	
	// Common
}