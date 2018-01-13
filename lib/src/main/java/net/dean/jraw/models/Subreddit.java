package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.RedditClient;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.databind.UnixTime;
import net.dean.jraw.references.Referenceable;
import net.dean.jraw.references.SubredditReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * <p>A community built around a specific topic (e.g. /r/birdswitharms).
 *
 * <p>A note about inaccessible subreddits: If a subreddit that is inaccessible to the currently authenticated user
 * (if any) is queried directly with {@link SubredditReference#about()}, an {@link net.dean.jraw.ApiException} will be
 * thrown. However, if queried indirectly like when an inaccessible subreddit appears in a subreddit search via
 * {@link net.dean.jraw.pagination.SubredditSearchPaginator}, most values will be null. If a method returns a primitive
 * and a null is encountered because of this reason, a default value like {@code false} or {@code -1} will be used
 * instead. This is generally noted in the method documentation.
 */
@AutoValue
@RedditModel
public abstract class Subreddit implements Created, Identifiable, Referenceable<SubredditReference> {
    /**
     * How many accounts are active on this subreddit at one time. If {@link #isAccountsActiveFuzzed()}, this will not
     * be exact.
     */
    @Nullable
    @Json(name = "accounts_active") public abstract Integer getAccountsActive();

    /** If reddit is hiding the true number of active accounts and giving a value that is "close enough" instead. */
    @Json(name = "accounts_active_fuzzed") public abstract boolean isAccountsActiveFuzzed();

    @Json(name = "comment_score_hide_mins")
    @Nullable
    abstract Integer getCommentScoreHideMinsNullable();

    /** How many minutes reddit hides new comments for. Returns -1 if the subreddit is inaccessible to the user */
    public final int getCommentScoreHideMins() {
        Integer result = getCommentScoreHideMinsNullable();
        return result == null ? -1 : result;
    }

    @NotNull
    @Override
    @Json(name = "created_utc") @UnixTime public abstract Date getCreated();

    @NotNull
    @Override
    @Json(name = "name") public abstract String getFullName();

    /** The color that makes up the subreddit's main theme. Mainly used on the mobile site. */
    @Nullable
    @Json(name = "key_color")
    public abstract String getKeyColor();

    /** Name without the "/r/" prefix: "pics", "funny", etc. */
    @Json(name = "display_name") public abstract String getName();

    @Nullable
    @Json(name = "over_18")
    public abstract Boolean getNsfw();

    @Json(name = "over_18")
    public final boolean isNsfw() { return getNsfw() != null && getNsfw(); }

    /**
     * Markdown-formatted text used when this subreddit comes up in searches.
     *
     * See <a href="https://www.reddit.com/subreddits/search">here</a> for more information.
     *
     * @see net.dean.jraw.pagination.SubredditSearchPaginator
     */
    @Json(name = "public_description") public abstract String getPublicDescription();

    @Nullable
    @Json(name = "quarantine")
    abstract Boolean getQuarantined();

    /**
     * If this subreddit has been quarantined. See
     * <a href="https://reddit.zendesk.com/hc/en-us/articles/205701245-Quarantined-Subreddits">here</a> for more.
     */
    @Json(name = "quarantine") public final boolean isQuarantined() { return getQuarantined() != null && getQuarantined(); }

    /** Sidebar content in raw Markdown */
    @Json(name = "description")
    @Nullable
    public abstract String getSidebar();

    @Nullable
    @Json(name = "spoilers_enabled")
    abstract Boolean getSpoilersEnabled();

    /** Whether the subreddit supports Markdown spoilers */
    public final boolean isSpoilersEnabled() { return getSpoilersEnabled() != null && getSpoilersEnabled(); }

    /** What type of submissions can be submitted to this subreddit */
    @Nullable
    @Json(name = "submission_type")
    public abstract SubmissionType getSubmissionType();

    /** The text on the button that users click to submit a link */
    @Nullable
    @Json(name = "submit_link_label") public abstract String getSubmitLinkLabel();

    /** The text on the button that users click to submit a self post */
    @Nullable
    @Json(name = "submit_text_label") public abstract String getSubmitTextLabel();

    @Json(name = "subscribers")
    @Nullable
    abstract Integer getSubscribersNullable();

    /**
     * The amount of subscribers this subreddit has. Returns -1 if the subreddit is inaccessible to the current user.
     */
    public final int getSubscribers() { return getSubscribersNullable() == null ? -1 : getSubscribersNullable(); }

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

    /**
     * The text to be displayed by the user's name in all comments/submissions in this subreddit, or null if there is no
     * authenticated user or selected flair.
     */
    @Nullable
    @Json(name = "user_flair_text") public abstract String getUserFlairText();

    @Json(name = "user_flair_enabled_in_sr")
    @Nullable
    abstract Boolean getUserFlairGenerallyEnabled();

    /**
     * Returns true if user flair for all users is enabled on this subreddit. Note that this value will always be false
     * when there is no authenticated user.
     */
    public final boolean isUserFlairGenerallyEnabled() {
        return getUserFlairGenerallyEnabled() != null && getUserFlairGenerallyEnabled();
    }

    /** If the flair for this particular user is enabled */
    @Nullable
    @Json(name = "user_sr_flair_enabled") abstract Boolean getUserFlairEnabled();

    public final boolean isFlairEnabledForUser() {
        return getUserFlairEnabled() != null && getUserFlairEnabled();
    }

    // See https://github.com/google/auto/issues/275 on why we need this
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

    public enum Access {
        /** Open to all users */
        @Json(name = "public") PUBLIC,
        /** Only approved members can view and submit */
        @Json(name = "private") PRIVATE,
        /** Anyone can view, but only some are approved to submit links */
        @Json(name = "restricted") RESTRICTED,
        /** Only users with reddit gold can post */
        @Json(name = "gold_restricted") GOLD_RESTRICTED,
        @Json(name = "archived") ARCHIVED,
        /** This subreddit is actually a user's profile page */
        @Json(name = "user") USER
    }
}
