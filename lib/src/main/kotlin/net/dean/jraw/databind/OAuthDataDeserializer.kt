package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import net.dean.jraw.oauth.OAuthData
import java.util.*

class OAuthDataDeserializer : StdDeserializer<OAuthData>(OAuthData::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): OAuthData {
        val node: JsonNode = p.codec.readTree(p)
        val shelfLife = node.get("expires_in").asInt() * 1000
        return OAuthData(
            accessToken = node.get("access_token").asText(),
            tokenType = node.get("token_type").asText(),
            shelfLife = shelfLife,
            scopes = node.get("scope").textValue().split(","),
            refreshToken = if (node.has("refresh_token")) node.get("refresh_token").asText() else null,
            expiration = Date(Date().time + shelfLife)
        )
    }
}
