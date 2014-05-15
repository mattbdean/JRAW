package net.dean.jraw.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation must be applied to a field where the value is dynamically assigned based on the return value of a
 * RestResponse's data. It is advised that the field be a primitive type, as primitives cannot be equal to null, which
 * the API sometimes returns.
 *
 * @author Matthew Dean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonAttribute {
	/**
	 * The JSON key of this attribute
	 * @return The JSON key of this attribute
	 */
	String jsonName();
}
