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

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Sounds.REG.register(bus);
        //MOB_EFFECTS.register(bus);
        Particles.REG.register(bus);
    }
    public class Particles {
        private static final DeferredRegister<ParticleType<?>> REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AsAboveSoBelow.MODID);
        
        public static final RegistryObject<SimpleParticleType> CUT_PARTICLE = REG.register("cut_particle", () -> new SimpleParticleType(true));
    }
    
    
	public class Sounds {
		private static final DeferredRegister<SoundEvent> REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AsAboveSoBelow.MODID);
		
	    public static final RegistryObject<SoundEvent> BARRIER_AMBIENT = registerSound("way.barrier.ambient");
	    public static final RegistryObject<SoundEvent> BARRIER_PROTECT = registerSound("way.barrier.protect");
	    public static final RegistryObject<SoundEvent> BARRIER_FAIL = registerSound("way.barrier.fail");
	    public static final RegistryObject<SoundEvent> BARRIER_IGNORED = registerSound("way.barrier.ignored");
	    
	    public static final RegistryObject<SoundEvent> WAY_WASTE = registerSound("way.waste");
	    public static final RegistryObject<SoundEvent> WAY_LEAK = registerSound("way.leak");
	    public static final RegistryObject<SoundEvent> WAY_EXPLODE = registerSound("way.explode");
	    public static final RegistryObject<SoundEvent> WAY_CHARGE = registerSound("way.charge");
	    public static final RegistryObject<SoundEvent> WAY_SLASH = registerSound("way.slash");
	    
	    public static final RegistryObject<SoundEvent> TRANSMUTE_SHAPE_GENERIC = registerSound("transmute.generic");
	    public static final RegistryObject<SoundEvent> TRANSMUTE_SHAPE_WATER = registerSound("transmute.water");
	    public static final RegistryObject<SoundEvent> TRANSMUTE_SHAPE_EARTH = registerSound("transmute.earth");
	    public static final RegistryObject<SoundEvent> TRANSMUTE_SHAPE_FIRE = registerSound("transmute.fire");
	    public static final RegistryObject<SoundEvent> TRANSMUTE_SHAPE_AIR = registerSound("transmute.air");
	    public static final RegistryObject<SoundEvent> TRANSMUTE_SHAPE_ALIVE = registerSound("transmute.living");
	    
	    private static RegistryObject<SoundEvent> registerSound(String name) {
	    	return REG.register(name, () -> new SoundEvent(new ResourceLocation(AsAboveSoBelow.MODID, name)));
	    }
		
	}
}
