package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.Submission
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.test.util.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class SubredditPaginationTest : Spek({
    describe("Builder.build()") {
        it("should keep the settings from the builder") {
            // Just make sure deserialization it doesn't fail for now
            val builder = reddit.subreddit("pics").posts()
                .limit(Paginator.RECOMMENDED_MAX_LIMIT)
                .sorting(Sorting.TOP)
                .timePeriod(TimePeriod.ALL)

            builder.baseUrl.should.equal("/r/pics")

            val ref = builder.build()
            ref.limit.should.equal(Paginator.RECOMMENDED_MAX_LIMIT)
            ref.sorting.should.equal(Sorting.TOP)
            ref.timePeriod.should.equal(TimePeriod.ALL)
        }
    }

    describe("next()") {
        it("should update the current page and current listing") {
            val limit = 10
            val ref = reddit.subreddit("pics").posts()
                .limit(limit)
                .build()

            ref.pageNumber.should.equal(0)
            ref.current.should.equal(null)

            val first10 = ref.next()
            first10.should.have.size.at.most(limit)
            ref.pageNumber.should.equal(1)
            ref.current.should.equal(first10)

            val second10 = ref.next()
            second10[0].fullName.should.not.equal(first10[0].fullName)
            ref.pageNumber.should.equal(2)
            ref.current.should.equal(second10)

            ref.restart()
            ref.pageNumber.should.equal(0)
            ref.current.should.equal(null)
            ref.next()[0].fullName.should.equal(first10[0].fullName)
        }

        it("should request that submissions be returned in a certain order") {
            val limit = 10

            fun expectDescendingScore(posts: Listing<Submission>) {
                var score = posts[0].score

                for (i in 1..limit - 1) {
                    posts[i].score.should.be.below(score)
                    score = posts[i].score
                }
            }

            val ref = reddit.subreddit("pics").posts()
                .sorting(Sorting.TOP)
                .timePeriod(TimePeriod.ALL)
                .limit(limit)
                .build()

            // Test the first 3 pages
            for (i in 0..2)
                expectDescendingScore(ref.next())
        }
    }

    describe("newBuilder()") {
        it("should create a Builder with the same settings as the original Reference") {
            val ref = reddit.subreddit("foo").posts()
                .limit(5)
                .sorting(Sorting.CONTROVERSIAL)
                .timePeriod(TimePeriod.ALL)
                .build()

            val new = ref.newBuilder().build()
            new.limit.should.equal(5)
            new.sorting.should.equal(Sorting.CONTROVERSIAL)
            new.timePeriod.should.equal(TimePeriod.ALL)
        }
    }
})
