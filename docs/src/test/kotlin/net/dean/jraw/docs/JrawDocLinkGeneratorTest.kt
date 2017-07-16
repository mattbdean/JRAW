package net.dean.jraw.docs

import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class JrawDocLinkGeneratorTest : Spek({
    describe("linkFor") {
        it("should generate an <a> tag for a class") {
            val docs = JrawDocLinkGenerator()
            docs.linkFor(RedditClient::class.java).should.equal(
                """<a href="${docs.base}net/dean/jraw/RedditClient.html" class="doc-link" title="Documentation for net.dean.jraw.RedditClient">RedditClient</a>"""
            )
        }
    }
})
