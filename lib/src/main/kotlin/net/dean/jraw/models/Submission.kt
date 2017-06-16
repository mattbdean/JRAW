package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.dean.jraw.databind.DistinguishedStatusDeserializer
import net.dean.jraw.databind.UnixTimeDeserializer
import java.util.*

data class Submission(
    /** Username of the author (e.g. "Unidan") */
    @JsonProperty("author") val authorName: String,

    /** Flair text to be displayed next to the author's name, if any */
    @JsonProperty("author_flair_text")
    val authorFlair: String?,

    /** Submissions are archived once they reach a certain age. At that point, they become unmodifiable */
    val archived: Boolean,

    override val canGild: Boolean,

    @JsonProperty("created_utc")
    @JsonDeserialize(using = UnixTimeDeserializer::class)
    override val created: Date,

    val contestMode: Boolean,

    /** The status of the person who created this Submission. Always non-null */
    @JsonDeserialize(using = DistinguishedStatusDeserializer::class)
    // Because of how the way the Kotlin Jackson module works, this property has to be marked as nullable even though
    // its deserializer always returns a non-null value. Jackson sees that the "distinguished" property is null and
    // immediately throws an Exception
    override val distinguished: DistinguishedStatus?,

    /**
     * Domain of this Submission's URL. If this is a self post, this property will be equal to `self.{subreddit}`,
     * otherwise it will be the actual link domain (e.g. "i.imgur.com")
     *
     * @see [isSelfPost]
     */
    val domain: String,

    /**
     * The last time this Submission was edited, or null if it hasn't been
     */
    @JsonDeserialize(using = UnixTimeDeserializer::class)
    val edited: Date?,

    /**
     * The full name of the submission, equivalent to `"t3_" + id`
     */
    @JsonProperty("name")
    val fullName: String,

    override val gilded: Short,

    /** Is this post hidden from the current user? */
    val hidden: Boolean,

    /** If reddit is masking this Submission's score */
    val hideScore: Boolean,

    /** The unique ID given to this Submission by reddit */
    val id: String,

    /** If this Submission is a self (text-only) post */
    @JsonProperty("is_self")
    val isSelfPost: Boolean,

    /** Logged-in-user vote status. True for upvote, false for downvote, null for no vote. */
    override val likes: Boolean?,

    /** Flair to display next to the Submission, if any */
    val linkFlairText: String?,

    /** If the moderators/admins have prevented creating new comments on this submission */
    val locked: Boolean,

    /** If this Submission contains adult content */
    @JsonProperty("over_18")
    val nsfw: Boolean,

    /** URL relative to reddit.com to access this Submission from a web browser */
    val permalink: String,

    /** The type of content reddit thinks this submission links to */
    val postHint: String?,

    /** If the subreddit this Submission has been posted to has been quarantined */
    val quarantine: Boolean,

    /** The number of reports this Submission has received */
    @JsonProperty("num_reports")
    val reports: Int,

    /** If the user has saved this to their profile for later */
    val saved: Boolean,

    override val score: Int,

    /** Markdown-formatted content, non-null when [isSelfPost] is true */
    val selftext: String?,

    /** If reddit thinks this Submission is spam */
    val spam: Boolean,

    /** If the creator of this Submission has marked it as containing spoilers */
    val spoiler: Boolean,

    /** Stickied submissions appear as the first submissions when browsing a subreddit by 'hot' */
    val stickied: Boolean,

    /** Name of the subreddit this Submission is hosted in */
    val subreddit: String,

    /**
     * Full name of [subreddit]
     *
     * @see Subreddit.fullName
     */
    @JsonProperty("subreddit_id")
    val subredditFullName: String,

    /** The suggested default comment sort */
    val suggestedSort: CommentSort?,

    /** An empty string for self posts, otherwise a reddit-generated-and-hosted thumbnail */
    val thumbnail: String,

    /** Title of the submission */
    val title: String,

    /** An absolute URL to the comments for a self post, otherwise an absolute URL to the Submission content */
    val url: String,

    /**
     * If the user has visited this Submission before. Requires a call to [net.dean.jraw.Endpoint.POST_STORE_VISITS] and
     * a subscription to reddit Gold
     */
    val visited: Boolean

    // TODO:
//    val userReports: List<UserReport>,
//    val modReports: List<ModReport>,
//    val media: ?
//    val preview: Preview,
//    val vote: VoteDirection // instead of `likes`
//    val postHint: Hint
) : Thing(ThingType.SUBMISSION), Created, Distinguishable, Gildable, Votable
