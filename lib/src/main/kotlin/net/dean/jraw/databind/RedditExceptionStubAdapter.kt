package net.dean.jraw.databind

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import net.dean.jraw.ApiExceptionStub
import net.dean.jraw.RedditExceptionStub

/**
 * Deserializes [RedditExceptionStub] and its derivatives. API exceptions can come in a few flavors:
 */
internal class RedditExceptionStubAdapter : JsonAdapter<RedditExceptionStub<*>>() {
    override fun fromJson(reader: JsonReader): RedditExceptionStub<*>? {
        // TODO This isn't finished, still have to handle ratelimit and other error formats
        if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
            val json = reader.readJsonValue() as Map<*, *>
            if (json["message"] != null && json["error"] != null)
                return ApiExceptionStub(json["message"] as String, json["error"].toString(), listOf())
        }

        return null
    }

    override fun toJson(writer: JsonWriter?, value: RedditExceptionStub<*>?) {
        TODO("not implemented")
    }
}
