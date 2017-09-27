package net.dean.jraw.databind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation's presence on a class symbolizes that the JSON is enveloped. See {@link RedditModelAdapterFactory}
 * for more.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RedditModel {
    /**
     * Most models are wrapped in an envelope that provides us with some basic type information. However, there are some
     * exceptions (namely {@link net.dean.jraw.models.WikiRevision}.
     */
    boolean enveloped() default true;
}
