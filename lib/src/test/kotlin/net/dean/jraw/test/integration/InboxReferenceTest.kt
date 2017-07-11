package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.test.TestConfig.reddit

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class InboxReferenceTest : Spek({
    describe("iterate") {
        val whereValues = listOf("inbox", "unread", "messages", "comments", "selfreply", "mentions")
        val inbox = reddit.me().inbox()
        for (where in whereValues) {
            it("should be able to iterate '$where'") {
                val p = inbox.iterate(where).build()
                val messages = p.accumulate(maxPages = 2)
                if (messages.size > 1)
                    // Make sure BarebonesPaginator is working correctly as far as dealing with multiple pages
                    messages[0][0].fullName.should.not.equal(messages[1][0].fullName)
            }
        }
    }
})
