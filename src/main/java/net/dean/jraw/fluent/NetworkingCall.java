package net.dean.jraw.fluent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to note that a method will be the direct cause a network request being made. This annotation
 * is used exclusively within the {@code net.dean.jraw.fluent} package.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NetworkingCall {
}
