package net.dean.jraw.test.integration

import net.dean.jraw.RedditClient
import net.dean.jraw.models.*
import net.dean.jraw.test.TestConfig
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates
import kotlin.reflect.KClass

typealias DeserializeTest = (RedditClient) -> List<RedditObject>

/**
 * We're testing to see if we have our models and Jackson ObjectMapper instance set up correctly. We test deserializing
 * both with and without a user because some properties are null without a logged-in user.
 */
class DeserializationTest : Spek({
    fun subredditPosts(reddit: RedditClient, sr: String) = reddit.subreddit(sr).posts().sorting(Sorting.HOT).build().next()
    // Map a Thing subclass an array of functions that uses Jackson to deserialize JSON into an instance of that Thing
    val testCases = mapOf<KClass<out RedditObject>, Array<DeserializeTest>>(
        Subreddit::class to arrayOf<DeserializeTest>(
            // Test both /r/pics and /r/redditdev, two very different subreddits (both content-wise and settings-wise)
            { listOf(it.subreddit("pics").about()) },
            { listOf(it.subreddit("redditdev" ).about()) },
            { listOf(it.randomSubreddit().about()) }
        ),
        Submission::class to arrayOf<DeserializeTest>(
            { subredditPosts(it, "pics") },
            { subredditPosts(it, "redditdev") },
            { subredditPosts(it, "random") }
        )
    )

    // Create a client for each
    var withUser: RedditClient by Delegates.notNull()
    var withoutUser: RedditClient by Delegates.notNull()

    beforeGroup {
        withUser = TestConfig.reddit
        withoutUser = TestConfig.redditUserless
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
