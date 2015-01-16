package net.dean.jraw.models.meta;

import net.dean.jraw.models.Account;
import net.dean.jraw.models.More;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to verify that the correct data is being mapped to the correct object. This is accomplished
 * by looking at the JSON's {@code kind} node. For each root node of a JSON model, there are two nodes: "data" and
 * "kind". The data node stores properties related to the actual content of the model, while the kind node establishes
 * the model's type. For example, an Account model would look like this:
 *
 * <pre>
 * {@code
 * {
 *     "kind": "t2",
 *     "data": {
 *          "name": "thatJavaNerd",
 *          "id": "gia0t",
 *          <...>
 *      }
 * }
 * }
 * </pre>
 *
 * Since {@link Account} is annotated with this annotation and its {@code kind()} is {@link Model.Kind#ACCOUNT} ("t2"),
 * this JsonNode is allowed to be paired with an Account instance.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Model {
    /** The expected value of the "kind" JSON node */
    public Kind kind();

    /** The class used to serialize instances of this model */
    public Class<? extends JsonSerializer> serializer() default DefaultJsonSerializer.class;

    /** Whether or not to validate JsonNodes that are attempting to bind themselves to model */
    public boolean validate() default true;

    /**
     * A list of possible values of "kind" nodes from the Reddit API
     */
    public static enum Kind {
        ABSTRACT("__ABSTRACT__"),
        NONE("__NONE__"),

        /** Represents a comment with the prefix "t1" */
        COMMENT("t1"),
        /** Represents an account with the prefix "t2" */
        ACCOUNT("t2"),
        /** Represents a submission with the prefix "t3" */
        LINK("t3"),
        /** Represents a message with the prefix "t4" */
        MESSAGE("t4"),
        /** Represents a subreddit with the prefix "t5" */
        SUBREDDIT("t5"),
        /** Represents an award with the prefix "t6" */
        AWARD("t6"),
        /** Represents a listing */
        LISTING("Listing"),
        /** Represents a "more" object. See {@link More} */
        MORE("more"),
        /** Represents a MultiReddit */
        MULTIREDDIT("LabeledMulti"),
        /** Represents a wiki page */
        WIKI_PAGE("wikipage"),
        /** Represents a wiki page's settings */
        WIKI_PAGE_SETTINGS("wikipagesettings"),
        /** Represents a live thread */
        LIVE_THREAD("LiveUpdateEvent"),
        /** Represents an update in a live thread */
        LIVE_UPDATE("LiveUpdate"),
        /** Represents a breakdown of karma by subreddit */
        KARMA_BREAKDOWN("KarmaList"),
        /** Represents an administrative action on behalf of a moderator of a subreddit */
        MOD_ACTION("modaction");

        private final String value;
        private Kind(String value) {
            this.value = value;
        }

        public String getValue() { return value; }

        /**
         * Gets a Kind by its JSON value (t1, t2, etc.)
         */
        public static Kind getByValue(String value) {
            for (Kind k : values()) {
                if (k.value.equals(value)) {
                    return k;
                }
            }

            return null;
        }
    }
}
