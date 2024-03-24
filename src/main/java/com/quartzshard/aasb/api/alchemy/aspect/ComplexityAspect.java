package com.quartzshard.aasb.api.alchemy.aspect;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.util.Colors;
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
	SIMPLE(0xffffff), // Anything that is mapped properly and is able to be used as input and output for transmutation with no issues
	COMPLEX(0xeeffee), // For things that were mapped, but aspect resolution could not flow, so it cannot be an output of transmutation
	NULLED(0x8e9e99, true), // Anything that *explicitly* has null as at least one of it's aspects. Cannot be used in transmutation circles at all
	UNKNOWN(0xffffff, true), // Things that were not mapped (either not found or mapping failed), and cannot be used for any alchemical processes
	
	SEEDGEN(0xeeffee), // Marks aspects as ones generated from some seed value. Generally treated like UNKNOWN.

	PHIL(Colors.PHILOSOPHERS.I), // Marks aspects as those of Phil. Any transmutation with this as its output will do Impossible Object crafting instead. Otherwise, acts like UNKNOWN
	IMPOSSIBLE(Colors.PHILOSOPHERS.I), // Marks aspects as those of an Impossible Object. Anything with this is considered a valid output for Impossible Object crafting
	;

	private final ResourceLocation symbol;
	private final int color;
	private final boolean allowsNull;

	ComplexityAspect(int color) {
		this(color, false);
	}
	ComplexityAspect(int color, boolean allowsNull) {
		symbol = AASB.rl("textures/symbol/aspect/complexity/"+this.name().toLowerCase()+".png");
		this.color = color;
		this.allowsNull = allowsNull;
	}

	@Override
	public boolean flowsTo(@Nullable ComplexityAspect other) {
		if (other == null) return false;
		return switch (this) {
			case SIMPLE, COMPLEX -> other == SIMPLE;
			default -> false;
		};
	}

	@Override
	public boolean flowsFrom(@Nullable ComplexityAspect other) {
		return other != null && other.flowsTo(this);
	}

	@Override
	public float violationTo(@Nullable ComplexityAspect other) {
		return this.flowsTo(other) ? 0 : Float.POSITIVE_INFINITY;
	}

	@Override
	public float violationFrom(@Nullable ComplexityAspect other) {
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

	@Override
	public int getColor() {
		return color;
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

	public boolean allowsNull() {
		return allowsNull;
	}

}
