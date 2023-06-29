package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * FX, like sounds and particles
 * @author solunareclipse1
 *
 */
public class EffectInit {

	public static void init(IEventBus bus) {
		Sounds.REG.register(bus);
		Particles.REG.register(bus);
	}
	public class Particles {
		private static final DeferredRegister<ParticleType<?>> REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AsAboveSoBelow.MODID);
		
		public static final RegistryObject<SimpleParticleType> CUT_PARTICLE = REG.register("cut_particle", () -> new SimpleParticleType(true));
	}
	
	
	public class Sounds {
		private static final DeferredRegister<SoundEvent> REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AsAboveSoBelow.MODID);
		
		public static final RegistryObject<SoundEvent>
				SENTIENT_WHISPERS = snd("sentient.whispers"),
						
				TRANSMUTE_SHAPE_GENERIC = snd("transmute.generic"),
				TRANSMUTE_SHAPE_WATER = snd("transmute.water"),
				TRANSMUTE_SHAPE_EARTH = snd("transmute.earth"),
				TRANSMUTE_SHAPE_FIRE = snd("transmute.fire"),
				TRANSMUTE_SHAPE_AIR = snd("transmute.air"),
				TRANSMUTE_SHAPE_ALIVE = snd("transmute.living"),
		
				WAY_WASTE = snd("way.waste"),
				WAY_LEAK = snd("way.leak"),
				WAY_EXPLODE = snd("way.explode"),
				WAY_CHARGE = snd("way.charge"),
				WAY_SLASH = snd("way.slash"),
				
				BARRIER_AMBIENT = snd("way.barrier.ambient"),
				BARRIER_PROTECT = snd("way.barrier.protect"),
				BARRIER_FAIL = snd("way.barrier.fail"),
				BARRIER_IGNORED = snd("way.barrier.ignored"),
				
				TRINKET_GLOVE = snd("trinket.glove"),
				TRINKET_RING = snd("trinket.ring"),
				TRINKET_CHARM = snd("trinket.charm"),

				TARGETLOCK = snd("misc.targetlock"),
				WHISTLE = snd("misc.whistle"),
				MUSTANG = snd("misc.mustang");
		
		private static RegistryObject<SoundEvent> snd(String name) {
			return REG.register(name, () -> new SoundEvent(AsAboveSoBelow.rl(name)));
		}
		
	}
}
