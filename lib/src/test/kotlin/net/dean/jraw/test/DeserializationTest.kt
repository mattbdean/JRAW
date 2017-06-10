package net.dean.jraw.test

import net.dean.jraw.RedditClient
import net.dean.jraw.http.oauth.OAuthHelper
import net.dean.jraw.models.Submission
import net.dean.jraw.models.Subreddit
import net.dean.jraw.models.Thing
import net.dean.jraw.test.util.CredentialsUtil
import net.dean.jraw.test.util.newOkHttpAdapter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates
import kotlin.reflect.KClass

typealias DeserializeTest = (RedditClient) -> List<Thing>

/**
 * We're testing to see if we have our models and Jackson ObjectMapper instance set up correctly. We test deserializing
 * both with and without a user because some properties are null without a logged-in user.
 */
class DeserializationTest : Spek({
    // Map a Thing subclass an array of functions that uses Jackson to deserialize JSON into an instance of that Thing
    val testCases = mapOf<KClass<out Thing>, Array<DeserializeTest>>(
        Subreddit::class to arrayOf<DeserializeTest>(
            // Test both /r/pics and /r/redditdev, two very different subreddits (both content-wise and settings-wise)
            { listOf(it.subreddit("pics").about()) },
            { listOf(it.subreddit("redditdev" ).about()) },
            { listOf(it.randomSubreddit().about()) }
        ),
        Submission::class to arrayOf<DeserializeTest>(
            { it.subreddit("pics").posts().hot() },
            { it.subreddit("redditdev").posts().hot() },
            { it.randomSubreddit().posts().hot() }
        )
    )

    // Create a client for each
    var withUser: RedditClient by Delegates.notNull()
    var withoutUser: RedditClient by Delegates.notNull()

    beforeGroup {
        withUser = OAuthHelper.script(CredentialsUtil.script, newOkHttpAdapter())
        withoutUser = OAuthHelper.applicationOnly(CredentialsUtil.applicationOnly, newOkHttpAdapter())
    }

    // Dynamically create tests for every entry in our testCases map
    for ((klass, testFunctions) in testCases) {
        it("should deserialize a ${klass.simpleName} with a logged in user") {
            for (deserializeThing in testFunctions) {
                deserializeThing(withUser)
            }
        }

        it("should deserialize a ${klass.simpleName} without a logged in user") {
            for (deserializeThing in testFunctions) {
                deserializeThing(withoutUser)
            }
        }
    }
})
