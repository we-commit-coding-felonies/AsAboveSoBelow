package com.quartzshard.aasb.config;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class DebugCfg {
	// Client
	public static BooleanValue
					HITBOX_CLIENT,
					ARROW_PATHFIND;
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
		ARROW_PATHFIND = cfg
				.comment("Creates particles along Sentient Arrow pathfinds")
				.define("debugArrowPathfind", false);
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
	public static BooleanValue
					LOGS;
	public static void common(Builder cfg) {
		LOGS = cfg
				.comment("Outputs a lot of debug stuff to logs", "THIS *WILL* CAUSE MASSIVE FILESIZES")
				.define("debugLogs", false);
	}
}
