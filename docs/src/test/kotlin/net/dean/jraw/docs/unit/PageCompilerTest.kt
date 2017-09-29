package net.dean.jraw.docs.unit

import com.winterbe.expekt.should
import net.dean.jraw.docs.CodeSampleRef
import net.dean.jraw.docs.DocLinkGenerator
import net.dean.jraw.docs.PageCompiler
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates

class PageCompilerTest : Spek({
    val linkGen = DocLinkGenerator()
    var compiler: PageCompiler by Delegates.notNull()

    val codeSample = CodeSampleRef("Foo.bar", listOf("// hello, world", "int x = 42;"))

    beforeEachTest {
        compiler = PageCompiler(linkGen, listOf(codeSample))
    }

    describe("compile") {
        it("should replace [[foo]] links") {
            compiler.compile(listOf("[[@RedditClient]]"))
                .should.equal(listOf("[RedditClient](${linkGen.generate("RedditClient")})"))
        }

        it("should replace multiple [[foo]] links on one line") {
            val before = "[[@RedditClient]]"
            val after = "[RedditClient](${linkGen.generate("RedditClient")})"

            val amount = 3
            compiler.compile(listOf(before.repeat(amount))).should.equal(listOf(after.repeat(3)))
        }

        it("should replace code sample placeholders with their actual contents") {
            val expected = codeSample.content.toMutableList()

            // We're using GFM here
            expected.add(0, "```java")
            expected.add("```")

            compiler.compile(listOf("{{ Foo.bar }}")).should.equal(expected)
        }
    }
})

