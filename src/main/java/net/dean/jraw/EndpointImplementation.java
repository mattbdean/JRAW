package net.dean.jraw;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * This annotation shows that this method is ultimately responsible for the implementation of a Reddit API endpoint
 * (such as {@code /api/login})
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndpointImplementation {
    /**
     * An array of endpoint URIs that this method implements (such as /api/login)
     *
     * @return A list of endpoints
     */
    public Endpoints[] value();
}
