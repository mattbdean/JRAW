package net.dean.jraw.databind

import com.squareup.moshi.*
import net.dean.jraw.models.KindConstants
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Message
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
 * Note that the [Enveloped] or [DynamicEnveloped] annotation must be specified in order for this adapter factory to
 * produce anything.
 *
 * ```kt
 * val adapter = moshi.adapter<Something>(Something::class.java, Enveloped::class.java)
 * val something = adapter.fromJson(json)
 * ```
 *
 * Use [Enveloped] when the exact type is known (e.g. `Subreddit`) and use [DynamicEnveloped] when it is not. For
 * example, a Listing for comment replies can contain both [net.dean.jraw.models.Comment] objects and
 * [net.dean.jraw.models.MoreChildren] objects. They both share a common interface,
 * [net.dean.jraw.models.NestedIdentifiable]. Logically, we would assume that we could just get an adapter like this:
 *
 * ```kt
 * moshi.adapter<NestedIdentifiable>(NestedIdentifiable::class.java, Enveloped::class.java)
 * ```
 *
 * However, this fails because Moshi doesn't know how to deserialize a NestedIdentifiable. To fix this, we use
 * [DynamicEnveloped] instead:
 *
 * ```kt
 * moshi.adapter<NestedIdentifiable>(NestedIdentifiable::class.java, DynamicEnveloped::class.java)
 * ```
 *
 * Dynamic deserialization works like this:
 *
 *  1. Write the JSON value into a "simple" type (e.g. a Map)
 *  2. Lookup the value of the "kind" node
 *  3. Determine the correct concrete class by looking up the kind in the [registry]
 *  4. Transform the intermediate JSON (the Map) into an instance of that class
 *
 * Note that dynamic deserialization is a bit hacky and is probably slower than static deserialization.
 *
 * Note that Messages MUST be deserialized dynamically to work properly.
 */
class RedditModelAdapterFactory(
    /**
     * A Map of kinds (the value of the 'kind' node) to the concrete classes they represent. Not necessary if only
     * deserializing statically. Adding [Listing] to this class may cause problems.
     */
    private val registry: Map<String, Class<*>>
) : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val otherAnnotations = Types.nextAnnotations(annotations, DynamicEnveloped::class.java)

        val rawType = Types.getRawType(type)

        // The user has requested that we deserialize the data dynamically
        if (otherAnnotations != null) {
            // Since the user has requested a dynamic interpretation of the JSON, `type` is NOT the concrete type but
            // rather the superclass of all objects to be serialized by the adapter
            if (rawType == Listing::class.java) {
                // If we have Listing<T>, then T is the upper bound for all children
                val upperBound = Types.getRawType((type as ParameterizedType).actualTypeArguments.first())
                return DynamicListingAdapter(registry, moshi, upperBound,
                    childrenAreEnveloped = upperBound.isAnnotationPresent(RedditModel::class.java) &&
                        upperBound.getAnnotation(RedditModel::class.java).enveloped)
            }

            // Messages are handled a bit differently in the JSON
            if (rawType == Message::class.java)
                return MessageAdapter(moshi)

            // Assume an easily-deserialized model
            return DynamicNonGenericModelAdapter(registry, moshi, rawType)
        }

        // If we're still here, the user knows the type represented by the JSON, make sure we have the @RedditModel
        // annotation
        if (!rawType.isAnnotationPresent(RedditModel::class.java)) return null

        // Make sure the type has the @Enveloped annotation, then return a new Set with it excluded
        val delegateAnnotations = Types.nextAnnotations(annotations, Enveloped::class.java) ?: return null

        // Create a type for RedditModelEnvelope<type>
        val envelope = Types.newParameterizedType(RedditModelEnvelope::class.java, type)
        val delegate = moshi.adapter<RedditModelEnvelope<*>>(envelope, delegateAnnotations)

        return StaticAdapter(registry, delegate).nullSafe()
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

        override fun fromJson(reader: JsonReader?): Any? {
            return delegate.fromJson(reader)?.data ?: return null
        }
    }

    private abstract class DynamicAdapter<T>(
        protected val registry: Map<String, Class<*>>,
        protected val moshi: Moshi,
        protected val upperBound: Class<*>
    ) : JsonAdapter<T>() {
        override fun toJson(writer: JsonWriter?, value: T?) {
            throw IllegalArgumentException("Serializing dynamic models aren't supported right now")
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

    /**
     * This class exists because the reddit API lies to us when we query for messages. For every other model the value
     * of the "kind" node dictates the exact shape the "data" node has. Messages are a bit different: messages can
     * either be a "t4" (message) OR a "t1" (comment). To make matters worse, a message with a kind of "t1" does not
     * have the same shape as a normal comment — **it has the shape of a `t4`**.
     *
     * This class attempts to parse any JSON value into a Message, regardless of kind.
     */
    private class MessageAdapter(moshi: Moshi) : DynamicAdapter<Message>(mapOf(), moshi, Message::class.java) {
        override fun fromJson(reader: JsonReader): Message? {
            val json = expectType<Map<String, Any>>(reader.readJsonValue(), "<root>")

            val envelopeType = Types.newParameterizedType(RedditModelEnvelope::class.java, Message::class.java)
            val adapter = moshi.adapter<RedditModelEnvelope<*>>(envelopeType)
            val result = adapter.fromJsonValue(json)!!
            return ensureInBounds(result.data) as Message
        }
    }

    /**
     * Dynamically reads a Listing structure and ensures that all children are the same class or a subclass of
     * [upperBound].
     */
    private class DynamicListingAdapter(
        registry: Map<String, Class<*>>,
        moshi: Moshi,
        upperBound: Class<*>,
        val childrenAreEnveloped: Boolean
    ) : DynamicAdapter<Listing<Any>>(registry, moshi, upperBound) {

        override fun fromJson(reader: JsonReader): Listing<Any>? {
            val root = reader.readJsonValue()
            if (root is String)
                // See RepliesAdapterFactory for an explanation
                return Listing.empty()
            val json = root as? Map<*, *> ?:
                throw IllegalArgumentException("Expected an object at ${reader.path}")

            val rootKind = expectType<String>(json["kind"], "kind")
            if (rootKind != KindConstants.LISTING)
                throw IllegalArgumentException("Expected kind to be '${KindConstants.LISTING}', was '$rootKind'")

            val data = expectType<Map<String, Any>>(json["data"], "data")
            val after = data["after"] as String?
            val children = expectType<List<Any>>(data["children"], "children")

            val mapped = children.mapIndexed { index, it ->
                val childRoot = expectType<Map<String, Any>>(it, "children[$index]")

                if (childrenAreEnveloped) {
                    val childKind = expectType<String>(childRoot["kind"], "childKind")
                    val clazz = registry[childKind] ?:
                        throw IllegalArgumentException("No registered class for kind '$childKind'")

                    val envelopeType = Types.newParameterizedType(RedditModelEnvelope::class.java, clazz)
                    val adapter = moshi.adapter<RedditModelEnvelope<*>>(envelopeType)

                    ensureInBounds(adapter.fromJsonValue(childRoot)?.data)
                } else {
                    // No type information included in the JSON, we have to assume the type is upperBound
                    val adapter = moshi.adapter<Any>(upperBound)
                    ensureInBounds(adapter.fromJsonValue(childRoot))
                }
            }

            return Listing.create(after, mapped)
        }
    }

    companion object {
        private inline fun <reified T> expectType(obj: Any?, name: String): T {
            if (obj == null)
                throw IllegalArgumentException("Expected $name to be non-null")
            return obj as? T ?:
                throw IllegalArgumentException("Expected $name to be a ${T::class.java.name}, was ${obj::class.java.name}")
        }
    }
}
