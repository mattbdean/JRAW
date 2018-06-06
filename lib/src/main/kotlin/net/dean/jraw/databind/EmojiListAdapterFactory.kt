package net.dean.jraw.databind

import com.squareup.moshi.*
import net.dean.jraw.models.Emoji
import net.dean.jraw.models.internal.EmojiData
import java.lang.reflect.Type

/**
 * This class exists to transform a `Map<String, Map<String, EmojiData>>` into a `List<Emoji>`. Here's an example:
 *
 * ```json
 * {
 *   "snoomojis": {
 *     "cake": {
 *       "url":"https://emoji.redditmedia.com/46kel8lf1guz_t5_3nqvj/cake",
 *       "created_by":"t2_6zfp6ii"
 *     },
 *     "cat_blep": {
 *       "url":"https://emoji.redditmedia.com/p9sxc1zh1guz_t5_3nqvj/cat_blep",
 *       "created_by":"t2_6zfp6ii"
 *     },
 *     ...
 *   },
 *   "t5_2qh0u": {
 *     "foo": {
 *       "url": "...",
 *       "created_by": "..."
 *     }
 *   }
 * }
 * ```
 *
 * Here, "snoomojis" and "t5_2qh0u" are namespaces. "cake", "cat_blep", and "foo" are names of emojis.
 */
class EmojiListAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type?, annotations: MutableSet<out Annotation>?, moshi: Moshi): JsonAdapter<*>? {
        return if (type == TYPE) Adapter(moshi.adapter(EmojiData::class.java)) else null
    }

    companion object {
        private val TYPE = Types.newParameterizedType(List::class.java, Emoji::class.java)
    }

    private class Adapter(val delegate: JsonAdapter<EmojiData>) : JsonAdapter<List<Emoji>>() {
        override fun fromJson(reader: JsonReader): List<Emoji> {
            val list = ArrayList<Emoji>()

            reader.beginObject()
            while (reader.hasNext()) {
                val origin = reader.nextName()
                reader.beginObject()

                while (reader.hasNext()) {
                    val name = reader.nextName()
                    val data = delegate.fromJson(reader)!!

                    list.add(Emoji.create(data.url, data.createdBy, name, origin))
                }

                reader.endObject()
            }
            reader.endObject()

            return list
        }

        override fun toJson(writer: JsonWriter, value: List<Emoji>?) {
            if (value == null) {
                writer.nullValue()
            } else {
                throw UnsupportedOperationException("Not supported")
            }
        }
    }
}
