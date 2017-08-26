package net.dean.jraw.databind

import com.squareup.moshi.*
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
 * The created adapter will ensure that the value of the `kind` node and [RedditModel.kind] match exactly and then
 * return the contents of the `data` node.
 */
class RedditModelJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        // Make sure we have the @RedditModel annotation
        if (!rawType.isAnnotationPresent(RedditModel::class.java)) return null

        // Make sure the type has the @Enveloped annotation, then return a new Set with it excluded
        val delegateAnnotations = Types.nextAnnotations(annotations, Enveloped::class.java) ?: return null

        // Create a type for RedditModelEnvelope<type>
        val envelope = Types.newParameterizedType(RedditModelEnvelope::class.java, type)
        val delegate = moshi.nextAdapter<RedditModelEnvelope<*>>(this, envelope, delegateAnnotations).nullSafe()

        val expectedKind = rawType.getAnnotation(RedditModel::class.java).kind
        return RedditModelJsonAdapter(delegate, expectedKind)
    }

    private class RedditModelJsonAdapter(private val delegate: JsonAdapter<RedditModelEnvelope<*>>, private val expectedKind: String) : JsonAdapter<Any>() {
        override fun toJson(writer: JsonWriter?, value: Any?) {
            throw NotImplementedError("serialization is not yet supported")
        }

        override fun fromJson(reader: JsonReader?): Any? {
            val result = delegate.fromJson(reader) ?: return null

            // Verify kind
            if (result.kind != expectedKind)
                throw IllegalArgumentException("Expected `kind` of '$expectedKind', got '${result.kind}'")
            return result.data
        }
    }
}

