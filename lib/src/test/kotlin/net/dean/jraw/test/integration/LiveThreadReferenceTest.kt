package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.LiveThreadPatch
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.references.LiveThreadReference
import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates

class LiveThreadReferenceTest : Spek({
    val title = "test"
    val desc = "test description"
    val res = "test resources"
    val nsfw = true

    var ref: LiveThreadReference by Delegates.notNull()

    // Since creating live threads is ratelimited, we have to structure the test in such a way that complies with its
    // livecycle. the beforeGroup and afterGroup hooks are used instead of it() blocks since we can't guarantee that one
    // test will execute before the other.

    beforeGroup {
        ref = reddit.me().createLiveThread(LiveThreadPatch.Builder()
            .title("test")
            .description(desc)
            .resources(res)
            .nsfw(nsfw)
            .build())

        val thread = ref.about()
        thread.title.should.equal(title)
        thread.description.should.equal(desc)
        thread.resources.should.equal(res)
        thread.nsfw.should.equal(nsfw)
        thread.state.should.equal("live")
    }

    describe("edit") {
        it("should overwrite existing settings") {
            ref.edit(LiveThreadPatch(nsfw = false, title = "new title"))
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
            ref.postUpdate("test")
            val updates = latestUpdates()
            updates.should.have.size.above(0)

            // We already posted one update, the original size is 1 minus the current size
            val originalSize = updates.size - 1

            val update = latestUpdates()[0]
            update.body.should.equal("test")
            update.stricken.should.be.`false`

            ref.strikeUpdate(update.fullName)
            latestUpdates()[0].stricken.should.be.`true`

            ref.deleteUpdate(update.fullName)
            latestUpdates().should.have.size(originalSize)
        }
    }

    afterGroup {
        ref.close()
        ref.about().state.should.equal("complete")
    }
})
