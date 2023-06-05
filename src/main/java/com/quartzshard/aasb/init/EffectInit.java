package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;

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
        //PARTICLES.register(bus);
    }
	public class Sounds {
		private static final DeferredRegister<SoundEvent> REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AsAboveSoBelow.MODID);
		
	    public static final RegistryObject<SoundEvent> BARRIER_AMBIENT = registerSound("barrier.ambient");
	    public static final RegistryObject<SoundEvent> BARRIER_PROTECT = registerSound("barrier.protect");
	    public static final RegistryObject<SoundEvent> BARRIER_FAIL = registerSound("barrier.fail");
	    public static final RegistryObject<SoundEvent> BARRIER_IGNORED = registerSound("barrier.ignored");
	    
	    public static final RegistryObject<SoundEvent> WAY_WASTE = registerSound("way.waste");
	    public static final RegistryObject<SoundEvent> WAY_LEAK = registerSound("way.leak");
	    public static final RegistryObject<SoundEvent> WAY_EXPLODE = registerSound("way.explode");
	    
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
