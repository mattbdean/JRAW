package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.randomName
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class InboxReferenceTest : Spek({
    val inbox = reddit.me().inbox()

    describe("iterate") {
        val whereValues = listOf("inbox", "unread", "messages", "comments", "selfreply", "mentions")
        for (where in whereValues) {
            it("should be able to iterate '$where'") {
                val p = inbox.iterate(where).build()
                val messages = p.accumulate(maxPages = 2)
                if (messages.size > 1 && messages[0].isNotEmpty() && messages[1].isNotEmpty())
                    // Make sure BarebonesPaginator is working correctly as far as dealing with multiple pages
                    messages[0][0].fullName.should.not.equal(messages[1][0].fullName)
            }
        }
    }

    describe("compose/markRead/delete") {
        it("should be able to send a message") {
            val body = "random ID: ${randomName()}"
            inbox.compose(reddit.requireAuthenticatedUser(), "test PM", body)

            // Make sure it appeared in our inbox. Messages sent to yourself are always marked as read, regardless of
            // calls to POST /api/unread_message. There's not much we can in this situation besides make sure that the
            // response doesn't contain any errors
            val message = inbox.iterate("messages").build().next().firstOrNull { it.body == body }
            message.should.not.be.`null`

            inbox.markRead(false, message!!.fullName)
            inbox.markRead(true, message.fullName)

            // Jst make sure it doesn't fail
            inbox.delete(message.fullName)
        }
    }

    describe("markAllRead") {
        it("should mark all messages as read") {
            // Again, there's no way we can really test this without involving another testing user
            inbox.markAllRead()
        }
    }
})
