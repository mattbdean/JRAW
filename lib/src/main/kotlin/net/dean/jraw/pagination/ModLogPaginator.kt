package net.dean.jraw.pagination

import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.ModAction

class ModLogPaginator private constructor(
    reddit: RedditClient,
    val subreddit: String,
    baseUrl: String,
    limit: Int,
    val actionType: String? = null,
    val moderatorName: String? = null
) : BarebonesPaginator<ModAction>(reddit, baseUrl, limit, ModAction::class.java) {

    override fun createNextRequest(): HttpRequest.Builder {
        val builder = super.createNextRequest()

        val extraQueryArgs = mutableMapOf<String, String>()
        if (actionType != null)
            extraQueryArgs["type"] = actionType
        if (moderatorName != null)
            extraQueryArgs["mod"] = moderatorName

        return builder.query(extraQueryArgs)
    }

    class Builder(reddit: RedditClient, val subreddit: String) : Paginator.Builder<ModAction>(
        reddit = reddit,
        baseUrl = "/r/${JrawUtils.urlEncode(subreddit)}/about/log",
        clazz = ModAction::class.java
    ) {
        private var limit: Int = Paginator.DEFAULT_LIMIT
        private var actionType: String? = null
        private var moderatorName: String? = null

        /**
         * The maximum amount of items per page. Unlike most other paginated endpoints, this one allows up to 500 items
         * per page instead of the regular 100.
         */
        fun limit(limit: Int): Builder { this.limit = limit; return this }

        /** What kind of action to filter by. A null value will remove the filter */
        fun actionType(actionType: String?): Builder { this.actionType = actionType; return this }

        /**
         * The username (not fullname) of a moderator. Only actions performed by this user will be returned. A value of
         * null removes the filter
         */
        fun moderatorName(moderatorName: String?): Builder { this.moderatorName = moderatorName; return this }

        override fun build(): ModLogPaginator =
            ModLogPaginator(reddit, subreddit, baseUrl, limit, actionType, moderatorName)
    }
}
