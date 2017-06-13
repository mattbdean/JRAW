package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.dean.jraw.databind.DistinguishedStatusDeserializer
import net.dean.jraw.databind.UnixTimeDeserializer
import java.util.*

/**
 * This class represents a comment on a [Submission].
 *
 * Comments are usually created through [CommentNode]s:
 *
 * ```kotlin
 * val firstTopLevelComment: Comment = reddit.submission(id).comments().replies[0].comment
 * ```
 */
data class Comment(
    /** If this comment belongs to a Submission which has been marked as unmodifiable */
    val archived: Boolean,

    /** Username of the user that created this comment */
    val author: String,

    /** Flair to appear next to the creator of this comment's name, if any */
    val authorFlairText: String?,

    /** The Markdown-formatted body of this comment */
    val body: String,

    /** If the currently logged-in-user can give reddit Gold to this comment */
    val canGild: Boolean,

    /**
     * Get this comments controversiality level. A comment is considered controversial if it has a large number of both
     * upvotes and downvotes. 0 means not controversial, 1 means controversial.
     */
    val controversiality: Int,

    /** When this comment was created */
    @JsonProperty("created_utc")
    @JsonDeserialize(using = UnixTimeDeserializer::class)
    override val created: Date,

    /** The level of distinguishment */
    @JsonDeserialize(using = DistinguishedStatusDeserializer::class) val distinguished: DistinguishedStatus?,

    /** When this comment was edited, if any */
    @JsonDeserialize(using = UnixTimeDeserializer::class)
    val edited: Date?,

    /** The full name of the comment (`t1_` + [id]) */
    @JsonProperty("name")
    val fullName: String,

    /** How many times this comment was given reddit Gold */
    val gilded: Short,

    /** The unique base 36 identifier given to this comment by reddit */
    val id: String,

    /** True for upvote, false for downvote, null for no vote */
    val likes: Boolean?,

    /** If the user has saved this comment or not */
    val saved: Boolean,

    /** The amount of upvotes minus downvotes */
    val score: Int,

    /** If reddit is masking the score of a new comment */
    val scoreHidden: Boolean,

    /** If this comment is shown at the top of the comment section, regardless of score */
    val stickied: Boolean,

    /** The full name of the submission that this comment is contained in (`t3_XXXXX`) */
    @JsonProperty("link_id")
    val submissionFullName: String,

    /** The name of the subreddit (e.g. "pics") */
    @JsonProperty("subreddit")
    val subredditName: String,

    /** The subreddit's full name */
    @JsonProperty("subreddit_id")
    val subredditFullName: String,

    /** The restrictions for accessing this subreddit */
    val subredditType: Subreddit.Type
) : Thing(ThingType.COMMENT), Created
