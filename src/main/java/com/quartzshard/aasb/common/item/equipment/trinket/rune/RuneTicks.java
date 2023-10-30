package com.quartzshard.aasb.common.item.equipment.trinket.rune;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * put on runes that should have their combat or utility abilities ticked <br>
 * passive abilities are always ticked, so this is unnecessary for those
 */
@Target(ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RuneTicks {
	boolean combat() default false;
	boolean utility() default false;
}
