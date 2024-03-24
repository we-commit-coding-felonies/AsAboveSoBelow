package com.quartzshard.aasb.api.alchemy.aspect;

import java.util.Random;

import com.quartzshard.aasb.AASB;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Way is a simple numerical value. <br>
 * It's flow is similar to the concept of inertia, it flows to the same value. <br>
 * Way flow violation is impossible, as violations of Way flow can easily lead to duplication exploits.
 * <p>
 * <b><i><u>UNDER NO CIRCUMSTANCES SHOULD WAY FLOW EVER BE VIOLATED IN TRANSMUTATION, UNLESS YOU THOROUGHLY UNDERSTAND THE CONSEQUENCES!!!
 */
public record WayAspect(long value) implements IAspect<WayAspect> {
	public static final WayAspect ZERO = new WayAspect(0);
	public static final ResourceLocation SYMBOL = AASB.rl("textures/symbol/aspect/way/way.png");
	public static final ResourceLocation SYMBOL_1K = AASB.rl("textures/symbol/aspect/way/kiloway.png");


	@Override
	public boolean flowsTo(@Nullable WayAspect other) {
		return other != null && other.value() == this.value;
	}

	@Override
	public boolean flowsFrom(@Nullable WayAspect other) {
		return flowsTo(other); // this is fine because way flow is symmetrical
	}

	@Override
	public float violationTo(@Nullable WayAspect other) {
		return flowsTo(other) ? 0 : Float.POSITIVE_INFINITY;

	}

	@Override
	public float violationFrom(@Nullable WayAspect other) {
		return violationTo(other); // this is fine because way flow is symmetrical
	}

	@Override
	public @NotNull String toString() {
		return "Way." + value;
	}

	@Override
	public String serialize() {
		return toString();
	}

	@Override
	public ResourceLocation symbolTexture() {
		return value() < 1000 ? SYMBOL : SYMBOL_1K;
	}

	@Override
	public int getColor() {
		return 0xffff99;
	}

	/**
	 * Deserializes a WayAspect from a string <br>
	 * Expected format is "Way.11", returns null if it fails
	 *
	 * @param dat
	 * @return
	 */
	@Nullable
	public static WayAspect deserialize(String dat) {
		if (dat != "" && dat.startsWith("Way.")) {
			try {
				return new WayAspect(Long.parseLong(dat.replace("Way.", "")));
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	public static WayAspect fromSeed(long seed) {
		return new WayAspect(new Random(seed).nextLong(1048576, 2097152));
	}
}
