package net.dean.jraw.models;

import com.google.auto.value.AutoValue;

import java.io.Serializable;

@AutoValue
public abstract class Emoji implements Serializable {
    public abstract String getUrl();

    /** The fullname of the account that created this emoji */
    public abstract String getCreatedBy();

    public abstract String getName();

    /** The fullname of the subreddit to which this emoji belongs or "snoomojis" if available site-wide */
    public abstract String getNamespace();

    public static Emoji create(String newUrl, String newCreatedBy, String newName, String newOrigin) {
        return new AutoValue_Emoji(newUrl, newCreatedBy, newName, newOrigin);
    }
}
