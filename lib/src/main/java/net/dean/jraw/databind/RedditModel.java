package net.dean.jraw.databind;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RedditModel {
    boolean enveloped() default true;
}
