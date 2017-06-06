package net.dean.jraw.http

import okhttp3.*
import java.io.IOException

open class RestClient(override var userAgent: String): HttpClient {
    protected val http: OkHttpClient = OkHttpClient()

    override fun execute(r: HttpRequest) {
        createCall(r).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                r.internalFailure(call!!.request(), e!!)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val httpResponse = HttpResponse(response!!)
                if (response.isSuccessful)
                    r.success(httpResponse)
                else
                    r.failure(httpResponse)
            }
        })
    }

    @Throws(NetworkException::class)
    override fun executeSync(r: HttpRequest): HttpResponse {
        val res = createCall(r)
        try {
            return HttpResponse(res.execute())
        } catch (ex: IOException) {
            throw HttpRequest.createInternalFailureException(res.request(), ex)
        }
    }

    private fun createCall(r: HttpRequest): Call =
        (if (r.basicAuth != null) createAuthenticatedClient(r.basicAuth) else http).newCall(compileRequest(r))

    private fun createAuthenticatedClient(data: BasicAuthData): OkHttpClient =
        http.newBuilder().authenticator(BasicAuthenticator(data)).build()

    private fun compileRequest(r: HttpRequest): Request =
        Request.Builder()
            .header("User-Agent", userAgent)
            .url(r.url)
            .method(r.method, r.body)
            .build()
}
