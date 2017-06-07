import com.winterbe.expekt.should
import net.dean.jraw.meta.Endpoint
import net.dean.jraw.meta.EndpointParser
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates

class EndpointParserTest : Spek({
    describe("fetch") {
        val METHODS = listOf("GET", "POST", "PUT", "PATCH", "DELETE")
        var endpoints: List<Endpoint> by Delegates.notNull()

        beforeGroup {
            endpoints = EndpointParser().fetch()
        }

        it("should return a list of valid Endpoints") {
            // There are at least 100 endpoints in the reddit API
            endpoints.should.have.size.above(100)

            for ((method, path, oauthScope, redditDocLink) in endpoints) {
                METHODS.should.contain(method)

                // http://regexr.com/3g4ad
                path.should.match(Regex("(/[a-z{}_0-9]+)+"))

                // http://regexr.com/3g4a7
                oauthScope.should.match(Regex("[a-z]+"))

                // http://regexr.com/3g4ap
                redditDocLink.should.match(Regex("https://www\\.reddit\\.com/dev/api/oauth#[A-Z]+_[0-9a-z_{}:]+"))
            }
        }

        it("should turn colon path parameters into path bracket parameters") {
            // For example:   /api/mod/conversations/:conversation_id/archive
            // should become: /api/mod/conversations/{conversation_id}/archive
            endpoints.firstOrNull { it.path == "/api/mod/conversations/{conversation_id}/archive" }.should.not.equal(null)
        }

        fun ensureFound(path: String) {
            endpoints.firstOrNull { it.path == path }.should.not.equal(null)
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
    }
})
