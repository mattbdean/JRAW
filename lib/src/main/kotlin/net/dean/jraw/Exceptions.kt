package net.dean.jraw

import net.dean.jraw.http.NetworkException

sealed class RedditException constructor(reason: String, cause: Throwable?) :
    RuntimeException(reason, cause)

/**
 * Represents a general error returned by the reddit API.
 * See [here](https://github.com/reddit/reddit/blob/master/r2/r2/lib/errors.py) for a full list of codes.
 */
class ApiException(val code: String, val explanation: String, val relevantParameters: List<String>, cause: NetworkException) :
    RedditException("API returned error: $code ($explanation), relevant parameters: $relevantParameters", cause)

/**
 * Thrown when reddit prevents the client from doing an action because it has done that action too frequently in the
 * recent past. For example, trying to post several links to a subreddit the user doesn't moderate in less than one
 * minute.
 */
class RateLimitException(val cooldown: Double, cause: NetworkException) :
    RedditException("reddit is ratelimiting this action, try again in ~${cooldown.toInt()} seconds", cause)

/** Contains the data to create a RedditException */
internal sealed class RedditExceptionStub<out T : RedditException> {
    abstract fun create(cause: NetworkException): T
}

internal data class ApiExceptionStub(val code: String, val explanation: String, val parameters: List<String>)
    : RedditExceptionStub<ApiException>() {

    override fun create(cause: NetworkException) = ApiException(code, explanation, parameters, cause)
}

internal data class RateLimitExceptionStub(val cooldown: Double) : RedditExceptionStub<RateLimitException>() {
    override fun create(cause: NetworkException) = RateLimitException(cooldown, cause)
}
