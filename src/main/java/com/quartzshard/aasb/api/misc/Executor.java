package com.quartzshard.aasb.api.misc;

import org.jetbrains.annotations.NotNull;

/**
 * Something that has no inputs, nor any outputs. It just does something.
 */
@FunctionalInterface
public interface Executor {
	void execute();

	/**
	 * Merges 2 Executors, running this one first, then the other.
	 * @param other the Performer to be executed after this one
	 * @return Combined Executor
	 */
	default @NotNull Executor also(Executor other) {
		return () -> {
			this.execute();
			other.execute();
		};
	}
}