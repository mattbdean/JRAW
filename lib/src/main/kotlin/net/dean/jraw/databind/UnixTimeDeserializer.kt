package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.util.*

/**
 * JsonDeserializer for numerical JSON properties represented as a UNIX time in seconds that want to be parsed as a
 * java.util.Date.
 */
internal class UnixTimeDeserializer : StdDeserializer<Date>(Date::class.java) {
    // There's probably a better way to do this
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Date? {
        // Some reddit API properties return either a unix time or the boolean value "false"
        if (p.text == "false") return null
        return Date(p.valueAsLong * 1000)
    }
}
