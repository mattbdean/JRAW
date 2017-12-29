package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.SearchSort
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.pagination.SearchPaginator
import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class SearchPaginatorTest : Spek({
    describe("Builder.build()") {
        it("should keep the settings from the builder") {
            // Just make sure deserialization it doesn't fail for now
            val query = "New Year"
            val paginator = reddit.search()
                .limit(Paginator.RECOMMENDED_MAX_LIMIT)
                .sorting(SearchSort.TOP)
                .timePeriod(TimePeriod.ALL)
                .syntax(SearchPaginator.QuerySyntax.PLAIN)
                .query(query)
                .build()

            paginator.limit.should.equal(Paginator.RECOMMENDED_MAX_LIMIT)
            paginator.sorting.should.equal(SearchSort.TOP)
            paginator.timePeriod.should.equal(TimePeriod.ALL)
            paginator.syntax.should.equal(SearchPaginator.QuerySyntax.PLAIN)
            paginator.query.should.equal(query)
            paginator.baseUrl.should.equal("/search")
        }

        it("should build baseUrl from multiple subreddits") {
            val builder = SearchPaginator.inSubreddits(reddit, "pics", "funny")
            builder.baseUrl.should.equal("/r/pics+funny/search")
        }
    }
})
