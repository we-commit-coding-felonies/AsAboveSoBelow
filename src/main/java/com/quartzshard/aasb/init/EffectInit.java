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
				WHISTLE = registerSound("whistle"),
				
				BARRIER_AMBIENT = registerSound("way.barrier.ambient"),
				BARRIER_PROTECT = registerSound("way.barrier.protect"),
				BARRIER_FAIL = registerSound("way.barrier.fail"),
				BARRIER_IGNORED = registerSound("way.barrier.ignored"),
		
				WAY_WASTE = registerSound("way.waste"),
				WAY_LEAK = registerSound("way.leak"),
				WAY_EXPLODE = registerSound("way.explode"),
				WAY_CHARGE = registerSound("way.charge"),
				WAY_SLASH = registerSound("way.slash"),
		
				TRANSMUTE_SHAPE_GENERIC = registerSound("transmute.generic"),
				TRANSMUTE_SHAPE_WATER = registerSound("transmute.water"),
				TRANSMUTE_SHAPE_EARTH = registerSound("transmute.earth"),
				TRANSMUTE_SHAPE_FIRE = registerSound("transmute.fire"),
				TRANSMUTE_SHAPE_AIR = registerSound("transmute.air"),
				TRANSMUTE_SHAPE_ALIVE = registerSound("transmute.living"),
				
				SENTIENT_WHISPERS = registerSound("sentient.whispers");
		
		private static RegistryObject<SoundEvent> registerSound(String name) {
			return REG.register(name, () -> new SoundEvent(AsAboveSoBelow.rl(name)));
		}
		
	}
}
