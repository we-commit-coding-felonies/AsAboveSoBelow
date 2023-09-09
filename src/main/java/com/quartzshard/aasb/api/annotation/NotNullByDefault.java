package com.quartzshard.aasb.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;

import javax.annotation.meta.TypeQualifierDefault;

import org.jetbrains.annotations.NotNull;

/**
 * This exists because Eclipse is a very stinky and smelly IDE <br>
 * Loosely based off of their @NonNullByDefault, but implemented a bit differently (and also rather poorly, if I had to guess)
 * <p>
 * Putting this on something will quote: "[have] the effect that type references,
 * which are contained in the declaration, and for which a null annotation is otherwise lacking,
 * should be considered as [{@link NotNull @NotNull}]." <br>
 * Changed from their NonNull to jetbrains @NotNull because standardization is really difficult guys, I promise!
 */
@TypeQualifierDefault({PACKAGE, TYPE, METHOD, CONSTRUCTOR, FIELD, LOCAL_VARIABLE})
@Retention(RUNTIME)
public @interface NotNullByDefault {
	
}
