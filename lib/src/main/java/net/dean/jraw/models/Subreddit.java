package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.RedditClient;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.references.Referenceable;
import net.dean.jraw.references.SubredditReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@AutoValue
@RedditModel(kind = KindConstants.SUBREDDIT)
public abstract class Subreddit implements Created, Identifiable, Referenceable<SubredditReference> {
    /**
     * How many accounts are active on this subreddit at one time. If {@link #isAccountsActiveFuzzed()}, this will not
     * be exact.
     */
    @Nullable
    @Json(name = "accounts_active") public abstract Integer getAccountsActive();

    /** If reddit is hiding the true number of active accounts and giving a value that is "close enough" instead. */
    @Json(name = "accounts_active_fuzzed") public abstract boolean isAccountsActiveFuzzed();

    /** How many minutes reddit hides new comments for */
    @Json(name = "comment_score_hide_mins") public abstract int getCommentScoreHideMins();

    @NotNull
    @Override
    @Json(name = "created_utc") public abstract Date getCreated();

    @NotNull
    @Override
    @Json(name = "name") public abstract String getFullName();

    @Json(name = "key_color") public abstract String getKeyColor();

    /** Name without the "/r/" prefix: "pics", "funny", etc. */
    @Json(name = "display_name") public abstract String getName();

    @Json(name = "over_18") public abstract boolean isNsfw();

    /**
     * If this subreddit has been quarantined. See
     * <a href="https://reddit.zendesk.com/hc/en-us/articles/205701245-Quarantined-Subreddits">here</a> for more.
     */
    @Json(name = "quarantine") public abstract boolean isQuarantined();

    /** Sidebar content in raw Markdown */
    @Json(name = "description") public abstract String getSidebar();

    /** Whether the subreddit supports Markdown spoilers */
    @Json(name = "spoilers_enabled") public abstract boolean isSpoilersEnabled();

    /** What type of submissions can be submitted to this subreddit */
    @Json(name = "submission_type") public abstract SubmissionType getSubmissionType();

    /** The text on the button that users click to submit a link */
    @Nullable
    @Json(name = "submit_link_label") public abstract String getSubmitLinkLabel();

    /** The text on the button that users click to submit a self post */
    @Nullable
    @Json(name = "submit_text_label") public abstract String getSubmitTextLabel();

    /** The amount of subscribers this subreddit has */
    public abstract int getSubscribers();

    /** The suggested default way to sort comments in this subreddit */
    @Nullable
    @Json(name = "suggested_comment_sort") public abstract CommentSort getSuggestedCommentSort();

    /** The title of the tab when visiting this subreddit on a web browser */
    public abstract String getTitle();

    /** The URL to access this subreddit relative to reddit.com. For example, "/r/pics" */
    public abstract String getUrl();

    @Nullable
    @Json(name = "user_is_muted") abstract Boolean getUserIsMuted();
    @Nullable
    @Json(name = "user_is_banned") abstract Boolean getUserIsBanned();
    @Nullable
    @Json(name = "user_is_contributor") abstract Boolean getUserIsContributor();
    @Nullable
    @Json(name = "user_is_moderator") abstract Boolean getUserIsModerator();
    @Nullable
    @Json(name = "user_is_subscriber") abstract Boolean getUserIsSubscriber();

    // See https://github.com/google/auto/issues/275 on why we need thi
    public final boolean isUserMuted() { return getUserIsMuted() != null && getUserIsMuted(); }
    public final boolean isUserBanned() { return getUserIsBanned() != null && getUserIsBanned(); }
    public final boolean isUserContributor() { return getUserIsContributor() != null && getUserIsContributor(); }
    public final boolean isUserModerator() { return getUserIsModerator() != null && getUserIsModerator(); }
    public final boolean isUserSubscriber() { return getUserIsSubscriber() != null && getUserIsSubscriber(); }

    @NotNull
    @Override
    public SubredditReference toReference(@NotNull RedditClient reddit) {
        return reddit.subreddit(getName());
    }

    public static JsonAdapter<Subreddit> jsonAdapter(Moshi moshi) {
        return new AutoValue_Subreddit.MoshiJsonAdapter(moshi);
    }

    /** An enumeration of how a subreddit can restrict the type of submissions that can be posted  */
    public enum SubmissionType {
        /** Links and self posts  */
        @Json(name = "any") ANY,
        /** Only links  */
        @Json(name = "link") LINK,
        /** Only self posts  */
        @Json(name = "self") SELF,
        /** Restricted subreddit  */
        @Json(name = "none") NONE
    }

    public enum Type {
        /** Open to all users */
        @Json(name = "public") PUBLIC,
        /** Only approved members can view and submit */
        @Json(name = "private") PRIVATE,
        /** Anyone can view, but only some are approved to submit links */
        @Json(name = "restricted") RESTRICTED,
        /** Only users with reddit gold can post */
        @Json(name = "gold_restricted") GOLD_RESTRICTED,
        @Json(name = "archived") ARCHIVED
    }
}
