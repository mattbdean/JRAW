package net.dean.jraw.http

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class OkHttpAdapter(override var userAgent: UserAgent): HttpAdapter {
    private val http: OkHttpClient = OkHttpClient()

    override fun execute(r: HttpRequest): HttpResponse {
        val call = createCall(r)
        try {
            val res = call.execute()
            if (!res.isSuccessful)
                throw NetworkException(call.request(), res)

            return HttpResponse(res)
        } catch (e: IOException) {
            val req = call.request()
            throw RuntimeException("HTTP request engine encountered an error: ${req.method()} ${req.url()}", e)
        }
    }

    private fun createCall(r: HttpRequest): Call =
        (if (r.basicAuth != null) createAuthenticatedClient(r.basicAuth) else http).newCall(compileRequest(r))

    private fun createAuthenticatedClient(data: BasicAuthData): OkHttpClient =
        http.newBuilder().authenticator(BasicAuthenticator(data)).build()

    private fun compileRequest(r: HttpRequest): Request =
        Request.Builder()
            .headers(r.headers.set("User-Agent", userAgent.value).build())
            .url(r.url)
            .method(r.method, r.body)
            .build()
}
