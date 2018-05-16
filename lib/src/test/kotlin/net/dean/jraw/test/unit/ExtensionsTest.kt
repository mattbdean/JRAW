package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.addQueryParameters
import net.dean.jraw.filterValuesNotNull
import okhttp3.HttpUrl
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ExtensionsTest : Spek({
    describe("Map.filterValuesNotNull") {
        it("should filter out any entries with null values") {
            mapOf(
                "foo" to null,
                "bar" to "baz",
                "qux" to null
            ).filterValuesNotNull().should.equal(mapOf(
                "bar" to "baz"
            ))
        }

        it("should leave the map as-is if there are no null values") {
            val map = mapOf(
                "foo" to "bar",
                "baz" to "qux"
            )

            map.filterValuesNotNull().should.equal(map)
        }

        it("should return a new copy of the map if nothing was modified") {
            val map = mapOf("foo" to "bar")
            (map.filterValuesNotNull() === map).should.be.`false`
        }

        it("should do nothing with an empty map") {
            mapOf<Any, Any?>().filterValuesNotNull().should.equal(mapOf())
        }
    }

    describe("HttpUrl.addQueryParameters") {
        it("should only add non-null values") {
            val b = HttpUrl.parse("https://example.com")!!.newBuilder()

            val query = mapOf(
                "non_null" to "foo",
                "null_val" to null
            )

            val finalUrl = b.addQueryParameters(query).build()

            finalUrl.queryParameter("non_null").should.equal("foo")
            finalUrl.queryParameter("null_val").should.be.`null`
        }

        it("should add, not just set") {
            val url = HttpUrl.parse("https://example.com?non_null=foo")!!
                .newBuilder()
                .addQueryParameters(mapOf("non_null" to "bar"))
                .build()

            url.queryParameterValues("non_null").should.equal(listOf("foo", "bar"))
        }
    }
})
