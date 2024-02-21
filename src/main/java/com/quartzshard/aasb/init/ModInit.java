package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.PlayerUtil.PlayerSelectedHandProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * Deals with initializing some misc core parts of the mod, and the creative tabs <br>
 * also does some stupid capability bullshit
 */
@Mod.EventBusSubscriber(modid = AASB.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModInit {

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AASB.MODID);

	public static void init(@NotNull IEventBus bus) {
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
				for (@NotNull RegistryObject<? extends Item> ro : ItemInit.ALL_NATURAL_ITEMS) {
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
	
	public static void attachEntityCaps(AttachCapabilitiesEvent<Entity> event){
		if (event.getObject() instanceof Player) {
			if (!event.getObject().getCapability(PlayerSelectedHandProvider.PLAYER_SELECTED_HAND).isPresent()) {
				event.addCapability(AASB.rl("player_selected_hand"), new PlayerSelectedHandProvider());
			}
		}
	}

	public static void onPlayerCloned(PlayerEvent.Clone event) {
		if (event.isWasDeath()) { // so its not lost on death
			event.getOriginal().getCapability(PlayerSelectedHandProvider.PLAYER_SELECTED_HAND).ifPresent(oldStore -> {
				event.getEntity().getCapability(PlayerSelectedHandProvider.PLAYER_SELECTED_HAND).ifPresent(newStore -> {
					newStore.copyFrom(oldStore);
				});
			});
		}
	}

	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.register(PlayerUtil.PlayerSelectedHand.class);
	}
}
