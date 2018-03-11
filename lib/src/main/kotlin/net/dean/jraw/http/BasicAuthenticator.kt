package net.dean.jraw.http

import okhttp3.*

internal class BasicAuthenticator(private val data: BasicAuthData): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val credential = Credentials.basic(data.username, data.password)
        val header = if (response.code() == 407) "Proxy-Authorization" else "Authorization"
        return response.request().newBuilder().header(header, credential).build()
    }
}
