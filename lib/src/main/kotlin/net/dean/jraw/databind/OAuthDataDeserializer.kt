package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import net.dean.jraw.http.oauth.OAuthData

class OAuthDataDeserializer(valueClass: Class<OAuthData>?) : StdDeserializer<OAuthData>(valueClass) {
    constructor(): this(OAuthData::class.java)

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): OAuthData {
        val node: JsonNode = p.codec.readTree(p)
        return OAuthData(
            accessToken = node.get("access_token").asText(),
            tokenType = node.get("token_type").asText(),
            shelfLife = node.get("expires_in").asInt() * 1000,
            scopes = node.get("scope").textValue().split(","),
            refreshToken = if (node.has("refresh_token")) node.get("refresh_token").asText() else null
        )
    }
}
