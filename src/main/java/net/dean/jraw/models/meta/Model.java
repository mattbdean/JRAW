package net.dean.jraw.models.meta;

import net.dean.jraw.NoSuchEnumConstantException;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.Award;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.JsonModel;
import net.dean.jraw.models.KarmaBreakdown;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LiveThread;
import net.dean.jraw.models.LiveUpdate;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.ModAction;
import net.dean.jraw.models.MoreChildren;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.WikiPage;
import net.dean.jraw.models.WikiPageSettings;
import com.fasterxml.jackson.databind.JsonNode;

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
    /**
     * The expected value of the "kind" JSON node. If the value is {@link Kind#ABSTRACT} or {@link Kind#NONE}, the
     * class annotated with this model will not be validated using {@link ModelManager#validate(JsonNode, Class)}.
     */
    public Kind kind();

    /** The class used to serialize instances of this model */
    public Class<? extends JsonSerializer> serializer() default DefaultJsonSerializer.class;

    /** Whether or not to validate JsonNodes that are attempting to bind themselves to model */
    public boolean validate() default true;

    /**
     * A list of possible values of "kind" nodes from the Reddit API
     */
    public static enum Kind {
        /** Represents an abstract type */
        ABSTRACT("__ABSTRACT__", null),
        NONE("__NONE__", null),

        /** Represents a comment with the prefix "t1" */
        COMMENT("t1", Comment.class),
        /** Represents an account with the prefix "t2" */
        ACCOUNT("t2", Account.class),
        /** Represents a submission with the prefix "t3" */
        LINK("t3", Submission.class),
        /** Represents a message with the prefix "t4" */
        MESSAGE("t4", Message.class),
        /** Represents a subreddit with the prefix "t5" */
        SUBREDDIT("t5", Subreddit.class),
        /** Represents an award with the prefix "t6" */
        AWARD("t6", Award.class),
        /** Represents a listing */
        LISTING("Listing", Listing.class),
        /** Represents a "more" object. See {@link MoreChildren} */
        MORE("more", MoreChildren.class),
        /** Represents a MultiReddit */
        MULTIREDDIT("LabeledMulti", MultiReddit.class),
        /** Represents a wiki page */
        WIKI_PAGE("wikipage", WikiPage.class),
        /** Represents a wiki page's settings */
        WIKI_PAGE_SETTINGS("wikipagesettings", WikiPageSettings.class),
        /** Represents a live thread */
        LIVE_THREAD("LiveUpdateEvent", LiveThread.class),
        /** Represents an update in a live thread */
        LIVE_UPDATE("LiveUpdate", LiveUpdate.class),
        /** Represents a breakdown of karma by subreddit */
        KARMA_BREAKDOWN("KarmaList", KarmaBreakdown.class),
        /** Represents an administrative action on behalf of a moderator of a subreddit */
        MOD_ACTION("modaction", ModAction.class);

        private final String value;
        private final Class<? extends JsonModel> defaultClass;
        private Kind(String value, Class<? extends JsonModel> defaultClass) {
            this.value = value;
            this.defaultClass = defaultClass;
        }

        public String getValue() { return value; }

        /** Gets the class most likely to use this Kind */
        public Class<? extends JsonModel> getDefaultClass() { return defaultClass; }

        /**
         * Gets a Kind by its JSON value (t1, t2, etc.)
         */
        public static Kind getByValue(String value) {
            for (Kind k : values()) {
                if (k.value.equals(value)) {
                    return k;
                }
            }

            throw new NoSuchEnumConstantException(Kind.class, value);
        }
    }
}
