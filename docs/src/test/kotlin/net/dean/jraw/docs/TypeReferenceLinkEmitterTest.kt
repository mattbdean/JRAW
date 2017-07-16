package net.dean.jraw.docs

import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TypeReferenceLinkEmitterTest : Spek({
    val docLinkGen = JrawDocLinkGenerator()
    val emitter = TypeReferenceLinkEmitter(docLinkGen)

    describe("emitSpan") {
        var out = StringBuilder()

        it("should throw an exception when given a non-JRAW type") {
            expectException(IllegalStateException::class) {
                emitter.emitSpan(out, "@String")
            }
        }

        it("should generate a link when given a JRAW type") {
            emitter.emitSpan(out, "@RedditClient")
            out.toString().should.equal(docLinkGen.linkFor(RedditClient::class.java))
        }

        afterEachTest {
            out = StringBuilder()
        }
    }
})
