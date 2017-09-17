package net.dean.jraw.databind

import com.squareup.moshi.*
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit

class UnixDateAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi?): JsonAdapter<*>? {
        Types.nextAnnotations(annotations, UnixTime::class.java) ?: return null
        val precision = (annotations.first { it is UnixTime } as UnixTime).precision
        return Adapter(precision)
    }

    private class Adapter(val precision: TimeUnit) : JsonAdapter<Date>() {
        override fun fromJson(reader: JsonReader): Date? {
            // Normally the value of a comment's "edited" field is either the boolean value "false" or the unix time
            // in seconds in which it was edited. Very old (7+ years) comments use the boolean value "true" if the
            // comment is edited. Since we can't tell exactly when it was edited and the percentage of comments this
            // affects is likely very small, return null like it was never edited.
            if (reader.peek() == JsonReader.Token.BOOLEAN) {
                reader.readJsonValue()
                return null
            }

            if (reader.peek() == JsonReader.Token.NUMBER)
                return Date(TimeUnit.MILLISECONDS.convert(reader.nextLong(), precision))

            throw IllegalArgumentException("Expected a boolean or number value, got ${reader.peek()}")
        }

        override fun toJson(writer: JsonWriter, value: Date?) {
            if (value == null)
                writer.value(false)
            else
                writer.value(precision.convert(value.time, TimeUnit.MILLISECONDS))
        }
    }
}
