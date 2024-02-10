package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AASB;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * particles / sounds
 */
public class FxInit {

	public static void init(IEventBus bus) {
		SOUNDS.register(bus);
		PARTICLES.register(bus);
	}
	private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AASB.MODID);
	private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AASB.MODID);
		
	public static final RegistryObject<SimpleParticleType> PTC_CUT = PARTICLES.register("cut_particle", () -> new SimpleParticleType(true));
	
	
		
		
	public static final RegistryObject<SoundEvent>
			SND_SENTIENT_WHISPERS = snd("sentient.whispers"),
					
			SND_TRANSMUTE_GENERIC = snd("transmute.generic"),
			SND_TRANSMUTE_WATER = snd("transmute.water"),
			SND_TRANSMUTE_EARTH = snd("transmute.earth"),
			SND_TRANSMUTE_FIRE = snd("transmute.fire"),
			SND_TRANSMUTE_AIR = snd("transmute.air"),
			SND_TRANSMUTE_ALIVE = snd("transmute.living"),
	
			SND_WAY_WASTE = snd("way.waste"),
			SND_WAY_LEAK = snd("way.leak"),
			SND_WAY_EXPLODE = snd("way.explode"),
			SND_WAY_CHARGE = snd("way.charge"),
			SND_WAY_SLASH = snd("way.slash"),
			
			SND_BARRIER_AMBIENT = snd("way.barrier.ambient"),
			SND_BARRIER_PROTECT = snd("way.barrier.protect"),
			SND_BARRIER_FAIL = snd("way.barrier.fail"),
			
			SND_TRINKET_GLOVE = snd("trinket.glove"),
			SND_TRINKET_RING = snd("trinket.ring"),
			SND_TRINKET_CHARM = snd("trinket.charm"),

			SND_TARGETLOCK = snd("misc.targetlock"),
			SND_WHISTLE = snd("misc.whistle"),
			SND_MUSTANG = snd("misc.mustang"),
			SND_TICK = snd("misc.tick"),
			SND_ELIXIR = snd("misc.elixir");

	private static RegistryObject<SoundEvent> snd(String name) {
		return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(AASB.rl(name)));
	}
	private static RegistryObject<SoundEvent> snd(String name, float range) {
		return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(AASB.rl(name), range));
	}
}
