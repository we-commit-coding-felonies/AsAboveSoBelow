package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.object.ItemInit;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Deals with initializing some misc core parts of the mod, and the creative tabs
 */
@Mod.EventBusSubscriber(modid = AASB.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModInit {

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AASB.MODID);

	public static void init(IEventBus bus) {
		TABS.register(bus);
		NetInit.register();
		bus.addListener(ModInit::commonSetup);
	}
	
	public static void commonSetup(final FMLCommonSetupEvent event) {
	}
	
	public static final RegistryObject<CreativeModeTab>
		NATURAL = TABS.register("natural", () -> CreativeModeTab.builder()
			.title(Component.translatable(LangData.CTAB_NATURAL))
			.withTabsBefore(CreativeModeTabs.COMBAT)
			.icon(() -> ItemInit.THE_PHILOSOPHERS_STONE.get().getDefaultInstance())
			.displayItems((parameters, tab) -> {
				for (RegistryObject<? extends Item> ro : ItemInit.ALL_NATURAL_ITEMS) {
					tab.accept(ro.get());
				}
			}).build()),
		SYNTHETIC = TABS.register("synthetic", () -> CreativeModeTab.builder()
				.title(Component.translatable(LangData.CTAB_SYNTHETIC))
				.withTabsBefore(NATURAL.getId())
				.icon(() -> ItemInit.ELIXIR_OF_LIFE.get().getDefaultInstance())
				.displayItems((parameters, tab) -> {
					for (RegistryObject<? extends Item> ro : ItemInit.ALL_SYNTHETIC_ITEMS) {
						tab.accept(ro.get());
					}
				}).build());
}
