package net.dean.jraw.meta.test

import com.winterbe.expekt.should
import net.dean.jraw.Endpoint
import net.dean.jraw.meta.EndpointAnalyzer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class EndpointAnalyzerTest : Spek({
    it("should return non-null values for implemented endpoints") {
        val meta = EndpointAnalyzer.getFor(Endpoint.GET_ME)
        meta.should.not.be.`null`

        // This method should originate from this library
        meta!!.implementation.declaringClass.`package`.name.should.startWith("net.dean.jraw")

        // There's no way we can really test this
        meta.sourceLine.should.be.above(0)
    }

    it("should return null for non-implemented endpoints") {
        EndpointAnalyzer.getFor(Endpoint.GET_MOD_CONVERSATIONS_CONVERSATION_ID_USER).should.be.`null`
    }
})
