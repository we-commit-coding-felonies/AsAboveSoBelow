package com.quartzshard.aasb;

import com.quartzshard.aasb.api.alchemy.Phil;
import com.quartzshard.aasb.client.Keybinds;
import com.quartzshard.aasb.init.*;
import com.quartzshard.aasb.init.object.*;
import com.quartzshard.aasb.util.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import java.util.Random;

@Mod(AASB.MODID)
public class AASB {
	public static final String MODID = "aasb";

	/** silly things we print during init */
	private static final String[] LOG_SPLASHES = {
			"Initializing mod...",
			"Transmuting Minecraft...",
			"Preparing to commit crimes against nature...",
			"Accessing forbidden knowledge...",
			"As Above, So Below is not responsible for any injury, maiming, death, or mental degradation that may occur...",
			"The Sentient Arrow will probably threaten to stab you, and in fact, is fully capable of speech...",
			"One shudders to imagine what inhuman code lies behind this mod...",
			"Repairing sickles...",
			"How to invincibility as cuboid magician? The world may never know...",
			"...Water, Earth, Fire, Air, Water, Earth, Fire, Air, Water, Earth...",
			"Skipping funny message...",
			"Rewriting the entire codebase...",
			"Adding more funny messages...",
			"Creating a bit of chaos...",
			"Transparenting high-resolution shadows...",
			"Petting cats for a bit...",
			"Deciding what funny number of funny messages there should be...",
			"coffee.speech.NothingArrowExclusion, or something like that...",
			"Procrastinating...",
			"Committing crimes...",
			"Finishing up? No, just getting started...",
			"Calculating 9+10, please wait..."
	};
	
	/** so we dont have to create instances of Random everywhere */
	public static final Random RNG = new Random();

	public AASB() {
		Logger.info("new AASB()", "Initializing", LOG_SPLASHES[RNG.nextInt(LOG_SPLASHES.length)]);
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		BlockInit.init(bus);
		EntityInit.init(bus);
		ItemInit.init(bus);
		AlchInit.init(bus);
		FxInit.init(bus);
		ConfigInit.init();
		ModInit.init(bus);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			bus.addListener(ClientInit::init);
			bus.addListener(Keybinds::register);
		});

		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
		MinecraftForge.EVENT_BUS.addListener(Phil::mapAspects);
	}

	// https://github.com/sinkillerj/ProjectE/blob/68fbb2dea0cf8a6394fa6c7c084063046d94cee5/src/main/java/moze_intel/projecte/PECore.java#L347
	private void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener((ResourceManagerReloadListener) manager -> Phil.stashReloadData(event.getServerResources(), event.getRegistryAccess(), manager));
	}
	
	public static ResourceLocation rl(String path) {
		return new ResourceLocation(MODID, path);
	}
}
