package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import net.dean.jraw.models.DistinguishedStatus

class DistinguishedStatusDeserializer : StdDeserializer<DistinguishedStatus>(DistinguishedStatus::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): DistinguishedStatus {
        if (p.text == "null") return DistinguishedStatus.NORMAL
        return DistinguishedStatus.valueOf(p.text.toUpperCase())
    }
}
