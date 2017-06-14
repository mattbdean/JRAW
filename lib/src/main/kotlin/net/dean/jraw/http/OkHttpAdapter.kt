package net.dean.jraw.http

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * [HttpAdapter] implementation backed by Square's fantastic [OkHttp](https://square.github.io/okhttp/)
 */
class OkHttpAdapter(override var userAgent: UserAgent) : HttpAdapter {
    private val http: OkHttpClient = OkHttpClient()

    override fun execute(r: HttpRequest): HttpResponse {
        val res = createCall(r).execute()
        return HttpResponse(
            code = res.code(),
            readBody = { res.body()!!.string() },
            requestMethod = res.request().method(),
            requestUrl = res.request().url().toString(),
            contentType = res.body()!!.contentType().toString()
        )
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
