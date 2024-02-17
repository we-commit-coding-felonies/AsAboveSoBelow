package com.quartzshard.aasb.api.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains functional interfaces with combinations of @Nullable and @NotNull for their types <br>
 * Forge already has its own {@link NonNullFunction}, so that combination has been omitted 
 * 
 * @see NonNullFunction
 * @see Function
 */
public class SemiNullableFunctions {
//	/**
//	 * Like Function, but input type is @NotNull
//	 * @param <I> @NotNull Input Parameter type
//	 * @param <R> Return type
//	 */
//	@FunctionalInterface
//	public interface NotNullParameterFunction<I, R> {
//		R apply(@NotNull I i);
//	}
//
//	/**
//	 * Like Function, but return type is @NotNull
//	 * @param <I> Input type
//	 * @param <R> @NotNull Return type
//	 */
//	@FunctionalInterface
//	public interface NotNullReturnFunction<I, R> {
//		@NotNull
//		R apply(I i);
//	}
//
//	/**
//	 * Like Function, but input type is @Nullable
//	 * @param <I> @Nullable Input type
//	 * @param <R> Return type
//	 */
//	@FunctionalInterface
//	public interface NullableParameterFunction<I, R> {
//		R apply(@Nullable I i);
//	}
//
//	/**
//	 * Like Function, but return type is @Nullable
//	 * @param <I> Input type
//	 * @param <R> @Nullable Return type
//	 */
//	@FunctionalInterface
//	public interface NullableReturnFunction<I, R> {
//		@Nullable
//		R apply(I i);
//	}

	/**
	 * Like Function, but input type is @Nullable, and return type is @NotNull
	 * @param <I> @Nullable Input type
	 * @param <R> @NotNull Return type
	 */
	@FunctionalInterface
	public interface MixedNullableParameterFunction<I, R> {
		@NotNull
		R apply(@Nullable I i);
	}

	/**
	 * Like Function, but input type is @NotNull, and return type is @Nullable
	 * @param <I> @NotNull Input type
	 * @param <R> @Nullable Return type
	 */
	@FunctionalInterface
	public interface MixedNullableReturnFunction<I, R> {
		@Nullable
		R apply(@NotNull I i);
	}

	/**
	 * Like Function, but both input type and return type are @Nullable
	 * @param <I> @NotNull Input type
	 * @param <R> @Nullable Return type
	 */
	@FunctionalInterface
	public interface NullableFunction<I, R> {
		@Nullable
		R apply(@Nullable I i);
	}
}
