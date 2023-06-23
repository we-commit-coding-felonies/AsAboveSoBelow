package com.quartzshard.aasb.init;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.shape.AirRune;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.shape.EarthRune;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.shape.FireRune;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.shape.WaterRune;
import com.quartzshard.aasb.util.ColorsHelper.Color;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class AlchemyInit {
	
	
	public static void init(IEventBus bus) {
		FormTree.REG.register(bus);
		TrinketRunes.REG.register(bus);
	}
		
		
	public class FormTree { 
		private static final DeferredRegister<AspectForm> REG = DeferredRegister.create(AsAboveSoBelow.rl("form_tree"), AsAboveSoBelow.MODID);
		public static final Supplier<IForgeRegistry<AspectForm>> REGISTRY_SUPPLIER = REG.makeRegistry(AspectForm.class, () -> {
	       		RegistryBuilder<AspectForm> builder = new RegistryBuilder<>();
	       		return builder;
	       	}
		);
	
		public static final RegistryObject<AspectForm> 
			MATERIA = make("materia", null, Color.PHILOSOPHERS.I),
				TERRAIN = make("terrain", MATERIA, Color.BROWN.I),
				MINERAL = make("mineral", MATERIA, Color.MID_GRAY.I),
					METAL = make("metal", MINERAL, 0xbbbbbb),
				ORGANIC = make("organic", MATERIA, Color.MID_GREEN.I),
				ARCANE = make("arcane", MATERIA, Color.MID_PURPLE.I),
				ETHEREAL = make("ethereal", MINERAL, Color.MID_TEAL.I);
		
		private static RegistryObject<AspectForm> make(String name, RegistryObject<AspectForm> parent, int color) {
			return REG.register(name, () -> new AspectForm(AsAboveSoBelow.rl(name), parent == null ? null : parent.get(), color));
		}

		public static IForgeRegistry<AspectForm> getReg() {
			return REGISTRY_SUPPLIER.get();
		}
		public static boolean exists(ResourceLocation rl) {
			return getReg().containsKey(rl);
		}
		@Nullable
		public static AspectForm get(ResourceLocation rl) {
			if (exists(rl)) {
				return getReg().getValue(rl);
			}
			return null;
		}
	}
	
	public class TrinketRunes {
	    private static final DeferredRegister<TrinketRune> REG = DeferredRegister.create(AsAboveSoBelow.rl("trinket_runes"), AsAboveSoBelow.MODID);
	    public static final Supplier<IForgeRegistry<TrinketRune>> REGISTRY_SUPPLIER = REG.makeRegistry(TrinketRune.class, () -> {
	            	RegistryBuilder<TrinketRune> builder = new RegistryBuilder<>();
	            	return builder;
	            }
	    );
		public static final RegistryObject<TrinketRune>
				WATER = REG.register("water", () -> new WaterRune()),
				EARTH = REG.register("earth", () -> new EarthRune()),
				FIRE = REG.register("fire", () -> new FireRune()),
				AIR = REG.register("air", () -> new AirRune());

		public static IForgeRegistry<TrinketRune> getReg() {
			return REGISTRY_SUPPLIER.get();
		}
		public static boolean exists(ResourceLocation rl) {
			return getReg().containsKey(rl);
		}
		@Nullable
		public static TrinketRune get(ResourceLocation rl) {
			if (exists(rl)) {
				return getReg().getValue(rl);
			}
			return null;
		}
	}
}
