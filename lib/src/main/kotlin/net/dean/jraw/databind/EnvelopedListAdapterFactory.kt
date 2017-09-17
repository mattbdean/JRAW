package net.dean.jraw.databind

import com.squareup.moshi.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * This class specifically handles List<T> properties annotated with [Enveloped].
 */
class EnvelopedListAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        if (rawType.name != NAME || type !is ParameterizedType) {
            return null
        }

        val subtype = type.actualTypeArguments[0]

        // Ensure we have either the @Enveloped or @DynamicEnveloped annotation
        var delegate: JsonAdapter<Any>? = null
        if (Types.nextAnnotations(annotations, Enveloped::class.java) != null)
            delegate = moshi.adapter<Any>(subtype, Enveloped::class.java)
        else if (Types.nextAnnotations(annotations, DynamicEnveloped::class.java) != null)
            delegate = moshi.adapter<Any>(subtype, DynamicEnveloped::class.java)

        if (delegate == null) return null
        return EnvelopedListAdapter(delegate)
    }

    companion object {
        // Probably a better way to do this
        @JvmStatic val NAME = List::class.java.name!!
    }

    private class EnvelopedListAdapter(private val delegate: JsonAdapter<Any>) : JsonAdapter<List<*>>() {
        override fun toJson(writer: JsonWriter?, value: List<*>?) {
            TODO("not implemented")
        }

        override fun fromJson(reader: JsonReader): List<*>? {
            val list: MutableList<Any?> = ArrayList()
            reader.beginArray()
            while (reader.hasNext())
                list.add(delegate.fromJson(reader))
            reader.endArray()
            return list
        }
    }
}
