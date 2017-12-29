package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.expectDescendingScore
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class SubmissionPaginationTest : Spek({
    describe("Builder.build()") {
        it("should keep the settings from the builder") {
            // Just make sure deserialization it doesn't fail for now
            val builder = reddit.subreddit("pics").posts()
                .limit(Paginator.RECOMMENDED_MAX_LIMIT)
                .sorting(SubredditSort.TOP)
                .timePeriod(TimePeriod.ALL)

            builder.baseUrl.should.equal("/r/pics")

            val ref = builder.build()
            ref.limit.should.equal(Paginator.RECOMMENDED_MAX_LIMIT)
            ref.sorting.should.equal(SubredditSort.TOP)
            ref.timePeriod.should.equal(TimePeriod.ALL)
        }
    }

    describe("next()") {
        it("should update the current page and current listing") {
            val limit = 10
            val ref = reddit.subreddit("pics").posts()
                // Sorting by NEW never returns stickied posts, which don't count towards the limit. If we specified HOT
                // instead (which is used by default), setting a limit of 10 could yield up to 12 submissions (since
                // there are allowed to be 2 stickied submissions at a time).
                .sorting(SubredditSort.NEW)
                .limit(limit)
                .build()

            ref.pageNumber.should.equal(0)
            ref.current.should.equal(null)
            ref.hasStarted().should.equal(false)

            val first10 = ref.next()
            first10.should.have.size.at.most(limit)
            ref.pageNumber.should.equal(1)
            ref.current.should.equal(first10)
            ref.hasStarted().should.equal(true)

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

            val sub = reddit.subreddit("pics").posts()
                .sorting(SubredditSort.TOP)
                .timePeriod(TimePeriod.ALL)
                .limit(limit)
                .build()

            // Test the first 3 pages
            for (i in 0..2) {
                expectDescendingScore(sub.next(), allowedMistakes = 1)
            }
        }
    }

    describe("accumulate and accumulateMerged") {
        val limit = 5
        val maxPages = 5
        val pag = reddit.subreddit("pics").posts()
            // See comment in the describe block for 'next()' about why this is necessary
            .sorting(SubredditSort.NEW)
            .limit(limit)
            .build()

        it("should only fetch up to a specific amount of pages") {
            val listings = pag.accumulate(maxPages)
            listings.should.have.size.above(0)
            listings.should.have.size.at.most(maxPages)
            listings.forEach { it.should.have.size.at.most(limit) }

            val posts = pag.accumulateMerged(maxPages)
            posts.should.have.size.above(0)
            posts.should.have.size.at.most(maxPages * limit)
        }

    }

})
