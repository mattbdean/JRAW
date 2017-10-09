package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.Endpoint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class EndpointTest : Spek({
    it("should have the correct features") {
        // This is mainly a sanity check/code coverage thing
        Endpoint.GET_ME.method.should.equal("GET")
        Endpoint.GET_ME.path.should.equal("/api/v1/me")
        Endpoint.GET_ME.scope.should.equal("identity")
    }
})
