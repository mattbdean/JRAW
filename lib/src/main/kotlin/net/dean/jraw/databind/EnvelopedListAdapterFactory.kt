package net.dean.jraw.databind

import com.squareup.moshi.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * This class specifically handles List<T> properties annotated with [Enveloped].
 */
class EnvelopedListAdapterFactory : JsonAdapter.Factory {
    /** @inheritDoc */
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        // Make sure we have the @Enveloped annotation
        Types.nextAnnotations(annotations, Enveloped::class.java) ?: return null

        val rawType = Types.getRawType(type)
        if (rawType.name != NAME || type !is ParameterizedType) {
            return null
        }

        // Get a
        val subtype = type.actualTypeArguments[0]
        val delegate: JsonAdapter<Any> = moshi.adapter(subtype, Enveloped::class.java)
        return EnvelopedListAdapter(delegate)
    }

    /** */
    companion object {
        // Probably a better way to do this
        @JvmStatic private val NAME = List::class.java.name!!
    }

    private class EnvelopedListAdapter(private val delegate: JsonAdapter<Any>) : JsonAdapter<List<*>>() {
        override fun toJson(writer: JsonWriter, value: List<*>?) {
            if (value == null) {
                writer.nullValue()
                return
            }

            writer.beginArray()
            for (v in value)
                delegate.toJson(v)
            writer.endArray()
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
