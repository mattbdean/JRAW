package net.dean.jraw.databind

import com.squareup.moshi.*
import net.dean.jraw.models.KindConstants
import net.dean.jraw.models.Listing
import net.dean.jraw.models.internal.RedditModelEnvelope
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Creates JsonAdapters for a class annotated with [RedditModel].
 *
 * This class assumes that the data is encapsulated in an envelope like this:
 *
 * ```json
 * {
 *   "kind": "<kind>",
 *   "data": { ... }
 * }
 * ```
 *
 * The [Enveloped] annotation must be specified in order for this adapter factory to produce anything.
 *
 * ```kt
 * val adapter = moshi.adapter<Something>(Something::class.java, Enveloped::class.java)
 * val something = adapter.fromJson(json)
 * ```
 *
 * If the target type does NOT have the `@RedditModel` annotation, it will be deserialized dynamically. For example, if
 * the JSON contains either a `Foo` or a `Bar`, we can specify their closest comment parent instead of either `Foo` or
 * `Bar`.
 *
 * ```kt
 * // Get an adapter that can deserialize boths Foos and Bars
 * moshi.adapter<Parent>(Parent::class.java, Enveloped::class.java)
 * ```
 *
 * Dynamic deserialization works like this:
 *
 *  1. Write the JSON value into a "simple" type (e.g. a Map)
 *  2. Lookup the value of the "kind" node
 *  3. Determine the correct concrete class by looking up the kind in the [registry]
 *  4. Transform the intermediate JSON (the Map) into an instance of that class
 *
 * Keep in mind that dynamic deserialization is a bit hacky and is probably slower than static deserialization.
 */
class RedditModelAdapterFactory(
    /**
     * A Map of kinds (the value of the 'kind' node) to the concrete classes they represent. Not necessary if only
     * deserializing statically. Adding [Listing] to this class may cause problems.
     */
    private val registry: Map<String, Class<*>>
) : JsonAdapter.Factory {
    /** @inheritDoc */
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
        // Require @Enveloped
        val delegateAnnotations = Types.nextAnnotations(annotations, Enveloped::class.java) ?: return null

        val rawType = Types.getRawType(type)

        // Special handling for Listings
        if (rawType == Listing::class.java) {
            val childType = (type as ParameterizedType).actualTypeArguments.first()
            // Assume children are enveloped
            val delegate = moshi.adapter<Any>(childType, Enveloped::class.java).nullSafe()
            return ListingAdapter(delegate)
        }

        val isRedditModel = rawType.isAnnotationPresent(RedditModel::class.java)

        return if (isRedditModel) {
            // Static JsonAdapter
            val enveloped = rawType.getAnnotation(RedditModel::class.java).enveloped
            return if (enveloped) {
                val actualType = Types.newParameterizedType(RedditModelEnvelope::class.java, type)
                val delegate = moshi.adapter<RedditModelEnvelope<*>>(actualType).nullSafe()
                StaticAdapter(registry, delegate)
            } else {
                moshi.nextAdapter<Any>(this, type, delegateAnnotations).nullSafe()
            }
        } else {
            // Dynamic JsonAdapter
            DynamicNonGenericModelAdapter(registry, moshi, rawType)
        }
    }

    /**
     * Statically (normally) deserializes some JSON value into a concrete class. All generic types must be resolved
     * beforehand.
     */
    private class StaticAdapter(
        private val registry: Map<String, Class<*>>,
        private val delegate: JsonAdapter<RedditModelEnvelope<*>>
    ) : JsonAdapter<Any>() {
        override fun toJson(writer: JsonWriter, value: Any?) {
            if (value == null) {
                writer.nullValue()
                return
            }

            // Reverse lookup the actual value of 'kind' from the registry
            var actualKind: String? = null
            for ((kind, clazz) in registry)
                if (clazz == value.javaClass)
                    actualKind = kind
            if (actualKind == null)
                throw IllegalArgumentException("No registered kind for Class '${value.javaClass}'")
            delegate.toJson(writer, RedditModelEnvelope.create(actualKind, value))
        }

        override fun fromJson(reader: JsonReader): Any? {
            return delegate.fromJson(reader)?.data ?: return null
        }
    }

    private abstract class DynamicAdapter<T>(
        protected val registry: Map<String, Class<*>>,
        protected val moshi: Moshi,
        protected val upperBound: Class<*>
    ) : JsonAdapter<T>() {
        override fun toJson(writer: JsonWriter?, value: T?) {
            throw UnsupportedOperationException("Serializing dynamic models aren't supported right now")
        }

        /**
         * Asserts that the given object is not null and the same class or a subclass of [upperBound]. Returns the value
         * after the check.
         */
        protected fun ensureInBounds(obj: Any?): Any {
            if (!upperBound.isAssignableFrom(obj!!.javaClass))
                throw IllegalArgumentException("Expected ${upperBound.name} to be assignable from $obj")
            return obj
        }
    }

    private class DynamicNonGenericModelAdapter(registry: Map<String, Class<*>>, moshi: Moshi, upperBound: Class<*>) :
        DynamicAdapter<Any>(registry, moshi, upperBound) {

        override fun fromJson(reader: JsonReader): Any? {
            val json = expectType<Map<String, Any>>(reader.readJsonValue(), "<root>")

            val kind = expectType<String>(json["kind"], "kind")

            val clazz = registry[kind] ?:
                throw IllegalArgumentException("No registered class for kind '$kind'")

            val envelopeType = Types.newParameterizedType(RedditModelEnvelope::class.java, clazz)
            val adapter = moshi.adapter<RedditModelEnvelope<*>>(envelopeType)
            val result = adapter.fromJsonValue(json)!!
            return ensureInBounds(result.data)
        }
    }

    private class ListingAdapter(val childrenDelegate: JsonAdapter<Any>) : JsonAdapter<Listing<Any>>() {
        override fun fromJson(reader: JsonReader): Listing<Any>? {
            // Assume that the JSON is enveloped, we have to strip that away and then parse the listing
            reader.beginObject()
            var listing: Listing<Any>? = null
            while (reader.hasNext()) {
                when (reader.selectName(envelopeOptions)) {
                    0 -> {
                        // "kind"
                        if (reader.nextString() != KindConstants.LISTING)
                            throw IllegalArgumentException("Expecting kind to be listing at ${reader.path}")
                    }
                    1 -> {
                        // "data"
                        listing = readListing(reader)
                    }
                    -1 -> {
                        // Unknown, skip it
                        reader.nextName()
                        reader.skipValue()
                    }
                }
            }
            reader.endObject()

            return listing
        }

        private fun readListing(reader: JsonReader): Listing<Any> {
            var after: String? = null
            val children: MutableList<Any> = ArrayList()

            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.selectName(listingOptions)) {
                    0 -> {
                        // "after"
                        after = if (reader.peek() == JsonReader.Token.NULL) reader.nextNull() else reader.nextString()
                    }
                    1 -> {
                        // "data"
                        reader.beginArray()
                        while (reader.hasNext())
                            children.add(childrenDelegate.fromJson(reader)!!)
                        reader.endArray()
                    }
                    -1 -> {
                        // Unknown, skip it
                        reader.nextName()
                        reader.skipValue()
                    }
                }
            }
            reader.endObject()
            return Listing.create(after, children)
        }

        override fun toJson(writer: JsonWriter, value: Listing<Any>?) {
            TODO("not implemented")
        }

        companion object {
            private val envelopeOptions = JsonReader.Options.of("kind", "data")
            private val listingOptions = JsonReader.Options.of("after", "children")
        }
    }

    /** */
    companion object {
        private inline fun <reified T> expectType(obj: Any?, name: String): T {
            if (obj == null)
                throw IllegalArgumentException("Expected $name to be non-null")
            return obj as? T ?:
                throw IllegalArgumentException("Expected $name to be a ${T::class.java.name}, was ${obj::class.java.name}")
        }
    }
}
