package com.quartzshard.aasb.api.alchemy.aspect;

import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.util.Colors;
import com.quartzshard.aasb.util.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/**
 * Shape is an enum with 5 possible values <br>
 * It's flow works in a cycle, Water -> Earth -> Fire -> Air -> Water... <br>
 * Flow violation is 25% to the same, 50% going against the cycle, 100% skipping across the cycle <br>
 * There is also Quintessence, the "universal shape". It flows to any of the basic 4, and everything violates 100% towards it (including itself)
 */
public enum ShapeAspect implements IAspect<ShapeAspect> {
	QUINTESSENCE(Colors.MID_PURPLE.I),
	WATER(Colors.MID_BLUE.I),
	EARTH(Colors.MID_GREEN.I),
	FIRE(Colors.MID_RED.I),
	AIR(Colors.MID_YELLOW.I);
	
	public final int color;
	public final String lang;
	private final @NotNull Component loc, fLoc;
	private final ResourceLocation symbol;

	// duplicate code because cant call name() from within constructor
	ShapeAspect(int color) {
		this.color = color;
		String lang = autoLangKey();
		this.lang = lang;
		loc = LangData.tc(lang);
		fLoc = loc.copy().withStyle(Style.EMPTY.withColor(color));
		symbol = AASB.rl("symbol/aspect/shape/"+this.name().toLowerCase());
	}
	ShapeAspect(String lang, int color) {
		this.color = color;
		this.lang = lang;
		loc = LangData.tc(lang);
		fLoc = loc.copy().withStyle(Style.EMPTY.withColor(color));
		symbol = AASB.rl("symbol/aspect/shape/"+this.name().toLowerCase());
	}
	
	private String autoLangKey() {
		String langKey = "alchemy."+AASB.MODID+".aspect.shape."+(this.name().toLowerCase());
		Logger.debug("AspectShape.autoLangKey()", "MadeKey", langKey);
		return langKey;
	}
	
	public MutableComponent loc() {
		return loc.copy();
	}
	public @NotNull MutableComponent fLoc() {
		return fLoc.copy();
	}
	
	@Override
	public boolean flowsTo(ShapeAspect other) {
		switch (this) {
			case QUINTESSENCE:
				return other != QUINTESSENCE;
			case WATER:
				return other == EARTH;
			case EARTH:
				return other == FIRE;
			case FIRE:
				return other == AIR;
			case AIR:
				return other == WATER;
			default:
				return false;
		}
	}

	@Override
	public boolean flowsFrom(ShapeAspect other) {
		return other.flowsTo(this);
	}

	@Override
	public float violationTo(ShapeAspect other) {
		if (!this.flowsTo(other)) {
			if (this == other) return 0.25f;
			switch (this) {
				case WATER:
					return other == AIR ? 0.5f : 1;
				case EARTH:
					return other == WATER ? 0.5f : 1;
				case FIRE:
					return other == EARTH ? 0.5f : 1;
				case AIR:
					return other == FIRE ? 0.5f : 1;
				default:
					return 1; // this only runs if this == other == QUINTESSENCE
			}
		}
		return 0;
	}

	@Override
	public float violationFrom(@NotNull ShapeAspect other) {
		return other.violationTo(this);
	}
	
	@Override
	public @NotNull String toString() {
		return "Shape."+this.name().toLowerCase();
	}

	@Override
	public String serialize() {
		return toString();
	}

	@Override
	public ResourceLocation symbolTexture() {
		return symbol;
	}

	/**
	 * Deserializes a ShapeAspect from a String <br>
	 * Expected format is "Shape.earth", returns null if it fails
	 * @param dat
	 * @return 
	 */
	@Nullable
	public static ShapeAspect deserialize(String dat) {
		if (dat != "" && dat.startsWith("Shape.")) {
			try {
				return ShapeAspect.valueOf(dat.replace("Shape.", "").toUpperCase());
			} catch (IllegalArgumentException e) {}
		}
		return null;
	}
	
	public static ShapeAspect fromSeed(long seed) {
		return ShapeAspect.values()[new Random(seed).nextInt(ShapeAspect.values().length)];
	}
}
