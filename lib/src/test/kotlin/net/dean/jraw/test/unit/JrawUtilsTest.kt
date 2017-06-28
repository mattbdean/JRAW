package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.JrawUtils
import net.dean.jraw.test.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class JrawUtilsTest : Spek({
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
})
