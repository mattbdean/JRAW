package net.dean.jraw.test.unit

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.winterbe.expekt.should
import net.dean.jraw.databind.*
import net.dean.jraw.models.KindConstants
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Subreddit
import net.dean.jraw.models.internal.RedditModelEnvelope
import net.dean.jraw.test.expectException
import net.dean.jraw.test.models.Child
import net.dean.jraw.test.models.NonEnvelopedModel
import net.dean.jraw.test.models.OtherModel
import net.dean.jraw.test.models.Parent
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import kotlin.reflect.KClass

class RedditModelAdapterFactoryTest : Spek({
    /**
     * Create a Map<String, Class<Any>> where the keys are the lowercase simple names of each class and the values are
     * the Java classes.
     *
     * ```kt
     * registry(Subreddit::class) == mapOf("subreddit" to Subreddit::class.java)
     * ```
     */
    fun registry(vararg classes: KClass<*>) = classes
        .map { it.simpleName!!.toLowerCase() }
        .zip(classes.map { it.java })
        .toMap()

    fun factory(registry: Map<String, Class<*>> = mapOf()) = RedditModelAdapterFactory(registry)

    fun moshi(registry: Map<String, Class<*>> = mapOf()) = Moshi.Builder()
        .add(ModelAdapterFactory.create())
        .add(UnixDateAdapterFactory())
        .add(factory(registry))
        .build()

    fun childJson(kindValue: String = "child") = """
        |{
        |  "kind": "$kindValue",
        |  "data": { "a": "some value" }
        |}
    """.trimMargin("|")

    /**
     * Parses the given JSON into a Map<String, Any> and invokes the provided `check` function on it. Returns the Map.
     */
    fun checkJsonValue(json: String, check: (value: Map<String, Any>) -> Unit): Map<String, Any> {
        val adapter = moshi().adapter<Map<String, Any>>(
            Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))

        val jsonValue = adapter.fromJson(json)!!
        check(jsonValue)
        return jsonValue
    }

    it("should return null when not given @Enveloped") {
        val factory = factory(registry = mapOf())
        factory.create(Subreddit::class.java, annotations = setOf(), moshi = moshi())
            .should.be.`null`
    }

    it("should return a ListingAdapter when given a Listing type") {
        val moshi = moshi(registry = registry(Child::class))
        val adapter = moshi.adapter<Listing<Any>>(Types.newParameterizedType(Listing::class.java, Child::class.java), Enveloped::class.java)
        adapter.should.not.be.`null`
        adapter.should.be.an.instanceof(RedditModelAdapterFactory.ListingAdapter::class.java)
    }

    it("should return a StaticAdapter when the given type is marked with @RedditModel") {
        val moshi = moshi(registry = registry(Child::class))
        val adapter = moshi.adapter<Any>(Child::class.java, Enveloped::class.java)

        // Sanity check
        Child::class.java.isAnnotationPresent(RedditModel::class.java).should.be.`true`

        adapter.should.be.an.instanceof(RedditModelAdapterFactory.StaticAdapter::class.java)
        (adapter as RedditModelAdapterFactory.StaticAdapter).expectedKind.should.be.`null`
    }

    it("should return a StaticAdapter with a non-null expectedKind when the type is Subreddit") {
        val adapter = moshi(registry = registry(Subreddit::class))
            .adapter<Any>(Subreddit::class.java, Enveloped::class.java)

        adapter.should.be.an.instanceof(RedditModelAdapterFactory.StaticAdapter::class.java)
        (adapter as RedditModelAdapterFactory.StaticAdapter).expectedKind.should.equal(KindConstants.SUBREDDIT)
    }

    it("should return a DynamicAdapter when the given type is not marked with @RedditModel") {
        // Sanity check
        Parent::class.java.isAnnotationPresent(RedditModel::class.java).should.be.`false`

        val adapter = moshi(registry = registry(Parent::class))
            .adapter<Any>(Parent::class.java, Enveloped::class.java)

        adapter.should.be.an.instanceof(RedditModelAdapterFactory.DynamicAdapter::class.java)
        (adapter as RedditModelAdapterFactory.DynamicAdapter).upperBound.should.equal(Parent::class.java)
    }

    it("should return a different adapter when the class is marked with @RedditModel(enveloped = false)") {
        // Sanity check
        NonEnvelopedModel::class.java.getAnnotation(RedditModel::class.java).enveloped.should.be.`false`;

        val adapter = moshi(registry = registry(NonEnvelopedModel::class))
            .adapter<Any>(NonEnvelopedModel::class.java, Enveloped::class.java)

        // Should eventually delegate to ClassJsonAdapter. The "$2" refers to JsonAdapter.nonNull()
        adapter.javaClass.name.should.equal("com.squareup.moshi.JsonAdapter$2")
    }

    describe("StaticAdapter") {
        describe("toJson") {
            it("should throw an IllegalArgumentException if there is no JsonAdapter registered for the provided kind") {
                // Nothing in the registry for Child
                val moshi = moshi(registry = mapOf())

                val adapter = moshi.adapter<Any>(Child::class.java, Enveloped::class.java)
                    as RedditModelAdapterFactory.StaticAdapter

                expectException(IllegalArgumentException::class) {
                    adapter.toJson(Child("some value"))
                }
            }

            it("should serialize to a RedditModelEnvelope") {
                // Child is included in the registry, everything should be fine
                val moshi = moshi(registry = registry(Child::class))
                val original = Child("some value")
                val adapter = moshi.adapter<Any>(Child::class.java, Enveloped::class.java)
                    as RedditModelAdapterFactory.StaticAdapter
                val json = adapter.toJson(original)

                checkJsonValue(json) {
                    it.keys.should.equal(setOf("kind", "data"))
                    it["kind"].should.equal("child")
                    it["data"].should.equal(mapOf("a" to original.a))
                }
            }
        }

        describe("fromJson") {
            it("should not care about the value of 'kind' when expectedKind is null") {
                val json = """
                    |{
                    |  "kind": "value should not matter",
                    |  "data": { "a": "some value" }
                    |}
                """.trimMargin("|")

                val adapter = moshi(registry(Child::class)).adapter<Child>(Child::class.java, Enveloped::class.java)
                    as RedditModelAdapterFactory.StaticAdapter

                // Sanity check
                adapter.expectedKind.should.be.`null`

                adapter.fromJson(json).should.equal(Child("some value"))
            }

            it("should assert the value of 'kind' matches expectedKind when expectedKind is non-null") {
                val registry = registry(Child::class)

                // Create an adapter for RedditModelEnvelope<Child>
                val envelopeAdapter = moshi(registry).adapter<RedditModelEnvelope<*>>(
                    Types.newParameterizedType(RedditModelEnvelope::class.java, Child::class.java))

                val adapter = RedditModelAdapterFactory.StaticAdapter(
                    registry = registry(Child::class),
                    delegate = envelopeAdapter,
                    expectedKind = "child"
                )

                // This should be fine since the value of 'kind' matches expectedKind
                adapter.fromJson(childJson(kindValue = "child")).should.equal(Child("some value"))

                // This should cause a problem because 'kind' doesn't match expectedKind
                expectException(JsonDataException::class) {
                    adapter.fromJson(childJson(kindValue = "some other value"))
                }
            }
        }

        it("should be able to deserialize JSON created by toJson() without loss of meaning") {
            val original = Child("foo")

            val adapter = moshi(registry = registry(Child::class))
                .adapter<Any>(Child::class.java, Enveloped::class.java) as RedditModelAdapterFactory.StaticAdapter

            adapter.fromJson(adapter.toJson(original)).should.equal(original)
        }
    }

    describe("DynamicAdapter") {
        describe("toJson") {
            // TODO(mattbdean): https://github.com/mattbdean/JRAW/issues/237
            xit("should be able to dynamically serialize JSON")
        }

        describe("fromJson") {
            it("should throw an IllegalArgumentException if there is no JsonAdapter registered for the given kind") {
                val adapter = moshi(registry = mapOf())
                    .adapter<Parent>(Parent::class.java, Enveloped::class.java) as RedditModelAdapterFactory.DynamicAdapter

                val ex = expectException(IllegalArgumentException::class) {
                    adapter.fromJson(childJson())
                }

                ex.message.should.equal("No registered class for kind 'child'")
            }

            it("should throw a JsonDataException if 'kind' is missing, or an IllegalArgumentException if its value is not registered") {
                val adapter = moshi(registry = registry(Child::class))
                    .adapter<Parent>(Parent::class.java, Enveloped::class.java) as RedditModelAdapterFactory.DynamicAdapter

                val missingKindEx = expectException(JsonDataException::class) {
                    adapter.fromJson("""{ "data": { "a": "foo" } }""")
                }
                missingKindEx.message.should.equal("Expected value at '$.kind' to be non-null")

                val notRegisteredEx = expectException(IllegalArgumentException::class) {
                    adapter.fromJson("""{ "kind": "some value", "data": { "a": "foo" } }""")
                }
                notRegisteredEx.message.should.equal("No registered class for kind 'some value'")
            }

            it("should dynamically find a JsonAdapter based on the value of 'kind' and deserialize that value") {
                val adapter = moshi(registry = registry(Child::class))
                    .adapter<Parent>(Parent::class.java, Enveloped::class.java) as RedditModelAdapterFactory.DynamicAdapter

                adapter.fromJson("""{ "kind": "child", "data": { "a": "some value" } }""").should.equal(Child("some value"))
            }

            it("should ensure the deserialized type is the same as or is a subclass of the upper bound") {
                // OtherModel's superclass is Object, so trying to deserialize these guys as Parent objects
                // should cause a problem
                val adapter = moshi(registry = registry(OtherModel::class))
                    .adapter<Parent>(Parent::class.java, Enveloped::class.java) as RedditModelAdapterFactory.DynamicAdapter

                val ex = expectException(IllegalArgumentException::class) {
                    adapter.fromJson("""{ "kind": "othermodel", "data": { "a": "some value" } }""")
                }

                ex.message.should.equal("Expected ${Parent::class.java.name} to be assignable from ${OtherModel("some value")}")
            }
        }

        // TODO(mattbdean): https://github.com/mattbdean/JRAW/issues/237
        xit("should be able to deserialize JSON created by toJson() without loss of meaning") {
            val original = Child("foo")

            val adapter = moshi(registry = registry(Child::class))
                .adapter<Any>(Parent::class.java, Enveloped::class.java) as RedditModelAdapterFactory.DynamicAdapter

            adapter.fromJson(adapter.toJson(original)).should.equal(original)
        }
    }

    describe("ListingAdapter") {
        fun <T : Any> listingType(dataType: KClass<T>) = Types.newParameterizedType(Listing::class.java, dataType.java)

        describe("toJson") {
            it("should produce a JSON object with 'kind' and 'data' properties") {
                val original: Listing<Any> = Listing.create("next name", listOf(Child("some value")))
                val adapter = moshi(registry = registry(Child::class))
                    .adapter<Listing<Child>>(listingType(Child::class), Enveloped::class.java) as RedditModelAdapterFactory.ListingAdapter

                checkJsonValue(adapter.toJson(original)) {
                    it.keys.should.equal(setOf("kind", "data"))
                    it["kind"].should.equal(KindConstants.LISTING)
                    it["data"].should.be.instanceof(Map::class.java)

                    val data = it["data"] as Map<*, *>
                    data["after"].should.equal("next name")
                    val children = data["children"] as List<*>
                    children.should.have.size(1)

                    // The contents of 'children' is handled by the delegate adapter, no need to verify any more
                }
            }
        }

        describe("fromJson") {
            val adapter = moshi(registry = registry(Child::class))
                .adapter<Listing<Child>>(listingType(Child::class), Enveloped::class.java) as RedditModelAdapterFactory.ListingAdapter

            it("should throw an IllegalArgumentException if 'kind' is not 'Listing'") {
                val ex = expectException(IllegalArgumentException::class) {
                    adapter.fromJson("""{ "kind": "something", "data": {} }""")
                }

                ex.message.should.equal("Expected '${KindConstants.LISTING}' at $.kind, got 'something'")
            }

            it("should throw a JsonDataException if 'data' doesn't exist or its non-null") {
                val nullDataEx = expectException(JsonDataException::class) {
                    adapter.fromJson("""{ "kind": "Listing", "data": null }""")
                }

                nullDataEx.message.should.equal("Expected a non-null value at $.data")

                val noDataEx = expectException(JsonDataException::class) {
                    adapter.fromJson("""{ "kind": "Listing" }""")
                }
                noDataEx.message.should.equal("Expected a value at $.data")
            }

            it("should be able to deserialize Listings given valid JSON") {
                adapter.fromJson("""{ "kind": "Listing", "data": { "next": null, "children": [] } }""")
                    .should.equal(Listing.create(null, listOf()))
            }
        }

        it("should be able to deserialize JSON created by toJson() without loss of meaning") {
            val original: Listing<Any> = Listing.create("next name", listOf(Child("some value"), Child("another value")))

            val adapter = moshi(registry = registry(Child::class))
                .adapter<Listing<Child>>(listingType(Child::class), Enveloped::class.java) as RedditModelAdapterFactory.ListingAdapter

            adapter.fromJson(adapter.toJson(original)).should.equal(original)
        }
    }
})
