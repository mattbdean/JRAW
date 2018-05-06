package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.models.Listing
import net.dean.jraw.models.UniquelyIdentifiable
import net.dean.jraw.pagination.BackoffStrategy
import net.dean.jraw.pagination.ConstantBackoffStrategy
import net.dean.jraw.pagination.RedditIterable
import net.dean.jraw.pagination.Stream
import net.dean.jraw.test.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class StreamTest : Spek({
    lateinit var dataSource: FakePaginator
    lateinit var stream: Stream<Foo>

    beforeEachTest {
        dataSource = FakePaginator()
        stream = Stream(dataSource, PirateBackoffStrategy())
    }

    it("should yield the first page and then nothing after that") {
        val initialAmount = 5
        dataSource.addNewData(startId = 0, amount = initialAmount)

        // Request the first page. There should only be `initialAmount` elements in the list once we call toList()
        stream
            .asSequence()
            .take(initialAmount)
            .map { it.uniqueId.toInt() }
            .toList()
            .should.equal((0 until initialAmount).toList())

        expectException(NoMoreBootyException::class) {
            stream.next()
        }

        val newStartId = 10
        val newData = 3
        dataSource.addNewData(startId = newStartId, amount = newData)

        for (i in 0 until newData) {
            stream
                .next()
                .uniqueId.toInt().should.equal(newStartId + i)
        }
    }

    it("should throw an exception when the BackoffStrategy returns a negative integer") {
        stream = Stream(dataSource, backoff = ConstantBackoffStrategy(-1L))
        expectException(IllegalArgumentException::class) {
            stream.next()
        }
    }
})

private class FakePaginator(val limit: Int = 5) : RedditIterable<Foo> {
    override val current: Listing<Foo>? = null
    override val pageNumber: Int get() = _pageNumber
    private var _pageNumber: Int = 0
    private val allData = mutableListOf<Foo>()

    private var lastEnd: Int = -1

    fun addNewData(startId: Int, amount: Int) {
        for (i in startId until startId + amount) {
            this.allData.add(0, Foo(i.toString()))
        }
    }

    override fun next(): Listing<Foo> {
        val endIndex = Math.min(lastEnd + 1 + limit, allData.size)
        val next = Listing.create(pageNumber.toString(), allData.subList(lastEnd + 1, endIndex))
        lastEnd = endIndex
        return next
    }

    override fun restart() {
        lastEnd = -1
        _pageNumber = 0
    }

    override fun hasStarted(): Boolean = pageNumber == 0

    // We don't care about these things here
    override fun accumulate(maxPages: Int): List<Listing<Foo>> = throw NotImplementedError()
    override fun accumulateMerged(maxPages: Int): List<Foo> = throw NotImplementedError()
    override fun iterator(): Iterator<Listing<Foo>> = throw NotImplementedError()
}

private data class Foo(override val uniqueId: String) : UniquelyIdentifiable

/** Throws an [NoMoreBootyException] if there are no new items. */
private class PirateBackoffStrategy : BackoffStrategy {
    override fun delayRequest(newItems: Int, totalItems: Int): Long {
        if (newItems == 0)
            throw NoMoreBootyException()

        // No delay
        return 0L
    }
}

private class NoMoreBootyException : RuntimeException("no more booty in this here Stream")
