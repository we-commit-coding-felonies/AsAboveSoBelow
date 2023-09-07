package com.quartzshard.aasb.api.alchemy.aspects;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;
import com.quartzshard.aasb.api.capability.aspect.IAspectHandler.AspectType;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.util.ColorsHelper.Color;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public enum AspectShape implements IAlchemicalFlow<AspectShape> {
	UNIVERSAL("misc.aasb.aspect.shape.quintessence", Color.MID_PURPLE),
	WATER(Color.MID_BLUE),
	EARTH(Color.MID_GREEN),
	FIRE(Color.MID_RED),
	AIR(Color.MID_YELLOW);
	
	public final Color color;
	private final Component loc, fLoc;
	
	// duplicate code because cant call name() in this()
	private AspectShape(Color color) {
		this.color = color;
		String langKey = autoLangKey();
		loc = AASBLang.tc(langKey);
		fLoc = loc.copy().withStyle(Style.EMPTY.withColor(color.I));
	}
	private AspectShape(String langKey, Color color) {
		this.color = color;
		loc = AASBLang.tc(langKey);
		fLoc = loc.copy().withStyle(Style.EMPTY.withColor(color.I));
	}
	
	private String autoLangKey() {
		String langKey = "misc."+AsAboveSoBelow.MODID+".aspect.shape."+(this.name().toLowerCase());
		LogHelper.debug("AspectShape.autoLangKey()", "MadeKey", langKey);
		return langKey;
	}
	
	public MutableComponent loc() {
		return loc.copy();
	}
	public MutableComponent fLoc() {
		return fLoc.copy();
	}
	
	/**
	 * Checks if the caller flows into the arg. Order matters!
	 * @param to The Shape we're checking flow to.
	 * @return 
	 */
	@Override
	public boolean flows(AspectShape to) {
		switch (this) {
		case AIR:
			return to == WATER;
		case EARTH:
			return to == FIRE;
		case FIRE:
			return to == AIR;
		case WATER:
			return to == EARTH;
		case UNIVERSAL:
			return true;
		}
		LogHelper.error("AspectShape.flows()", "EscapedSwitch", "Somehow, the shape that called this wasn't a shape. Maybe it was null? Please send us logs if you see this!");
		return false;
	}
	
	/**
	 * Checks if the caller is perpendicular to the arg. Order matters!
	 * @param to The Shape we're checking against.
	 * @return
	 */
	@Override
	public boolean perpendicular(AspectShape to) {
		return this == to;
	}
	
	/**
	 * Checks if flow is violated when travelling from caller to arg. Order matters!
	 * @param to The Shape we're checking against.
	 * @return
	 */
	@Override
	public boolean violates(AspectShape to) {
		return !this.flows(to) && !this.perpendicular(to);
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	
	/**
	 * Returns null if deserialization fails
	 * @param dat
	 * @return 
	 */
	@Nullable
	public static AspectShape deserialize(String dat) {
		if (dat != null) {
			try {
				return AspectShape.valueOf(dat.toUpperCase());
			} catch (IllegalArgumentException e) {}
		}
		return null;
	}

	@Override
	public AspectType type() {
		return AspectType.SHAPE;
	}
} 
