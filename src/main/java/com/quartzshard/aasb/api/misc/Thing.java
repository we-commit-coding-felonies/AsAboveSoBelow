package com.quartzshard.aasb.api.misc;

import org.jetbrains.annotations.Nullable;

/**
 * A thing. Probably almost certainly very cursed. <br>
 * Can be used somewhat similarly to generics / dynamic typing (such as in P*thon or JavaScr*pt), but not exactly the same. <br>
 * Was originally intended for use in LabTE's stack pushing code, but is now dormant, waiting to ruin some other poor unsuspecting code.
 * @param <T> Type of the thing
 */
public class Thing<T> {
	public Thing(T thing) {
		this.thing = thing;
	}
	private final T thing;
	
	/**
	 * Attempts to cast the thing to the given class
	 * @param <Q> Desired type of the thing
	 * @param clazz The class to attempt to cast the thing to
	 * @return The casted thing, or null if the cast failed
	 */
	@Nullable
	public <Q> Q getAs(Class<Q> clazz) {
		try {
			return clazz.cast(thing);
		} catch (ClassCastException e) {}
		return null;
	}
	
	/**
	 * Checks if the thing is an instance of the given class
	 * @param <Q> Desired type of the thing
	 * @param clazz The class to check if the thing is
	 * @return If the thing instanceof the class
	 */
	public <Q> boolean is(Class<Q> clazz) {
		return clazz.isInstance(thing);
	}
}
