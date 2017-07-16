package net.dean.jraw.docs

import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class CodeBlockEmitterTest : Spek({
    var out = StringBuilder()

    it("should output an HTML code block with no language") {
        val emitter = CodeBlockEmitter(emptyList())
        emitter.emitBlock(out, mutableListOf("First line", "Second line"), "")
        out.toString().should.equal(
"""<div class="code-container"><pre><code class="nohighlight">
First line
Second line
</code></pre></div>
"""
        )
    }

    it("should recognize different languages") {
        val emitter = CodeBlockEmitter(emptyList())
        emitter.emitBlock(out, mutableListOf("stuff"), "some-language")
        out.toString().should.equal(
"""<div class="code-container"><pre><code class="some-language">
stuff
</code></pre></div>
"""
        )
    }

    it("should insert code samples when requested") {
        val emitter = CodeBlockEmitter(listOf(CodeSampleRef("Sample.name", listOf("foo", "bar"))))
        emitter.emitBlock(out, mutableListOf("_"), "@Sample.name")
        out.toString().should.equal(
"""<div class="code-container"><pre><code class="java">
foo
bar
</code></pre></div>
"""
        )

    }

    it("should fail to insert a code sample when it can't be found") {
        val emitter = CodeBlockEmitter(emptyList())
        expectException(IllegalStateException::class) {
            emitter.emitBlock(out, mutableListOf("_"), "@Sample.name")
        }
    }

    afterEachTest {
        out = StringBuilder()
    }
})

