package net.dean.jraw.models.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Used for testing purposes to signify that a method retrieves data from a JSON node. For a given JsonModel, each
 * method annotated with this annotation will be invoked via reflection. If the method throws an exception of any kind
 * or the return value was null and {@link #nullable()} returns false, then the test will fail. Methods annotated with
 * this class must take zero parameters.
 *
 * <p>Note that this class is different from Jackson's JsonProperty annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface JsonProperty {
    /**
     * Whether the return value of this method is allowed to be null
     * @return If this method is allowed to return null
     */
    public boolean nullable() default false;
}
