package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.dean.jraw.RedditClient
import net.dean.jraw.databind.UnixTimeDeserializer
import net.dean.jraw.references.Referenceable
import net.dean.jraw.references.SubredditReference
import java.util.*

data class Subreddit(
    /**
     * How many accounts are active on this subreddit at one time. If [accountsActiveIsFuzzed], this will not be the
     * exact number of accounts.
     */
    val accountsActive: Int,

    /** If true, [accountsActive] will be inexact */
    val accountsActiveIsFuzzed: Boolean,

    /** How many minutes reddit hides new comments for */
    val commentScoreHideMins: Int,

    @JsonProperty("created_utc")
    @JsonDeserialize(using = UnixTimeDeserializer::class)
    override val created: Date,

    /** The result of "t5_" + [id] */
    @JsonProperty("name")
    val fullName: String,

    /** A unique base-36 identifier for this Subreddit, e.g. "2qh0u" in the case of /r/pics */
    val id: String,

    /** A hex color used primarily to style the header of the mobile site */
    val keyColor: String,

    /** Language code, e.g. "en" for English */
    val lang: String,

    /** Name without the "/r/" prefix: "pics", "funny", etc. */
    @JsonProperty("display_name")
    val name: String,

    /** If this subreddit contains mostly adult content */
    val over18: Boolean,

    /**
     * If this subreddit has been quarantined. See
     * [here](https://reddit.zendesk.com/hc/en-us/articles/205701245-Quarantined-Subreddits) for more.
     */
    val quarantine: Boolean,

    /** Sidebar content in raw Markdown */
    @JsonProperty("description")
    val sidebar: String,

    /** Whether the subreddit supports Markdown spoilers */
    val spoilersEnabled: Boolean,

    /** What type of submissions can be submitted to this subreddit */
    val submissionType: SubmissionType,

    /** The text on the button that users click to submit a link */
    val submitLinkLabel: String?,

    /** The text on the button that users click to submit a self post */
    val submitTextLabel: String?,
    @JsonProperty("subreddit_type") val subredditAvailability: Type,

    /** The amount of subscribers this subreddit has */
    val subscribers: Int,

    /** The suggested default comment sort */
    val suggestedCommentSort: CommentSort?,

    /** The URL to access this subreddit relative to reddit.com. For example, "/r/pics" */
    val url: String,

    @JsonProperty("user_is_muted") val isUserMuted: Boolean,
    @JsonProperty("user_is_banned") val isUserBanned: Boolean,
    @JsonProperty("user_is_contributor") val isUserContributor: Boolean,
    @JsonProperty("user_is_moderator") val isUserModerator: Boolean,
    @JsonProperty("user_is_subscriber") val isUserIsSubscriber: Boolean,

    /** If this subreddit's wiki is enabled */
    val wikiEnabled: Boolean
) : Thing(ThingType.SUBREDDIT), Created, Referenceable<SubredditReference> {
    override fun toReference(reddit: RedditClient): SubredditReference = reddit.subreddit(name)

    enum class Type {
        /** Open to all users */
        @JsonProperty("public") PUBLIC,
        /** Only approved members can view and submit */
        @JsonProperty("private") PRIVATE,
        /** Anyone can view, but only some are approved to submit links */
        @JsonProperty("restricted") RESTRICTED,
        /** Only users with reddit gold can post */
        @JsonProperty("gold_restricted") GOLD_RESTRICTED,
        @JsonProperty("archived") ARCHIVED
    }

    /** An enumeration of how a subreddit can restrict the type of submissions that can be posted  */
    enum class SubmissionType {
        /** Links and self posts  */
        @JsonProperty("any") ANY,
        /** Only links  */
        @JsonProperty("link") LINK,
        /** Only self posts  */
        @JsonProperty("self") SELF,
        /** Restricted subreddit  */
        @JsonProperty("none") NONE
    }
}

