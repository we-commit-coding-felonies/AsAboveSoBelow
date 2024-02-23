package com.quartzshard.aasb.api.alchemy.aspect;

import net.minecraft.resources.ResourceLocation;

/**
 * Shared stuff between all 3 different aspect types
 */
public interface IAspect<A extends IAspect<A>> {
	/**
	 * Checks if flow between aspects is perfect (this -> other)
	 * @param other The aspect we want to check flow towards
	 * @return If THIS aspect flows into OTHER aspect
	 */
	boolean flowsTo(A other);
	
	/**
	 * Checks if flow between aspects is perfect (other -> this)
	 * @param other The aspect we want to check flow from
	 * @return If OTHER aspect flows into THIS aspect
	 */
	boolean flowsFrom(A other);
	
	/**
	 * Gets how "violating" the change (this -> other) is, expressed as a percentage between 0 and 1. <br>
	 * Always returns 0 if the `flowsTo(other)` would return true
	 * @param other The aspect we want to check violation towards
	 * @return How violating the described change is, or 0 if `flowsTo(other)` would return true
	 */
	float violationTo(A other);
	
	/**
	 * Gets how "violating" the change (other -> this) is, expressed as a percentage between 0 and 1. <br>
	 * Always returns 0 if the `flowsFrom(other)` would return true
	 * @param other The aspect we want to check violation from
	 * @return How violating the described change is, or 0 if `flowsFrom(other)` would return true
	 */
	float violationFrom(A other);
	
	/**
	 * Serializes this IAspect to a String
	 * @return String representing this IAspect
	 */
	String serialize();

	/**
	 * Gets the ResourceLocation for this aspects symbol texture
	 * @return
	 */
	ResourceLocation symbolTexture();
}
