package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.test.TestConfig
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class SubredditTest : Spek({
    val sub = TestConfig.reddit.subreddit("pics")
    describe("about") {
        it("should return a Subreddit instance") {
            val pics = sub.about()
            pics.name.should.equal("pics")

            // Make sure the Date serialization treats this as seconds instead of milliseconds
            // See /r/pics.json --> created_utc
            pics.created.should.be.above(Date(1201132800))
        }
    }

    describe("randomSubmission") {
        it("should retrieve a random submission") {
            val links = (0..1).map { sub.randomSubmission().submission }
            // Pretty unlikely reddit will send us back the exact same submission twice in a row
            links[0].id.should.not.equal(links[1].id)
        }
    }
})
