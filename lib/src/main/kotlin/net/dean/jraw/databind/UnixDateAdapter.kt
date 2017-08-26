package net.dean.jraw.databind

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.util.*

class UnixDateAdapter : JsonAdapter<Date>() {
    override fun fromJson(reader: JsonReader): Date? {
        if (reader.peek() == JsonReader.Token.BOOLEAN) {
            return if (!reader.nextBoolean()) null else throw IllegalArgumentException("Only 'false' boolean values are allowed")
        }

        if (reader.peek() == JsonReader.Token.NUMBER)
            return Date(reader.nextLong() * 1000)

        throw IllegalArgumentException("Expected a boolean or number value, got ${reader.peek()}")
    }

    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value == null)
            writer.value(false)
        else
            writer.value(value.time / 1000)
    }
}
