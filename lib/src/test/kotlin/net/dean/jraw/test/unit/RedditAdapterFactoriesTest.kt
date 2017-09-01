package net.dean.jraw.test.unit

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.winterbe.expekt.should
import net.dean.jraw.databind.*
import net.dean.jraw.models.Created
import net.dean.jraw.models.Listing
import net.dean.jraw.test.expectException
import net.dean.jraw.test.models.GenericParentType
import net.dean.jraw.test.models.Subclass1
import net.dean.jraw.test.models.Subclass2
import org.intellij.lang.annotations.Language
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class RedditAdapterFactoriesTest : Spek({
    val moshi = Moshi.Builder()
        .add(ModelAdapterFactory.create())
        .add(RedditModelAdapterFactory(mapOf(
            "subclass1" to Subclass1::class.java,
            "subclass2" to Subclass2::class.java
        )))
        .add(EnvelopedListAdapterFactory())
        .build()

    @Language("JSON")
    val SUBTYPE1_JSON = """
{
    "kind": "subclass1",
    "data": {
        "foo": 42
    }
}
"""


    it("should statically deserialize a concrete model") {
        val adapter = moshi.adapter<Subclass1>(Subclass1::class.java, Enveloped::class.java)
        val result = adapter.fromJson(SUBTYPE1_JSON)!!
        result.foo.should.equal(42)
    }

    it("should dynamically deserialize a concrete model") {
        val adapter = moshi.adapter<GenericParentType>(GenericParentType::class.java, DynamicEnveloped::class.java)
        val result = adapter.fromJson(SUBTYPE1_JSON)!!
        result.should.be.instanceof(Subclass1::class.java)
        (result as Subclass1).foo.should.equal(42)
    }

    it("should fail when given JSON data that doesn't conform to the given supertype") {
        // Subtype1 doesn't implement Created, this should fail
        val adapter = moshi.adapter<Created>(Created::class.java, DynamicEnveloped::class.java)
        expectException(IllegalArgumentException::class) {
            adapter.fromJson(SUBTYPE1_JSON)
        }
    }

    it("should statically deserialize a Listing") {
        @Language("JSON")
        val json = """
{
    "kind": "Listing",
    "data": {
        "after": "after",
        "children": [
            $SUBTYPE1_JSON
        ]
    }
}
"""
        val type = Types.newParameterizedType(Listing::class.java, Subclass1::class.java)
        val adapter = moshi.adapter<Listing<Subclass1>>(type, Enveloped::class.java)
        val result = adapter.fromJson(json)!!

        result.nextName.should.equal("after")
        result.should.have.size(1)
        result[0].foo.should.equal(42)
    }

    it("should dynamically deserialize a Listing") {
        @Language("JSON")
        val json = """
{
    "kind": "Listing",
    "data": {
        "after": "after",
        "children": [
            $SUBTYPE1_JSON,
            {
                "kind": "subclass2",
                "data": {
                    "baz": "three",
                    "qux": "four",
                    "replies": {
                        "kind": "Listing",
                        "data": {
                            "after": "after",
                            "children": [
                                $SUBTYPE1_JSON
                            ]
                        }
                    }
                }
            }
        ]
    }
}
"""
        val adapter = moshi.adapter<Listing<GenericParentType>>(Types.newParameterizedType(Listing::class.java, Any::class.java), DynamicEnveloped::class.java)
        val result = adapter.fromJson(json)!!

        result.nextName.should.equal("after")
        result.should.have.size(2)
        result[0].should.be.instanceof(Subclass1::class.java)
        result[1].should.be.instanceof(Subclass2::class.java)

        val first = result.children.first() as Subclass1
        first.foo.should.equal(42)

        val second = result.children[1] as Subclass2
        second.baz.should.equal("three")
        second.qux.should.equal("four")
        second.replies.should.have.size(1)
        second.replies[0].foo.should.equal(42)
    }
})
