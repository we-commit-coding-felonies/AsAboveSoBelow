package com.quartzshard.aasb;

import java.util.Random;

import com.quartzshard.aasb.init.ClientInit;
import com.quartzshard.aasb.init.ConfigInit;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.init.ModInit;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AsAboveSoBelow.MODID)
public class AsAboveSoBelow {
	public static final String MODID = "aasb";
	public static final String DISPLAYNAME = "As Above, So Below";
	/** provides easy access to the Random class */
	public static final Random RAND = new Random();
	
	private static final String[] LOG_SPLASHES = {
			"Initializing mod, please wait...",
			"Transmuting Minecraft, please wait...",
			"Preparing to commit crimes against nature...",
			"Accessing forbidden knowledge, please wait...",
			"As Above, So Below is not responsible for any injury, maiming, death, or mental degradation that may occur...",
			"The Sentient Arrow will probably threaten to stab you, and in fact, is fully capable of speech...",
			"One shudders to imagine what inhuman code lies behind this mod...",
			"Repairing sickles, please wait...",
			"How to invincibility as cubic magician? The world may never know...",
			"Water, Earth, Fire, Air, Water, Earth, Fire, Air, Water, Earth...",
			"Skipping funny message, please wait..."
	};

	public AsAboveSoBelow() {
		LogHelper.info("AsAboveSoBelow()", "Initializing", LOG_SPLASHES[RAND.nextInt(LOG_SPLASHES.length)]);
		ObjectInit.init();
		EffectInit.init();
		ConfigInit.init();

		IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		modbus.addListener(ModInit::init);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientInit::init));

		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public static ResourceLocation rl(String rl) {
		return new ResourceLocation(MODID, rl);
	}
}
