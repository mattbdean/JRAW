package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.RateLimitException
import net.dean.jraw.models.LiveThreadPatch
import net.dean.jraw.models.LiveWebSocketUpdate
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.references.LiveThreadReference
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.websocket.LiveThreadListener
import net.dean.jraw.websocket.ReadOnlyWebSocketHelper
import okhttp3.Response
import okhttp3.WebSocket
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates

class LiveThreadReferenceTest : Spek({
    val title = "test"
    val desc = "test description"
    val res = "test resources"
    val nsfw = true

    var initialized = true
    var ref: LiveThreadReference by Delegates.notNull()
    var ws: ReadOnlyWebSocketHelper by Delegates.notNull()

    val updateEvents = ArrayList<LiveWebSocketUpdate>()
    val expectedEventTypes: MutableList<String> = ArrayList()

    // Since creating live threads is ratelimited, we have to structure the test in such a way that complies with its
    // livecycle. the beforeGroup and afterGroup hooks are used instead of it() blocks since we can't guarantee that one
    // test will execute before the other.

    beforeGroup {
        try {
            ref = reddit.me().createLiveThread(LiveThreadPatch.Builder()
                .title("test")
                .description(desc)
                .resources(res)
                .nsfw(nsfw)
                .build())
        } catch (e: RateLimitException) {
            initialized = false
            e.printStackTrace()
        }

        if (initialized) {
            val thread = ref.about()
            thread.title.should.equal(title)
            thread.description.should.equal(desc)
            thread.resources.should.equal(res)
            thread.nsfw.should.equal(nsfw)
            thread.state.should.equal("live")

            ws = ref.liveUpdates(object: LiveThreadListener() {
                override fun onUpdate(update: LiveWebSocketUpdate) {
                    updateEvents.add(update)
                }

                override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                    t?.printStackTrace()
                    t.should.be.`null`
                }

                override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                    println("closed ($code): $reason")
                }
            })
        }
    }

    describe("edit") {
        it("should overwrite existing settings") {
            initialized.should.be.`true`

            ref.edit(LiveThreadPatch(nsfw = false, title = "new title"))
            expectedEventTypes.add("settings")

            val thread = ref.about()
            // The endpoint should have changed the things we specified
            thread.nsfw.should.equal(false)
            thread.title.should.equal("new title")

            // ... and reset those we didn't
            thread.resources.should.be.empty
            thread.description.should.be.empty
        }
    }

    describe("postUpdate/strikeUpdate/deleteUpdate/latestUpdates") {
        fun latestUpdates() = ref.latestUpdates().limit(Paginator.RECOMMENDED_MAX_LIMIT).build().next()

        it("should create or modify a particular update") {
            initialized.should.be.`true`

            val body = "https://www.youtube.com/watch?v=xuCn8ux2gbs"
            ref.postUpdate(body)
            expectedEventTypes.addAll(listOf("update", "embeds_ready"))
            val updates = latestUpdates()
            updates.should.have.size.above(0)

            // We already posted one update, the original size is 1 minus the current size
            val originalSize = updates.size - 1

            val update = latestUpdates()[0]
            update.body.should.equal(body)
            update.stricken.should.be.`false`

            ref.strikeUpdate(update.fullName)
            latestUpdates()[0].stricken.should.be.`true`
            expectedEventTypes.add("strike")

            ref.deleteUpdate(update.fullName)
            latestUpdates().should.have.size(originalSize)
            expectedEventTypes.add("delete")
        }
    }

    afterGroup {
        if (initialized) {
            try {
                ref.close()
                ref.about().state.should.equal("complete")
                expectedEventTypes.add("complete")

                updateEvents
                    .map { it.type }
                    // "activity" events happen randomly and uncontrollably, don't include these in our assertions
                    .filter { it != "activity" }
                    .should.equal(expectedEventTypes)
            } finally {
                ws.close()
            }
        }
    }
})
