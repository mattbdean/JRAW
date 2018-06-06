package net.dean.jraw.databind

import com.squareup.moshi.*
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This factory produces JsonAdapters that handle properties annotated with [UnixTime].
 */
class UnixDateAdapterFactory : JsonAdapter.Factory {
    /** @inheritDoc */
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi?): JsonAdapter<*>? {
        if (type != Date::class.java) return null
        Types.nextAnnotations(annotations, UnixTime::class.java) ?: return null
        val precision = (annotations.first { it is UnixTime } as UnixTime).precision
        return Adapter(precision)
    }

    internal class Adapter(val precision: TimeUnit) : JsonAdapter<Date>() {
        override fun fromJson(reader: JsonReader): Date? {
            val path = reader.path
            return when (reader.peek()) {
                // Normally the value of a comment's "edited" field is either the boolean value "false" or the unix time
                // in seconds in which it was edited. Very old (7+ years) comments use the boolean value "true" if the
                // comment is edited. Since we can't tell exactly when it was edited and the percentage of comments this
                // affects is likely very small, return null like it was never edited.
                JsonReader.Token.BOOLEAN -> {
                    reader.nextBoolean()
                    null
                }
                JsonReader.Token.NULL -> reader.nextNull()
                JsonReader.Token.NUMBER -> Date(TimeUnit.MILLISECONDS.convert(reader.nextLong(), precision))
                else -> {
                    throw JsonDataException("Expected a null, boolean, or numeric value, got ${reader.peek()} at $path")
                }
            }
        }

        override fun toJson(writer: JsonWriter, value: Date?) {
            if (value == null)
                writer.nullValue()
            else
                writer.value(precision.convert(value.time, TimeUnit.MILLISECONDS))
        }
    }
}
