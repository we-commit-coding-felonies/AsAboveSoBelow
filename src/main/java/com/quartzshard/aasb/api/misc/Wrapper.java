package com.quartzshard.aasb.api.misc;

import org.jetbrains.annotations.Nullable;

/**
 * Like a Tuple, but with only 1 thing instead of 2
 * @param <T>
 */
public class Wrapper<T> {
	@Nullable private T thing;

	public Wrapper() {}
	public Wrapper(T thing) {
		this.thing = thing;
	}
	
	@Nullable
	public T get() {
		return thing;
	}
	
	public void set(@Nullable T next) {
		thing = next;
	}
}
