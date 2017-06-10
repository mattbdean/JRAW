package net.dean.jraw.test

import net.dean.jraw.test.util.TestConfig
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class SubredditPaginationTest : Spek({
    it("should return a Listing<Submission>") {
        // Just make sure deserialization it doesn't fail for now
        TestConfig.reddit.subreddit("pics").posts().hot()
    }
})
