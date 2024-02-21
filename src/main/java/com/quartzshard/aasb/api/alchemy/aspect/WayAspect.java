package com.quartzshard.aasb.api.alchemy.aspect;

import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Way is a simple numerical value. <br>
 * It's flow is similar to the concept of inertia, it flows to the same value. <br>
 * Way flow violation is impossible, as violations of Way flow can easily lead to duplication exploits.
 * <p>
 * <b><i><u>UNDER NO CIRCUMSTANCES SHOULD WAY FLOW EVER BE VIOLATED IN TRANSMUTATION, UNLESS YOU THOROUGHLY UNDERSTAND THE CONSEQUENCES!!!
 */
public class WayAspect implements IAspect<WayAspect> {
	public static final WayAspect ZERO = new WayAspect(0);
	
	private final long value;
	
	public WayAspect(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}

	@Override
	public boolean flowsTo(WayAspect other) {
		return other.getValue() == this.value;
	}

	@Override
	public boolean flowsFrom(@NotNull WayAspect other) {
		return other.flowsTo(this);
	}

	@Override
	public float violationTo(WayAspect other) {
		return flowsTo(other) ? 0 : 1;
		//long oVal = other.getValue();
		//if (oVal > value) {
		//	float bound = value * 2f;
		//	return ((float)oVal - (float)value) / (bound - value);
		//} else if (oVal < value) {
		//	float bound = value / 2f;
		//	return ((float)value - (float)oVal) / (value - bound);
		//}
		//return flowsTo(other) ? 0 : 1;
		
	}

	@Override
	public float violationFrom(WayAspect other) {
		return other.violationTo(this);
	}
	
	@Override
	public @NotNull String toString() {
		return "Way."+value;
	}

	@Override
	public String serialize() {
		return toString();
	}
	
	/**
	 * Deserializes a WayAspect from a string <br>
	 * Expected format is "Way.11", returns null if it fails
	 * @param dat
	 * @return 
	 */
	@Nullable
	public static WayAspect deserialize(String dat) {
		if (dat != "" && dat.startsWith("Way.")) {
			try {
				return new WayAspect(Long.parseLong(dat.replace("Way.", "")));
			} catch (NumberFormatException e) {}
		}
		return null;
	}
	
	public static WayAspect fromSeed(long seed) {
		return new WayAspect(new Random(seed).nextLong(1048576, 2097152));
	}
}
