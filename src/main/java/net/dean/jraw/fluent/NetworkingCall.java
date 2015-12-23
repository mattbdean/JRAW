package net.dean.jraw.fluent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to show that a method will be the direct cause a network request being made. This annotation
 * is used exclusively within the {@code net.dean.jraw.fluent} package. The placement of this annotation on a method
 * also signifies that that method can throw a {@link net.dean.jraw.http.NetworkException}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NetworkingCall {}
