package net.dean.jraw.databind

import com.squareup.moshi.FromJson
import net.dean.jraw.models.OAuthData
import net.dean.jraw.models.internal.OAuthDataJson
import java.util.*
import java.util.concurrent.TimeUnit

class OAuthDataJsonAdapter {
    @FromJson fun fromJson(json: OAuthDataJson): OAuthData {
        return OAuthData(
            accessToken = json.accessToken,
            scopes = json.scopeList.split(","),
            refreshToken = json.refreshToken,
            expiration = Date(Date().time + TimeUnit.MILLISECONDS.convert(json.expiresIn, TimeUnit.SECONDS))
        )
    }
}
