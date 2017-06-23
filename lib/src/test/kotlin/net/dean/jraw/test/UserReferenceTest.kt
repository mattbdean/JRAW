package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.ApiException
import net.dean.jraw.models.MultiredditPatch
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.references.UserReference
import net.dean.jraw.test.util.CredentialsUtil
import net.dean.jraw.test.util.TestConfig.reddit
import net.dean.jraw.test.util.TestConfig.redditUserless
import net.dean.jraw.test.util.expectException
import net.dean.jraw.test.util.randomName
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

    describe("trophies") {
        it("should return a List of Trophies") {
            // Just make sure it deserializes
            reddit.me().trophies()
            reddit.user("Shitty_Watercolour").trophies()
        }
    }

    describe("prefs and patchPrefs") {
        it("should return a Map<String, Any>") {
            val prefs = reddit.me().prefs()
            prefs.should.have.size.above(0)
            // This one has been here since forever, if this one isn't here either reddit has undergone a major API
            // change or we're making the request wrong.
            prefs["over_18"].should.not.be.`null`
        }

        it("should update the preferences") {
            val me = reddit.me()

            // Go with something that's 1) pretty much guaranteed to be there and 2) a boolean so we can sipmly toggle
            // the value
            val key = "over_18"
            val newVal = !(me.prefs()[key] as Boolean)
            val newPrefsPatch = mapOf(key to newVal)
            me.patchPrefs(newPrefsPatch)[key].should.equal(newVal)
        }

        it("should throw an ApiException when the user isn't 'me'") {
            expectException(ApiException::class) {
                redditUserless.me().prefs()
            }

            val oldPrefs = reddit.me().prefs()
            expectException(ApiException::class) {
                redditUserless.me().patchPrefs(oldPrefs)
            }
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

        fun testFirst(ref: UserReference, where: String) {
            var builder = ref.history(where)
                .limit(5)

            if (where in sortable)
                builder = builder.sorting(Sorting.TOP)
                .timePeriod(TimePeriod.ALL)

            val models = builder.build().next()

            if (where !in sortable) return
            if (models.isEmpty()) return
            var last: Int = models[0].score

            for (i in 1..models.size - 1) {
                models[i].score.should.be.at.most(last)
                last = models[i].score
            }
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
            val multi = me.multi(randomName()).createOrUpdate(MultiredditPatch.Builder().build())
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
})
