package net.dean.jraw.docs

import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import net.dean.jraw.http.UserAgent
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class CodeBlockEmitterTest : Spek({
    var out = StringBuilder()
    val docProvider = JrawDocLinkGenerator()

    fun compareNoNewlines(expected: String) = out.toString().replace("\n", "").should.equal(expected.replace("\n", ""))

    it("should output an HTML code block with no language") {
        val emitter = CodeBlockEmitter(emptyList(), docProvider)
        emitter.emitBlock(out, mutableListOf("First line", "Second line"), "")
        compareNoNewlines(
"""<div class="code-container"><pre><code class="nohighlight">
First line
Second line
</code></pre></div>
"""
        )
    }

    it("should recognize different languages") {
        val emitter = CodeBlockEmitter(emptyList(), docProvider)
        emitter.emitBlock(out, mutableListOf("stuff"), "some-language")
        compareNoNewlines(
"""<div class="code-container"><pre><code class="some-language">
stuff
</code></pre></div>
"""
        )
    }

    it("should insert code samples when requested") {
        val emitter = CodeBlockEmitter(listOf(CodeSampleRef("Sample.name", listOf("String foo;", "String bar;"))), docProvider)
        emitter.emitBlock(out, mutableListOf("_"), "@Sample.name")
        compareNoNewlines(
"""<div class="code-container"><pre><code class="java">
String foo;
String bar;
</code></pre></div>
"""
        )
    }

    it("should fail to insert a code sample when it can't be found") {
        val emitter = CodeBlockEmitter(emptyList(), docProvider)
        expectException(IllegalStateException::class) {
            emitter.emitBlock(out, mutableListOf("_"), "@Sample.name")
        }
    }

    it("should insert a link to the Javadoc when given types from the main project") {
        val emitter = CodeBlockEmitter(listOf(CodeSampleRef("Sample.name", listOf(
            "RedditClient r = null;",
            "UserAgent ua = new UserAgent(\"something\");"
        ))), docProvider)
        emitter.emitBlock(out, mutableListOf("_"), "@Sample.name")
        val userAgentLink = docProvider.linkFor(UserAgent::class.java)
        compareNoNewlines(
"""<div class="code-container"><pre><code class="java">
${docProvider.linkFor(RedditClient::class.java)} r = null;
$userAgentLink ua = new $userAgentLink("something");
</code></pre></div>
"""
        )
    }

    afterEachTest {
        out = StringBuilder()
    }
})

