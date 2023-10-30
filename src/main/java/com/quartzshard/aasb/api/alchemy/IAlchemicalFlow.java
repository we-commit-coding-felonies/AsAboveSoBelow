package com.quartzshard.aasb.api.alchemy;

import com.quartzshard.aasb.api.capability.aspect.IAspectHandler.AspectType;

public interface IAlchemicalFlow<T> {
	/**
	 * Checks if the caller flows into the arg. Order matters!
	 * @param to The Shape we're checking flow to.
	 * @return if this follows flow
	 */
	boolean flows(T to);
	
	/**
	 * Checks if the caller is perpendicular to the arg. Order matters!
	 * @param to The Shape we're checking against.
	 * @return if this is considered "perpendicular" (border between flowing & not flowing)
	 */
	boolean perpendicular(T to);
	
	/**
	 * Checks if flow is violated when travelling from caller to arg. Order matters!
	 * @param to The Shape we're checking against.
	 * @return if this violates flow
	 */
	boolean violates(T to);
	
	/**
	 * Returns a string representation of this aspect, used when saving to NBT
	 * @return
	 */
	default String serialize() {
		return toString();
	}
	
	AspectType type();
}
