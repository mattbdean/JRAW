package net.dean.jraw.meta

data class Endpoint(
    /** HTTP request method ("GET", "POST", etc.) */
    val method: String,

    /** Endpoint path, such as `/api/comment` */
    val path: String,

    /** The OAuth scope required to send a successful request to this endpoint */
    val oauthScope: String,

    /**
     * The URL to reddit's documentation at https://www.reddit.com/dev/api/oauth. For example, the doc link for
     * `POST /api/comment` is `https://www.reddit.com/dev/api/oauth#POST_api_comment`.
     */
    val redditDocLink: String,

    /**
     * True if the original path was prefixed with "[/r/subreddit]", indicating that a request can be made either to
     * `/foo` or `/r/{subreddit}/foo`
     */
    val subredditPrefix: Boolean
)
