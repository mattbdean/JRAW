package net.dean.jraw.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for testing purposes to signify that this method interacts with the JSON response in order to provide a usable
 * API in pure Java. The return value of this method must not be a primitive type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonInteraction {
	/**
	 * Whether the return value of this method can be {@code null}
	 * @return If this method can return null
	 */
	public boolean nullable() default false;
}
