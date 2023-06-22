package com.quartzshard.aasb.api.alchemy;

import com.quartzshard.aasb.util.LogHelper;

public enum AspectShape implements IAlchemicalFlow<AspectShape>{
	UNIVERSAL,
	WATER,
	EARTH,
	FIRE,
	AIR,
	ASPECT_NULL;
	
	/**
	 * Checks if the caller flows into the arg. Order matters!
	 * @param to The Shape we're checking flow to.
	 * @return 
	 */
	public boolean flows(AspectShape to) {
		if (to == ASPECT_NULL) return false;
		switch(this) {
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
		case ASPECT_NULL:
			return false;
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
		if (this == ASPECT_NULL || to == ASPECT_NULL) return false;
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



} 
