package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.ApiException
import net.dean.jraw.models.AccountStatus
import net.dean.jraw.models.MultiredditPatch
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.models.UserHistorySort
import net.dean.jraw.references.UserReference
import net.dean.jraw.test.CredentialsUtil
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.TestConfig.redditUserless
import net.dean.jraw.test.expectDescendingScore
import net.dean.jraw.test.expectException
import net.dean.jraw.test.randomName
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class UserReferenceTest : Spek({
    describe("about") {
        it("should return an Account") {
            // We authenticated `reddit` using the script credentials, should have the same username
            reddit.me().about().name.should.equal(CredentialsUtil.script.username)

            val name = "_vargas_"
            reddit.user(name).about().name.should.equal(name)
        }
    }

    describe("query") {
        it("should return a status of EXISTS on an account that exists") {
            val query = reddit.me().query()
            query.name.should.equal(reddit.requireAuthenticatedUser())
            query.account.should.not.be.`null`
            query.status.should.equal(AccountStatus.EXISTS)
        }

        it("should return a status of NON_EXISTENT on an account name that has never been used") {
            val name = randomName()
            val query = reddit.user(name).query()
            query.name.should.equal(name)
            query.account.should.be.`null`
            query.status.should.equal(AccountStatus.NON_EXISTENT)
        }

        it("should return a status of SUSPENDED on a suspended account") {
            val query = reddit.user("TheFlintASteel").query()
            query.name.should.equal("TheFlintASteel")
            query.account.should.be.`null`
            query.status.should.equal(AccountStatus.SUSPENDED)
        }
    }

    describe("trophies") {
        it("should return a List of Trophies") {
            // Just make sure it deserializes
            reddit.me().trophies()
            reddit.user("Shitty_Watercolour").trophies().should.have.size.above(0)
        }
    }

    describe("history") {
        val me = reddit.me()
        val other = reddit.user("_vargas_")

        // Available to all users
        val allUsers = arrayOf("overview", "submitted", "comments", "gilded")
        // Available to only the logged-in user
        val onlySelf = arrayOf("upvoted", "downvoted", "hidden", "saved")
        // Only these 'where' values are sortable
        val sortable = arrayOf("overview", "submitted", "comments")

        fun testFirst(ref: UserReference<*>, where: String) {
            var builder = ref.history(where)
                .limit(5)

            if (where in sortable)
                builder = builder.sorting(UserHistorySort.TOP)
                .timePeriod(TimePeriod.ALL)

            val models = builder.build().next()

            if (where !in sortable) return
            if (models.isEmpty()) return

            expectDescendingScore(models, allowedMistakes = 1)
        }

        // Dynamically create tests
        for (where in listOf(*allUsers, *onlySelf)) {
            it("should be able to access '$where' for the logged-in user") {
                testFirst(me, where)
            }
        }

        for (where in allUsers) {
            it("should be able to access '$where' for another user") {
                testFirst(other, where)
            }
        }

        for (where in onlySelf) {
            it("should NOT be able to access '$where' for another user") {
                expectException(ApiException::class) {
                    testFirst(other, where)
                }
            }

            it("should throw an IllegalStateException trying to access '$where' with application-only credentials") {
                expectException(IllegalStateException::class) {
                    testFirst(redditUserless.me(), where)
                }
            }
        }
    }

    describe("listMultis") {
        it("should list the logged-in user's multireddits") {
            val me = reddit.me()
            // Create the multireddit and get a reference to it
            val multi = me.createMulti(randomName(), MultiredditPatch.Builder().build())
            val ref = multi.toReference(reddit)

            try {
                me.listMultis().map { it.displayName }.should.contain(multi.displayName)
            } finally {
                // Delete the multireddit
                ref.delete()
            }
        }

        it("should list another users's public multis") {
            // /u/reddit has some official multireddits
            reddit.user("reddit").listMultis().should.have.size.above(0)
        }
    }

    describe("flairOn") {
        it("should equivalent to SubredditReference.otherUserFlair") {
            val sr = "pics"
            reddit.me().flairOn(sr).should.equal(reddit.subreddit(sr).selfUserFlair())
            reddit.user("foo").flairOn(sr).should.equal(reddit.subreddit(sr).otherUserFlair("foo"))
        }
    }
})
