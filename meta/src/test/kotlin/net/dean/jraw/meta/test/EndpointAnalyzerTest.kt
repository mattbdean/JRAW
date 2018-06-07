package net.dean.jraw.meta.test

import com.winterbe.expekt.should
import net.dean.jraw.meta.EndpointAnalyzer
import net.dean.jraw.meta.EndpointOverview
import net.dean.jraw.meta.ImplementationStatus
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates

class EndpointAnalyzerTest : Spek({
    describe("fetch") {
        val httpMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE")
        var overview: EndpointOverview by Delegates.notNull()

        beforeGroup {
            overview = EndpointAnalyzer().fetch()
        }

        it("should return a list of valid Endpoints") {
            // There are at least 100 endpoints in the reddit API
            overview.endpoints.should.have.size.above(100)

            for (endpoint in overview.endpoints) {
                httpMethods.should.contain(endpoint.method)

                // http://regexr.com/3g4ad
                endpoint.path.should.match(Regex("(/[a-z{}_0-9.]+)+"))

                // http://regexr.com/3g4a7
                endpoint.oauthScope.should.match(Regex("[a-z]+"))

                // http://regexr.com/3g4ap
                endpoint.redditDocLink.should.match(Regex("https://www\\.reddit\\.com/dev/api/oauth#[A-Z]+_[0-9a-z_{}:.]+"))
            }
        }

        it("should turn colon path parameters into path bracket parameters") {
            // For example:   /api/mod/conversations/:conversation_id/archive
            // should become: /api/mod/conversations/{conversation_id}/archive
            overview.endpoints.firstOrNull { it.path == "/api/mod/conversations/{conversation_id}/archive" }.should.not.equal(null)
        }

        fun ensureFound(path: String) {
            overview.endpoints.firstOrNull { it.path == path }.should.not.equal(null)
        }

        it("should preserve path variables") {
            // Single out these two
            ensureFound("/user/{username}/{where}")
            ensureFound("/api/v1/me/friends/{username}")
        }

        it("should preserve underscores") {
            ensureFound("/api/accept_moderator_invite")
            ensureFound("/api/read_all_messages")
        }

        it("should only replace the proper instance of the path parameter keyword") {
            // Instead of /api/live/{thread}/close_{thread}
            ensureFound("/api/live/{thread}/close_thread")
        }
    }

    describe("implDetails") {
        it("should return a non-null method and sourceUrl values for implemented endpoints") {
            val meta = EndpointAnalyzer().implDetails("GET", "/api/v1/me")
            meta.method.should.not.be.`null`
            meta.sourceUrl.should.not.be.`null`
            meta.status.should.equal(ImplementationStatus.IMPLEMENTED)

            // This method should originate from this library
            meta.method!!.declaringClass.`package`.name.should.startWith("net.dean.jraw")

            meta.sourceUrl.should.startWith("https://github.com/mattbdean/JRAW/tree/")
        }

        it("should return a null method and sourceUrl for non-implemented endpoints") {
            val meta = EndpointAnalyzer().implDetails("GET", "/api/v1/foo")
            meta.method.should.be.`null`
            meta.sourceUrl.should.be.`null`
            meta.status.should.equal(ImplementationStatus.PLANNED)
        }
    }
})
