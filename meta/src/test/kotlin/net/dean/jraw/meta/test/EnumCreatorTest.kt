package net.dean.jraw.meta.test

import com.winterbe.expekt.should
import net.dean.jraw.meta.EndpointMeta
import net.dean.jraw.meta.EndpointOverview
import net.dean.jraw.meta.EnumCreator
import net.dean.jraw.meta.EnumCreator.Companion.SUBREDDIT_PREFIX_CONSTANT_NAME
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class EnumCreatorTest : Spek({
    val overview = EndpointOverview(listOf(
        EndpointMeta(
            "GET", "/api/v1/foo/{bar}",
            oauthScope = "fooscope",
            redditDocLink = "<reddit doc url>",
            subredditPrefix = false
        ),
        EndpointMeta(
            "POST", "/api/v1/foo/{bar}",
            oauthScope = "fooscope",
            redditDocLink = "<reddit doc url>",
            subredditPrefix = true
        )
    ))

    it("should generate an enum with unique identifiers") {
        val out = StringBuilder()
        EnumCreator(overview).writeTo(out)

        val identifiers = out.toString().split("\n").filter {
            it.trim().matches(Regex("[A-Z_]{3}+.*?[,;]"))
        }.map { it.trim() }

        identifiers.should.have.size(overview.endpoints.size)
        identifiers[0].should.equal("""GET_FOO_BAR("GET", "/api/v1/foo/{bar}", "fooscope"),""")
        identifiers[1].should.equal("""POST_FOO_BAR("POST", $SUBREDDIT_PREFIX_CONSTANT_NAME + "/api/v1/foo/{bar}", "fooscope");""")
    }

    it("should generate compilable code") {
        ensureCompilable { EnumCreator(overview).writeTo(it) }
    }
})
