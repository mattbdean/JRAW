package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.ApiException
import net.dean.jraw.test.CredentialsUtil.moderationSubreddit
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.expectException
import net.dean.jraw.test.randomName
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

class EmojiReferenceTest : Spek({
    describe("list") {
        it("should provide a list of emojis") {
            reddit.subreddit("pics").emoji().list().should.have.size.above(0)
        }
    }

    describe("upload/delete") {
        it("should make the emoji available to the subreddit") {
            val testEmoji = File(EmojiReferenceTest::class.java.getResource("/emoji.png").file)
            val emojiName = randomName()
            val sr = reddit.subreddit(moderationSubreddit)
            val ref = sr.emoji()
            val expectedNamespace = sr.about().fullName

            /** Query the emojis and find the one that we've uploaded */
            fun findEmoji() = ref.list().find { it.namespace == expectedNamespace && it.name == emojiName }

            // Sanity check
            testEmoji.isFile.should.be.`true`

            // Upload the Emoji
            ref.upload(testEmoji, emojiName)

            // Make sure it's here
            val uploadedEmoji = findEmoji()!!

            // Test delete() and clean up the emoji at the same time
            ref.delete(uploadedEmoji.name)

            // Make sure it was deleted
            findEmoji().should.be.`null`
        }
    }

    describe("delete") {
        it("should throw an ApiException when trying to delete an emoji that doesn\'t exist") {
            expectException(ApiException::class) {
                reddit.subreddit(moderationSubreddit).emoji().delete("foo")
            }
        }
    }
})
