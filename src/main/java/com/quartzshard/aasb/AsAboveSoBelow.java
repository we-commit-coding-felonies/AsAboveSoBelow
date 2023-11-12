package com.quartzshard.aasb;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.ItemData;
import com.quartzshard.aasb.api.alchemy.PhilosophersStone;
import com.quartzshard.aasb.api.alchemy.RecipeData;
import com.quartzshard.aasb.init.AlchemyInit;
import com.quartzshard.aasb.init.ClientInit;
import com.quartzshard.aasb.init.ConfigInit;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.init.ModInit;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Items;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
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
	
	@Nullable
	private ServerResourceCache cache;

	public AsAboveSoBelow() {
		
		IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		
		LogHelper.info("AsAboveSoBelow()", "Initializing", LOG_SPLASHES[RAND.nextInt(LOG_SPLASHES.length)]);
		ObjectInit.init(modbus);
		AlchemyInit.init(modbus);
		EffectInit.init(modbus);
		ConfigInit.init(); // Uses different bus, ¯\_(ツ)_/¯
		modbus.addListener(ModInit::init);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientInit::init));
		MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
		MinecraftForge.EVENT_BUS.addListener(this::onTagsUpdated);

		//MinecraftForge.EVENT_BUS.register(this);
	}
	
	private void onAddReloadListeners(AddReloadListenerEvent event) {
		//PhilosophersStone.getAllRecipes(event.getServerResources())
		event.addListener((ResourceManagerReloadListener) manager -> cache = new ServerResourceCache(event.getServerResources(), manager));
	}
	
	private void onTagsUpdated(TagsUpdatedEvent event) {
		if (cache != null) {
			long start = System.currentTimeMillis();
			try {
				Map<ResourceLocation, RecipeData> allRecipes = PhilosophersStone.getAllRecipes(cache.resources());
				/*
				Map<ResourceLocation, RecipeData> searchResults = PhilosophersStone.searchRecipesFor(ItemData.fromItem(Items.DEBUG_STICK), allRecipes);
				
				String resultStr = "";
				if (searchResults != null) for (Entry<ResourceLocation,RecipeData> dat : searchResults.entrySet()) {
					resultStr += dat.getKey().toString() + ", ";
				}
				LogHelper.debug("AsAboveSoBelow.tagsUpdated()", "SearchResults", resultStr);
				*/
				LogHelper.info("AsAboveSoBelow.tagsUpdated()", "MapperDone", "Alchemy mapping completed! (" + (System.currentTimeMillis() - start) + "ms)");
			} catch (Throwable t) {
				LogHelper.error("AsAboveSoBelow.tagsUpdated()", "MapperFailure", "Failed to finish alchemy mapping! (" + (System.currentTimeMillis() - start) +"ms)");
				System.out.println(t.getLocalizedMessage());
				t.printStackTrace();
			}
			cache = null;
		}
	}
	
	
	public static ResourceLocation rl(String rl) {
		return new ResourceLocation(MODID, rl);
	}
	
	private record ServerResourceCache(ReloadableServerResources resources, ResourceManager manager) {
		
	}
}
