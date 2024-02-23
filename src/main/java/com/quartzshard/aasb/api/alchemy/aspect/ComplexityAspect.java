package com.quartzshard.aasb.api.alchemy.aspect;

import com.quartzshard.aasb.AASB;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Very different from the other aspects, complexity describes
 * the state of an items AlchData and how it behaves in transmutation <br>
 * Flow behaves very strangely with complex items, flowing from SIMPLE -> SIMPLE or COMPLEX -> SIMPLE <br>
 * Violation is absolute. Everything that doesnt flow violates 100%
 */
public enum ComplexityAspect implements IAspect<ComplexityAspect> {
	SIMPLE, // Anything that is mapped properly and is able to be used as input and output for transmutation with no issues
	COMPLEX, // For things that were mapped, but aspect resolution could not flow, so it cannot be an output of transmutation
	NULLED, // Anything that *explicitly* has null as at least one of it's aspects. Cannot be used in transmutation circles at all
	UNKNOWN, // Things that were not mapped (either not found or mapping failed), and cannot be used for any alchemical processes
	
	SEEDGEN, // Marks aspects as ones generated from some seed value. Generally treated like UNKNOWN.

	PHIL, // Marks aspects as those of Phil. Any transmutation with this as its output will do Impossible Object crafting instead. Otherwise, acts like UNKNOWN
	IMPOSSIBLE, // Marks aspects as those of an Impossible Object. Anything with this is considered a valid output for Impossible Object crafting
	;

	private final ResourceLocation symbol;

	ComplexityAspect() {
		symbol = AASB.rl("symbol/aspect/complexity/"+this.name().toLowerCase());
	}

	@Override
	public boolean flowsTo(ComplexityAspect other) {
		switch (this) {
			case SIMPLE:
			case COMPLEX:
				return other == SIMPLE;
			
			default:
				return false;
		}
	}

	@Override
	public boolean flowsFrom(@NotNull ComplexityAspect other) {
		return other.flowsTo(this);
	}

	@Override
	public float violationTo(ComplexityAspect other) {
		return this.flowsTo(other) ? 0 : 1;
	}

	@Override
	public float violationFrom(@NotNull ComplexityAspect other) {
		return other.violationTo(this);
	}
	
	@Override
	public String toString() {
		return "Complexity."+this.name().toLowerCase();
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
	public static ComplexityAspect deserialize(@NotNull String dat) {
		if (dat != "" && dat.startsWith("Complexity.")) {
			try {
				return ComplexityAspect.valueOf(dat.replace("Complexity.", "").toUpperCase());
			} catch (IllegalArgumentException e) {}
		}
		return null;
	}

}
