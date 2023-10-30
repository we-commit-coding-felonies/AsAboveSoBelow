package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.alchemy.PhilosophersStone;
import com.quartzshard.aasb.common.entity.living.HorrorEntity;
import com.quartzshard.aasb.common.network.AASBNet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModInit {
	public static void init(final FMLCommonSetupEvent event) {
		AASBNet.register();
		//TODO: Invoke the mapper here, once it exists.
	}

	public static final String TAB_NAME = AsAboveSoBelow.MODID;
	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ObjectInit.Items.PHILOSOPHERS_STONE.get());
		}
	};
	
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ObjectInit.Entities.HORROR.get(), HorrorEntity.defaultAttributes().build());
    }
}
