package net.dean.jraw;

import java.lang.annotation.*;

/**
 * This annotation shows that this method is ultimately responsible for the implementation of a Reddit API endpoint (such
 * as /api/login)
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
