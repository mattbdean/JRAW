package net.dean.jraw.test.integration

import org.jetbrains.spek.api.Spek

class UserReferenceTest : Spek({
    // TODO
//    describe("about") {
//        it("should return an Account") {
//            // We authenticated `reddit` using the script credentials, should have the same username
//            reddit.me().about().name.should.equal(CredentialsUtil.script.username)
//
//            val name = "_vargas_"
//            reddit.user(name).about().name.should.equal(name)
//        }
//    }
//
//    describe("trophies") {
//        it("should return a List of Trophies") {
//            // Just make sure it deserializes
//            reddit.me().trophies()
//            reddit.user("Shitty_Watercolour").trophies()
//        }
//    }
//
//    describe("history") {
//        val me = reddit.me()
//        val other = reddit.user("_vargas_")
//
//        // Available to all users
//        val allUsers = arrayOf("overview", "submitted", "comments", "gilded")
//        // Available to only the logged-in user
//        val onlySelf = arrayOf("upvoted", "downvoted", "hidden", "saved")
//        // Only these 'where' values are sortable
//        val sortable = arrayOf("overview", "submitted", "comments")
//
//        fun testFirst(ref: UserReference, where: String) {
//            var builder = ref.history(where)
//                .limit(5)
//
//            if (where in sortable)
//                builder = builder.sorting(Sorting.TOP)
//                .timePeriod(TimePeriod.ALL)
//
//            val models = builder.build().next()
//
//            if (where !in sortable) return
//            if (models.isEmpty()) return
//
//            expectDescendingScore(models, allowedMistakes = 1)
//        }
//
//        // Dynamically create tests
//        for (where in listOf(*allUsers, *onlySelf)) {
//            it("should be able to access '$where' for the logged-in user") {
//                testFirst(me, where)
//            }
//        }
//
//        for (where in allUsers) {
//            it("should be able to access '$where' for another user") {
//                testFirst(other, where)
//            }
//        }
//
//        for (where in onlySelf) {
//            it("should NOT be able to access '$where' for another user") {
//                expectException(ApiException::class) {
//                    testFirst(other, where)
//                }
//            }
//
//            it("should throw an IllegalStateException trying to access '$where' with application-only credentials") {
//                expectException(IllegalStateException::class) {
//                    testFirst(redditUserless.me(), where)
//                }
//            }
//        }
//    }
//
//    describe("listMultis") {
//        it("should list the logged-in user's multireddits") {
//            val me = reddit.me()
//            // Create the multireddit and get a reference to it
//            val multi = me.createMulti(randomName(), MultiredditPatch.Builder().build())
//            val ref = multi.toReference(reddit)
//
//            try {
//                me.listMultis().map { it.displayName }.should.contain(multi.displayName)
//            } finally {
//                // Delete the multireddit
//                ref.delete()
//            }
//        }
//
//        it("should list another users's public multis") {
//            // /u/reddit has some official multireddits
//            reddit.user("reddit").listMultis().should.have.size.above(0)
//        }
//    }
})
