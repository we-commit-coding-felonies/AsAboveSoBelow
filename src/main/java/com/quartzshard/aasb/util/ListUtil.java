package com.quartzshard.aasb.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ListUtil {

	/**
	 * Removes every null from the given list
	 * @param list The list to remove nulls from
	 * @param <T> Type of the list
	 */
	public static <T> void purgeNulls(List<T> list) {
		list.removeIf(Objects::isNull);
	}
}
