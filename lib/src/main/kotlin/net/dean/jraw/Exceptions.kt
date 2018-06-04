package net.dean.jraw

import net.dean.jraw.http.NetworkException

/** A RedditException is an Exception thrown because an error was detected by a response from the reddit API. */
sealed class RedditException constructor(reason: String, cause: Throwable?) :
    RuntimeException(reason, cause)

/**
 * Represents a general error returned by the reddit API.
 * See [here](https://github.com/reddit/reddit/blob/master/r2/r2/lib/errors.py) for a full list of codes.
 *
 * @property code A code unique to every type of error. For example, every error returned because the API is expecting
 * an authenticated user has the code `USER_REQUIRED`
 * @property explanation A human-readable version of the code.
 * @property relevantParameters A list of parameter names that reddit thinks were relevant to why the error was returned
 */
class ApiException(val code: String, val explanation: String, val relevantParameters: List<String>, cause: NetworkException) :
    RedditException("API returned error: $code ($explanation), relevant parameters: $relevantParameters", cause)

/**
 * Thrown when reddit prevents the client from doing an action because it has done that action too frequently in the
 * recent past. For example, trying to post several links to a subreddit the user doesn't moderate in less than one
 * minute.
 *
 * @property cooldown The amount of seconds until the client can send another request without being ratelimited
 */
class RateLimitException(val cooldown: Double, cause: NetworkException) :
    RedditException("reddit is ratelimiting this action, try again in ~${cooldown.toInt()} seconds", cause)

class NoSuchSubredditException(val subreddit: String, cause: Throwable? = null) :
    RuntimeException("/r/$subreddit doesn't seem to exist", cause)
