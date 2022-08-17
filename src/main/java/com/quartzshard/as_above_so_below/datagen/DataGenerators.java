package com.quartzshard.as_above_so_below.datagen;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new AASBRecipes(generator));
            generator.addProvider(new AASBLootTables(generator));
            AASBBlockTags blockTags = new AASBBlockTags(generator, event.getExistingFileHelper());
            generator.addProvider(blockTags);
            generator.addProvider(new AASBItemTags(generator, blockTags, event.getExistingFileHelper()));
        }
        if (event.includeClient()) {
            generator.addProvider(new AASBBlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(new AASBItemModels(generator, event.getExistingFileHelper()));
            generator.addProvider(new AASBLanguageProvider(generator, "en_gb"));
            generator.addProvider(new AASBLanguageProvider(generator, "en_us"));

        }
    }
}
