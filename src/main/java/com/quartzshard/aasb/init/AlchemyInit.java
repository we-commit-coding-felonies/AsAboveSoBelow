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
			MATERIA = make("materia", null, Color.PHILOSOPHERS.I),			//	Matter
				TERRAIN = make("terrain", MATERIA, 0xff8000),				//		Basic Blocks
					SOIL = make("soil", TERRAIN, 0x804000),					//			Dirt & Sand
					ROCK = make("rock", TERRAIN, 0x404040),					//			Stone
						ROUGH = make("rough", ROCK, 0x605040),				//				Bumpy / Cobble
						SMOOTH = make("smooth", ROCK, 0x405060),			//				Smooth / Stone
				MINERAL = make("mineral", MATERIA, 0x8080c0),				//		Ores & Similar
					METAL = make("metal", MINERAL, 0x808080),				//			Pure / Mercury
						SUN = make("aurum", METAL, 0xd4af37),				//				Radiant / Gold
						MOON = make("argentum", METAL, 0xc0c0c0),			//				Reflective / Silver
						VENUS = make("cuprum", METAL, 0xda8a67),			//				Beautiful / Copper
						MARS = make("ferrum", METAL, 0x606060),				//				Sturdy / Iron
						JUPITER = make("stannum", METAL, 0xc0c0b0),			//				Improvable / Tin
						SATURN = make("plumbum", METAL, 0x555a63),			//				Malleable / Lead
					CRYSTAL = make("crystal", MINERAL, 0xbbbbbb),			//			Gems & Crystals
						BRILLIANT = make("brilliant", CRYSTAL, 0xccccdd),	//				Perfect / Diamond, Amethyst
						DULL = make("dull", CRYSTAL, 0xaa9999),				//				Flawed / Quartz, Coal
				ORGANIC = make("organic", MATERIA, 0x889988),				//		Carbony Stuff
					DEAD = make("inanimate", ORGANIC, 0x222222),			//			Nonliving / Charcoal
					ALIVE = make("living", ORGANIC, 0xcc4444),				//			Biological
						PLANT = make("plant", ALIVE, 0xc5deb3),				//				Plantlife / Wood, Flowers
							FOLIAGE = make("foliage", PLANT, 0x40c040),		//					Greenery / Leaves
						BEAST = make("beast", ALIVE, 0x9a614d),				//				Creatures
							ANIMAL = make("animal", BEAST, 0xff8080),		//					Friendly
							MONSTER = make("monster", BEAST, 0x66000),		//					Aggressive
					IMMORTAL = make("immortal", ORGANIC, 0xd4af77),			//			Divine / Minium Stone
				ARCANE = make("arcane", MATERIA, 0x8866aa),					//		Magical
					ALCHEMY = make("alchemy", ARCANE, 0xff0044),			//			As Above, So Below
					ENCHANTING = make("enchanting", ARCANE, 0xa152ff),		//			Vanilla Enchantments
					WITCHCRAFT = make("witchcraft", ARCANE, 0x246658),		//			Vanilla Potions
				ETHEREAL = make("ethereal", MATERIA, 0xa0c0c0),				//		Intangible
					MIND = make("mind", ETHEREAL, 0x90c060),				//			XP-related
					SOUL = make("soul", ETHEREAL, 0x8899ee);				//			Spirit Stuff
		
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
