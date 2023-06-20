package com.quartzshard.aasb.data;

import java.util.function.Supplier;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.init.EffectInit;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class AASBSounds extends SoundDefinitionsProvider {

	protected AASBSounds(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, AsAboveSoBelow.MODID, helper);
	}

	@Override
	public void registerSounds() {
		simple(EffectInit.Sounds.BARRIER_AMBIENT, new ResourceLocation("block/beacon/ambient"), 0.5);
		simple(EffectInit.Sounds.BARRIER_PROTECT, "way/barrier/protect");
		simple(EffectInit.Sounds.BARRIER_FAIL, "way/barrier/fail");
		//simple(EffectInit.Sounds.BARRIER_IGNORED, "way/barrier/ignored");
		
		simple(EffectInit.Sounds.WAY_WASTE, "way/waste");
		simple(EffectInit.Sounds.WAY_LEAK, "way/leak");
		simple(EffectInit.Sounds.WAY_EXPLODE, "way/explode");
		simple(EffectInit.Sounds.WAY_CHARGE, "way/charge");
		simple(EffectInit.Sounds.WAY_SLASH, "way/slash");
	}

	private void simple(Supplier<SoundEvent> ro, String loc) {
		simple(ro, AsAboveSoBelow.rl(loc));
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
			rlocs[i] = AsAboveSoBelow.rl(locs[i]);
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
	
	private String subFor(Supplier<SoundEvent> ro) {
		return "subtitles." + ro.get().getLocation().toString().replace(':', '.').replace('/', '.');
	}
	
	


	@Override
	public String getName() {
		return AsAboveSoBelow.DISPLAYNAME + " | sounds.json";
	}

}
