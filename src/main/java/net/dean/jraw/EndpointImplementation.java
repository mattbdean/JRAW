package net.dean.jraw;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * This annotation shows that this method is ultimately responsible for the implementation of a Reddit API endpoint
 * (such as {@link Endpoints#FLAIRSELECTOR /api/flairselector}). Note that methods that have this annotation
 * <em>will</em> send at least one HTTP request.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndpointImplementation {
    /**
     * An array of endpoint URIs that this method implements (such as /api/flairselector)
     *
     * @return A list of endpoints
     */
    Endpoints[] value();
}
