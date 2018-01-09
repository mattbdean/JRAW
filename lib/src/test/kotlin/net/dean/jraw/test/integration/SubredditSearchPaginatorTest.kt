package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.SubredditSearchSort
import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class SubredditSearchPaginatorTest : Spek({
    it("should return the most relevant results when requested") {
        val limit = 10
        val query = "test"

        val p = reddit.searchSubreddits()
            .limit(limit)
            .query(query)
            .build()

        // This should be the default
        p.sorting.should.equal(SubredditSearchSort.RELEVANCE)

        // There are at least 10 subreddits with "test" in the name or public description, the search should pick up on that
        p.next().forEach { (it.name + "\n" + it.publicDescription).toLowerCase().should.contain(query) }
    }

    it("should return subreddits with the most activity when requested") {
        val query = "test"

        val sortings = SubredditSearchSort.values()
        val averageSubs = sortings.map {
            // Find the top 100 results for each sorting method
            reddit.searchSubreddits()
                .limit(100)
                .query(query)
                .sorting(it)
                .build()
                .next()
                // We only care about the subscribers
                .map { it.subscribers }
                // Find the average amount of subscribers per subreddit on the first page of the results
                .average()
        }

        // Create a Map using sortings as keys and averageSubs as values
        val mappedResults = sortings.zip(averageSubs).toMap()

        // We would expect that subreddits with a higher activity would have (on average) a higher amount of subscribers
        mappedResults[SubredditSearchSort.ACTIVITY].should.be.above(mappedResults[SubredditSearchSort.RELEVANCE]!!)
    }
})
