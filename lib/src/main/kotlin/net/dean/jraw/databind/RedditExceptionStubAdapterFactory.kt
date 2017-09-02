package net.dean.jraw.databind

import com.squareup.moshi.*
import net.dean.jraw.models.internal.ObjectBasedApiExceptionStub
import net.dean.jraw.models.internal.RedditExceptionStub
import java.lang.reflect.Type

/**
 * Deserializes [RedditExceptionStub] and its derivatives. API exceptions can come in a few flavors:
 */
internal class RedditExceptionStubAdapterFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        if (!RedditExceptionStub::class.java.isAssignableFrom(rawType)) return null

        val adapters = types.map { moshi.adapter<RedditExceptionStub<*>>(it) }
        return StubAdapter(adapters)
    }

    companion object {
        private val types: List<Class<out RedditExceptionStub<*>>> = listOf(
            ObjectBasedApiExceptionStub::class.java
        )
    }

    private class StubAdapter(private val delegates: List<JsonAdapter<out RedditExceptionStub<*>>>) : JsonAdapter<RedditExceptionStub<*>>() {
        override fun fromJson(reader: JsonReader): RedditExceptionStub<*>? {
            // TODO This isn't finished, still have to handle ratelimit and other error formats

            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                val jsonValue = reader.readJsonValue()

                // The suggested implementation is simpler to read but much slower
                @Suppress("LoopToCallChain")
                for (adapter in delegates) {
                    val stub = adapter.fromJsonValue(jsonValue)
                    if (stub != null)
                        return stub
                }

            }

            return null
        }

        override fun toJson(writer: JsonWriter?, value: RedditExceptionStub<*>?) {
            TODO("not implemented")
        }
    }
}
