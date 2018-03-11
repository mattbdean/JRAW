package net.dean.jraw.test.unit

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.winterbe.expekt.should
import net.dean.jraw.databind.Enveloped
import net.dean.jraw.databind.ModelAdapterFactory
import net.dean.jraw.databind.RedditModelAdapterFactory
import net.dean.jraw.models.Listing
import net.dean.jraw.test.models.Child
import net.dean.jraw.test.models.NonEnvelopedChild
import net.dean.jraw.test.models.Parent
import org.intellij.lang.annotations.Language
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.lang.reflect.Type
import kotlin.reflect.KClass

private val moshi = Moshi.Builder()
    .add(RedditModelAdapterFactory(mapOf(
        "child" to Child::class.java
    )))
    .add(ModelAdapterFactory.create())
    .build()

private inline fun <reified T> envelopedAdapter(type: Type = T::class.java) =
    moshi.adapter<T>(type, Enveloped::class.java)

class RedditModelAdapterFactoryTest : Spek({
    fun listingType(clazz: KClass<*>): Type = Types.newParameterizedType(Listing::class.java, clazz.java)

    it("should statically deserialize a simple value") {
        val adapter = envelopedAdapter<Child>()
        adapter.fromJson(childJson).should.equal(childValue)
    }

    it("should dynamically deserialize a simple value") {
        val adapter = envelopedAdapter<Parent>()
        adapter.fromJson(childJson).should.equal(childValue)
    }

    it("should statically deserialize a Listing") {
        val adapter = envelopedAdapter<Listing<Child>>(listingType(Child::class))
        adapter.fromJson(listingJson).should.equal(listingValue)
    }

    it("should dynamically deserialize a Listing") {
        val adapter = envelopedAdapter<Listing<Parent>>(listingType(Parent::class))
        adapter.fromJson(listingJson).should.equal(listingValue)
    }

    it("should statically deserialize a Listing with non-enveloped children") {
        val adapter = envelopedAdapter<Listing<NonEnvelopedChild>>(listingType(NonEnvelopedChild::class))
        adapter.fromJson(nonEnvelopedListingJson).should.equal(nonEnvelopedListingValue)
    }

    it("should be able to serialize/deserialize a Listing without loss of meaning") {
        val adapter = envelopedAdapter<Listing<Child>>(listingType(Child::class))
        // Parse the JSON from our in-code string
        val round1 = adapter.fromJson(listingJson)

        // Serialize and immediately deserialize the object to see if it stayed the same
        val round2 = adapter.fromJson(adapter.toJson(round1))

        round1.should.equal(round2)
    }
})

@Language("JSON") private val childJson = """{ "kind": "child", "data": { "a": "foo" } }"""
private val childValue = Child("foo")

@Language("JSON") private val listingJson =
"""{
    "kind": "Listing",
    "data": {
        "after": "abc123",
        "children": [
            { "kind": "child", "data": { "a": "foo" } },
            { "kind": "child", "data": { "a": "bar" } }
        ]
    }
}"""
private val listingValue = Listing.create("abc123", listOf(Child("foo"), Child("bar")))

@Language("JSON") private val nonEnvelopedListingJson =
"""{
    "kind": "Listing",
    "data": {
        "after": "abc123",
        "children": [
            { "a": "foo" },
            { "a": "bar" }
        ]
    }
}"""
private val nonEnvelopedListingValue = Listing.create("abc123", listOf(NonEnvelopedChild("foo"), NonEnvelopedChild("bar")))
