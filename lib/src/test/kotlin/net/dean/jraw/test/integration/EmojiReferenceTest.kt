package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class EmojiReferenceTest : Spek({
    describe("list") {
        it("should provide a list of emojis") {
            reddit.subreddit("pics").emoji().list().should.have.size.above(0)
        }
    }
})
