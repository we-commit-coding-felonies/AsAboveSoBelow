package com.quartzshard.aasb.init;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.aspect.FormAspect;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.form.*;
import com.quartzshard.aasb.api.alchemy.rune.shape.*;
import com.quartzshard.aasb.util.Colors;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

/**
 * Here is where things related to the alchemy system are initialized, such as the form tree
 */
public class AlchInit {
	
	public static void init(IEventBus bus) {
		FORMS.register(bus);
		RUNES.register(bus);
	}
	
	private static final DeferredRegister<FormAspect> FORMS = DeferredRegister.create(AASB.rl("forms"), AASB.MODID);
	public static final Supplier<IForgeRegistry<FormAspect>> FORMS_SUPPLIER = FORMS.makeRegistry(() -> {
		RegistryBuilder<FormAspect> builder = new RegistryBuilder<>();
		return builder;
	});
	
	public static final RegistryObject<FormAspect> 
		MATERIA = makeForm("materia", null, Colors.PHILOSOPHERS.I),			//	Matter
			TERRAIN = makeForm("terrain", MATERIA, 0xff8000),				//		Basic Blocks
				SOIL = makeForm("soil", TERRAIN, 0x804000),					//			Dirt & Sand
				ROCK = makeForm("rock", TERRAIN, 0x404040),					//			Stone
					ROUGH = makeForm("rough", ROCK, 0x605040),				//				Bumpy / Cobble
					SMOOTH = makeForm("smooth", ROCK, 0x405060),			//				Smooth / Stone
			MINERAL = makeForm("mineral", MATERIA, 0x8080c0),				//		Ores & Similar
				METAL = makeForm("metal", MINERAL, 0x808080),				//			Pure Metal / Mercury
					SUN = makeForm("aurum", METAL, 0xd4af37),				//				Radiant / Gold
					MOON = makeForm("argentum", METAL, 0xc0c0c0),			//				Reflective / Silver
					VENUS = makeForm("cuprum", METAL, 0xda8a67),			//				Beautiful / Copper
					MARS = makeForm("ferrum", METAL, 0x606060),				//				Sturdy / Iron
					JUPITER = makeForm("stannum", METAL, 0xc0c0b0),			//				Improvable / Tin
					SATURN = makeForm("plumbum", METAL, 0x555a63),			//				Malleable / Lead
				CRYSTAL = makeForm("crystal", MINERAL, 0xbbbbbb),			//			Gems & Crystals
					BRILLIANT = makeForm("brilliant", CRYSTAL, 0xccccdd),	//				Perfect / Diamond, Amethyst
					DULL = makeForm("dull", CRYSTAL, 0xaa9999),				//				Flawed / Quartz, Coal
			ORGANIC = makeForm("organic", MATERIA, 0x889988),				//		Carbony Stuff
				DEAD = makeForm("inanimate", ORGANIC, 0x222222),			//			Nonliving / Charcoal
				ALIVE = makeForm("living", ORGANIC, 0xcc4444),				//			Biological
					PLANT = makeForm("plant", ALIVE, 0xc5deb3),				//				Plantlife / Wood, Flowers
						FOLIAGE = makeForm("foliage", PLANT, 0x40c040),		//					Greenery / Leaves
					BEAST = makeForm("beast", ALIVE, 0x9a614d),				//				Creatures
						ANIMAL = makeForm("animal", BEAST, 0xff8080),		//					Friendly
						MONSTER = makeForm("monster", BEAST, 0x66000),		//					Aggressive
				IMMORTAL = makeForm("immortal", ORGANIC, 0xd4af77),			//			Divine / Minium Stone
			ARCANE = makeForm("arcane", MATERIA, 0x8866aa),					//		Magical
				ALCHEMY = makeForm("alchemy", ARCANE, 0xff0044),			//			As Above, So Below
				ENCHANTING = makeForm("enchanting", ARCANE, 0xa152ff),		//			Vanilla Enchantments
				WITCHCRAFT = makeForm("witchcraft", ARCANE, 0x246658),		//			Vanilla Potions
			ETHEREAL = makeForm("ethereal", MATERIA, 0xa0c0c0),				//		Intangible
				MIND = makeForm("mind", ETHEREAL, 0x90c060),				//			XP-related
				SOUL = makeForm("soul", ETHEREAL, 0x8899ee);				//			Spirit Stuff
	
	private static RegistryObject<FormAspect> makeForm(String name, RegistryObject<FormAspect> parent, int color) {
		return FORMS.register(name, () -> new FormAspect(AASB.rl(name), parent == null ? null : parent.get(), color));
	}

	public static IForgeRegistry<FormAspect> getFormReg() {
		return FORMS_SUPPLIER.get();
	}
	public static boolean formExists(ResourceLocation rl) {
		return getFormReg().containsKey(rl);
	}
	@Nullable
	public static FormAspect getForm(ResourceLocation rl) {
		if (formExists(rl)) {
			return getFormReg().getValue(rl);
		}
		return null;
	}
	

	
	private static final DeferredRegister<Rune> RUNES = DeferredRegister.create(AASB.rl("runes"), AASB.MODID);
	public static final Supplier<IForgeRegistry<Rune>> RUNES_SUPPLIER = RUNES.makeRegistry(() -> {
		RegistryBuilder<Rune> builder = new RegistryBuilder<>();
		return builder;
	});
	
	public static final RegistryObject<Rune>
		RUNE_WATER = RUNES.register("water", () -> new WaterRune()),
		RUNE_EARTH = RUNES.register("earth", () -> new EarthRune()),
		RUNE_FIRE = RUNES.register("fire", () -> new FireRune()),
		RUNE_AIR = RUNES.register("air", () -> new AirRune()),

		RUNE_ARCANE = RUNES.register("arcane", () -> new ArcaneRune()),
		RUNE_ETHEREAL = RUNES.register("ethereal", () -> new EtherealRune()),
		RUNE_ORGANIC = RUNES.register("organic", () -> new OrganicRune()),
		RUNE_MINERAL = RUNES.register("mineral", () -> new MineralRune()),
		RUNE_TERRAIN = RUNES.register("terrain", () -> new TerrainRune()),

		RUNE_MATERIA = RUNES.register("materia", () -> new MateriaRune()),
		RUNE_QUINTESSENCE = RUNES.register("quintessence", () -> new QuintessenceRune())
		;

}
