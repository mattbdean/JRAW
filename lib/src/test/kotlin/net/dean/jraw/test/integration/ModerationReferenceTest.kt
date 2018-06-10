package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.test.CredentialsUtil.moderationSubreddit
import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ModerationReferenceTest : Spek({
    val ref = reddit.subreddit(moderationSubreddit).moderate()

    describe("log") {
        it("should paginate the moderation log") {
            val paginator = ref.log().build()

            // Make sure there's no error
            paginator.next()
        }

        it("should allow filtering by one type of action") {
            val action = ref.log().limit(1).build().next().first().action

            val filteredLog = ref.log().actionType(action).build().next()
            filteredLog.should.not.be.empty
            filteredLog.forEach {
                it.action.should.equal(action)
            }
        }

        it("should allow filtering by one moderator") {
            val moderator = ref.log().limit(1).build().next().first().moderator

            val filteredLog = ref.log().moderatorName(moderator).build().next()
            filteredLog.should.not.be.empty
            filteredLog.forEach {
                it.moderator.should.equal(moderator)
            }
        }
    }
})
