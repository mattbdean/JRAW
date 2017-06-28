package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.JrawUtils
import net.dean.jraw.test.expectException
import org.intellij.lang.annotations.Language
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class JrawUtilsTest : Spek({
    describe("parseUrlEncoded") {
        it("should return an empty map when an empty string is passed") {
            JrawUtils.parseUrlEncoded("").should.equal(emptyMap())
        }

        it("should return URL-decoded data") {
            // Normal strings
            JrawUtils.parseUrlEncoded("foo=bar&baz=qux").should.equal(mapOf("foo" to "bar", "baz" to "qux"))

            // URL-encoded strings
            val original = "$%!="
            val value = JrawUtils.urlEncode(original)
            JrawUtils.parseUrlEncoded("foo=$value").should.equal(mapOf("foo" to original))
        }

        it("should fail when no '=' is present") {
            expectException(IllegalArgumentException::class) {
                JrawUtils.parseUrlEncoded("foo")
            }
        }
    }

    describe("mapOf") {
        it("should return an empty map when given nothing") {
            JrawUtils.mapOf().should.equal(emptyMap())
        }

        it("should throw an error when given a odd amount of arguments") {
            expectException(IllegalArgumentException::class) {
                JrawUtils.mapOf("foo")
            }
        }

        it("should return a map of the keys and values") {
            JrawUtils.mapOf(
                "foo", "bar",
                "baz", "qux"
            ).should.equal(mapOf(
                "foo" to "bar",
                "baz" to "qux"
            ))
        }
    }

    describe("navigateJson") {
        @Language("JSON")
        val json = JrawUtils.jackson.readTree("""
{
  "foo": {
    "bar": [
      {
        "baz": "qux"
      }
    ]
  }
}
""")
        it("should return a JsonNode for for the described path") {
            JrawUtils.navigateJson(json, "foo", "bar", 0, "baz").asText().should.equal("qux")
        }

        it("should return the original node when given no path parameters") {
            JrawUtils.navigateJson(json).should.equal(json)
        }

        it("should fail when given an invalid path argument") {
            expectException(IllegalArgumentException::class) {
                JrawUtils.navigateJson(json, "other")
            }
        }

        it("should fail when given a non-String or int for a path") {
            expectException(IllegalArgumentException::class) {
                JrawUtils.navigateJson(json, 45.9)
            }
        }
    }
})
