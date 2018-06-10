package net.dean.jraw.models

/**
 * A list of values for the "kind" node in a typical JSON response. See
 * [net.dean.jraw.databind.RedditModelAdapterFactory] for more information.
 */
object KindConstants {
    /** A [Comment] */
    const val COMMENT = "t1"

    /** An [Account] */
    const val ACCOUNT = "t2"

    /** A [Submission] */
    const val SUBMISSION = "t3"

    /** A [Message] */
    const val MESSAGE = "t4"

    /** A [Subreddit] */
    const val SUBREDDIT = "t5"

    /** A [Trophy] */
    const val TROPHY = "t6"

    /** A [MoreChildren] */
    const val MORE_CHILDREN = "more"

    /** A [Listing] */
    const val LISTING = "Listing"

    /** A [Multireddit] */
    const val MULTIREDDIT = "LabeledMulti"

    /** A [LiveUpdate] */
    const val LIVE_UPDATE = "LiveUpdate"

    /** A [LiveThread] */
    const val LIVE_THREAD = "LiveUpdateEvent"

    /** A list of [Trophy] objects */
    const val TROPHY_LIST = "TrophyList"

    /** A [net.dean.jraw.models.internal.LabeledMultiDescription] */
    const val LABELED_MULTI_DESC = "LabeledMultiDescription"

    /** A [WikiPage] */
    const val WIKI_PAGE = "wikipage"

    /** A [ModAction] */
    const val MODACTION = "modaction"
}
