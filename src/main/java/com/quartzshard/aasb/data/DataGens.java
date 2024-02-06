package com.quartzshard.aasb.data;

import java.util.concurrent.CompletableFuture;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.data.tags.*;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AASB.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGens {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput out = gen.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lp = event.getLookupProvider();
		ExistingFileHelper help = event.getExistingFileHelper();
		
		if (event.includeClient()) {
			//gen.addProvider(new AASBBlockStates(gen, help));
			gen.addProvider(true, new ItemModelData(out, help));
			gen.addProvider(true, new LangData(out));
			gen.addProvider(true, new SoundData(out, help));
		}
		
		if (event.includeServer()) {
			GenProvider gp = new GenProvider(out, lp);
			lp = gp.getRegistryProvider();
			gen.addProvider(true, gp);
			//gen.addProvider(new AASBRecipes(gen));
			//gen.addProvider(new AASBLoot(gen));
			BlockTP btp = new BlockTP(out, lp, help);
			gen.addProvider(true, btp);
			gen.addProvider(true, new ItemTP(out, lp, help, btp));
			gen.addProvider(true, new EntityTP(out, lp, help));
			gen.addProvider(true, new DmgTP(out, lp, help));
		}
	}
}
