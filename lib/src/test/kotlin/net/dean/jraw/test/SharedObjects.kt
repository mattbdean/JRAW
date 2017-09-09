package net.dean.jraw.test

import net.dean.jraw.RateLimitException
import net.dean.jraw.models.SubmissionKind
import net.dean.jraw.references.SubmissionReference
import net.dean.jraw.test.TestConfig.reddit
import java.util.*

/**
 * A collection of immutable objects which are shared throughout the tests that are created via heavily rate-limited API
 * methods.
 *
 * In a typical unit test, we'd like to call API methods as much as necessary to guarantee we're making the call
 * correctly. However, this can create situations where we'd have to wait a long time before starting another test.
 *
 * Consider two tests, one which submits posts and another which edits it. Ideally, we would like each test to create
 * their own submission. However, reddit allows one call to `/api/submit` every 10 minutes for new accounts. We would
 * either have to wait 10 minutes to execute the next test or re-use the newly created submission. The latter is exactly
 * what this class is for. It contains lazily-initiated objects that are created by heavily-rate limited API endpoints.
 */
object SharedObjects {
    val submittedSelfPost: SubmissionReference? by lazyRateLimited {
        reddit
            .subreddit("jraw_testing2")
            .submit(SubmissionKind.SELF, "test self post", "submitted ${Date()}", sendReplies = false)
    }

    private fun <T> lazyRateLimited(create: () -> T): Lazy<T?> {
        return lazy {
            try {
                create()
            } catch (e: RateLimitException) {
                null
            }
        }
    }
}
