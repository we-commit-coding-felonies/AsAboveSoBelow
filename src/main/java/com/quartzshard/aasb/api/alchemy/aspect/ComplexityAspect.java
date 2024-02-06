package com.quartzshard.aasb.api.alchemy.aspect;

import org.jetbrains.annotations.Nullable;

/**
 * Very different from the other aspects, complexity describes
 * the state of an items AlchData and how it behaves in transmutation <br>
 * Flow behaves very strangely with complex items, flowing from SIMPLE -> SIMPLE <br>
 * Violation is absolute. 
 */
public enum ComplexityAspect implements IAspect<ComplexityAspect> {
	SIMPLE, // Anything that is mapped properly and is able to be used as input and output for transmutation with no issues
	COMPLEX, // For things that were mapped, but aspect resolution could not flow, so it cannot be an output of transmutation
	NULLED, // Anything that *explicitly* has null as at least one of it's aspects. Cannot be used in transmutation circles at all
	UNKNOWN, // Things that were not mapped (either not found or mapping failed), and cannot be used for any alchemical processes
	;

	@Override
	public boolean flowsTo(ComplexityAspect other) {
		return this == other && other == ComplexityAspect.SIMPLE;
	}

	@Override
	public boolean flowsFrom(ComplexityAspect other) {
		return other.flowsTo(this);
	}

	@Override
	public float violationTo(ComplexityAspect other) {
		return this.flowsTo(other) ? 0 : 1;
	}

	@Override
	public float violationFrom(ComplexityAspect other) {
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
	
	/**
	 * Deserializes a ShapeAspect from a String <br>
	 * Expected format is "Shape.earth", returns null if it fails
	 * @param dat
	 * @return 
	 */
	@Nullable
	public static ComplexityAspect deserialize(String dat) {
		if (dat != "" && dat.startsWith("Complexity.")) {
			try {
				return ComplexityAspect.valueOf(dat.replace("Complexity.", "").toUpperCase());
			} catch (IllegalArgumentException e) {}
		}
		return null;
	}

}
