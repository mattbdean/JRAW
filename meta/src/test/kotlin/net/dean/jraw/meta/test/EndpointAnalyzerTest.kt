package net.dean.jraw.meta.test

import com.winterbe.expekt.should
import net.dean.jraw.meta.EndpointAnalyzer
import net.dean.jraw.meta.ParsedEndpoint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class EndpointAnalyzerTest : Spek({
    it("should return non-null values for implemented endpoints") {
        val meta = EndpointAnalyzer.getFor(ParsedEndpoint(
            method = "GET",
            path = "/api/v1/me",
            oauthScope = "doesn't matter", redditDocLink = "doesn't matter", subredditPrefix = false
        ))
        meta.should.not.be.`null`

        // This method should originate from this library
        meta!!.implementation.declaringClass.`package`.name.should.startWith("net.dean.jraw")

        meta.sourceUrl.should.startWith("https://github.com/mattbdean/JRAW/tree/")
    }

    it("should return null for non-implemented endpoints") {
        EndpointAnalyzer.getFor(ParsedEndpoint("", "", "", "", false)).should.be.`null`
    }
})
