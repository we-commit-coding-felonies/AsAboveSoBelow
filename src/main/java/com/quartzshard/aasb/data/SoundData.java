package com.quartzshard.aasb.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.init.FxInit;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class SoundData extends SoundDefinitionsProvider {

	protected SoundData(PackOutput out, ExistingFileHelper helper) {
		super(out, AASB.MODID, helper);
	}

	@Override
	public void registerSounds() {
		multi(FxInit.SND_WHISTLE,
				"misc/whistle1",
				"misc/whistle2",
				"misc/whistle3");
		simple(FxInit.SND_MUSTANG, "misc/mustang");
		simple(FxInit.SND_TARGETLOCK, "misc/targetlock");
		
		simple(FxInit.SND_BARRIER_AMBIENT, new ResourceLocation("block/beacon/ambient"), 0.5);
		simple(FxInit.SND_BARRIER_PROTECT, "way/barrier/protect");
		simple(FxInit.SND_BARRIER_FAIL, "way/barrier/fail");
		
		simple(FxInit.SND_WAY_WASTE, "way/waste");
		simple(FxInit.SND_WAY_LEAK, "way/leak");
		simple(FxInit.SND_WAY_EXPLODE, "way/explode");
		simple(FxInit.SND_WAY_CHARGE, "way/charge");
		simple(FxInit.SND_WAY_SLASH, "way/slash");
		
		simple(FxInit.SND_SENTIENT_WHISPERS, "sentient/whispers");
		
		multi(FxInit.SND_TRINKET_GLOVE,
				"trinket/glove1",
				"trinket/glove2",
				"trinket/glove3",
				"trinket/glove4");
		multi(FxInit.SND_TRINKET_RING,
				"trinket/band1",
				"trinket/band2",
				"trinket/band3",
				"trinket/band4");
		multi(FxInit.SND_TRINKET_CHARM,
				"trinket/charm1");
		
		simple(FxInit.SND_TICK, "misc/tick");
		
		Map<ResourceLocation,Double> snds = new HashMap<>();
		snds.put(new ResourceLocation("item/bottle/drink_honey1"), 1.0);
		snds.put(new ResourceLocation("item/bottle/drink_honey2"), 1.0);
		snds.put(new ResourceLocation("item/bottle/drink_honey3"), 0.35);
		snds.put(new ResourceLocation("item/bottle/drink_honey3"), 0.75);
		multi(FxInit.SND_ELIXIR, snds);
	}

	private void simple(Supplier<SoundEvent> ro, String loc) {
		simple(ro, AASB.rl(loc));
	}
	private void simple(Supplier<SoundEvent> ro, ResourceLocation loc) {
		add(ro, definition().with(sound(loc)).subtitle(subFor(ro)));
	}
	private void simple(Supplier<SoundEvent> ro, ResourceLocation loc, double pitch) {
		add(ro, definition().with(sound(loc).pitch(pitch)).subtitle(subFor(ro)));
	}
	
	private void multi(Supplier<SoundEvent> ro, String... locs) {
		ResourceLocation[] rlocs = new ResourceLocation[locs.length];
		for (int i = 0; i < locs.length; i++) {
			rlocs[i] = AASB.rl(locs[i]);
		}
		multi(ro, rlocs);
	}
	private void multi(Supplier<SoundEvent> ro, ResourceLocation... locs) {
		SoundDefinition def = definition();
		for (ResourceLocation loc : locs) {
			def.with(sound(loc));
		}
		add(ro, def.subtitle(subFor(ro)));
	}
	private void multi(Supplier<SoundEvent> ro, Map<ResourceLocation,Double> locs) {
		SoundDefinition def = definition();
		for (Map.Entry<ResourceLocation,Double> loc : locs.entrySet()) {
			def.with(sound(loc.getKey()).pitch(loc.getValue()));
		}
		add(ro, def.subtitle(subFor(ro)));
	}
	
	public static String subFor(Supplier<SoundEvent> ro) {
		return "subtitles." + ro.get().getLocation().toString().replace(':', '.').replace('/', '.');
	}
	
	


	@Override
	public String getName() {
		return AASB.MODID.toUpperCase() + " | Sounds";
	}

}
