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
            if (reader.peek() == JsonReader.Token.BOOLEAN) {
                return if (!reader.nextBoolean()) null else throw IllegalArgumentException("Only 'false' boolean values are allowed")
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
