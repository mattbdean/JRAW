package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.Enveloped;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.databind.UnixTime;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;

@RedditModel
@AutoValue
public abstract class WikiPage implements Serializable {

    @Json(name = "may_revise") abstract boolean getMayRevise();

    /** True if there is an authenticated user and that user has the privileges to edit this wiki page */
    public final boolean mayRevise() {
        return getMayRevise();
    }

    /** The last time this page was edited, or null if never. */
    @Nullable
    @UnixTime
    @Json(name = "revision_date") public abstract Date getRevisionDate();

    /** The person that last revised this page, or null if never revised. */
    @Nullable
    @Enveloped
    @Json(name = "revision_by") public abstract Account getRevionBy();

    /** The Markdown-formatted body of the page */
    @Json(name = "content_md") public abstract String getContent();

    public static WikiPage create(boolean newMayRevise, Date newRevisionDate, Account newRevionBy, String newContent) {
        return new AutoValue_WikiPage(newMayRevise, newRevisionDate, newRevionBy, newContent);
    }

    public static JsonAdapter<WikiPage> jsonAdapter(Moshi moshi) {
        return new AutoValue_WikiPage.MoshiJsonAdapter(moshi);
    }
}
