package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper help = event.getExistingFileHelper();
        if (event.includeServer()) {
            gen.addProvider(new AASBRecipes(gen));
            gen.addProvider(new AASBLoot(gen));
            AASBTags.BlockTP blockTags = AASBTags.INSTANCE.new BlockTP(gen, help);
            gen.addProvider(blockTags);
            gen.addProvider(AASBTags.INSTANCE.new ItemTP(gen, blockTags, help));
        }
        if (event.includeClient()) {
            gen.addProvider(new AASBBlockStates(gen, help));
            gen.addProvider(new AASBItemModels(gen, help));
            gen.addProvider(AASBLang.INSTANCE.new Provider(gen, "en_us"));
            gen.addProvider(new AASBSounds(gen, help));
        }
    }
}
