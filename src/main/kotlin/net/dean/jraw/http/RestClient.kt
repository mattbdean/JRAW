package net.dean.jraw.http

import okhttp3.*
import java.io.IOException

open class RestClient(override var userAgent: String) : HttpClient {
    protected val http: OkHttpClient = OkHttpClient()

    override fun execute(r: HttpRequest) {
        val request = Request.Builder()
            .header("User-Agent", userAgent)
            .url(r.url)
            .method(r.method, r.body)
            .build()

        fun handleNonError(response: Response) {
            val httpResponse = HttpResponse(response)
            if (response.isSuccessful)
                r.success(httpResponse)
            else
                r.failure(httpResponse)
        }

        val http = if (r.basicAuth != null) createAuthenticatedClient(r.basicAuth) else http

        if (r.sync) {
            try {
                handleNonError(http.newCall(request).execute())
            } catch (ex: IOException) {
                r.internalFailure(request, ex)
            }
        } else {
            http.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    r.internalFailure(call!!.request(), e!!)
                }

                override fun onResponse(call: Call?, response: Response?) {
                    handleNonError(response!!)
                }
            })
        }
    }

    private fun createAuthenticatedClient(data: BasicAuthData): OkHttpClient =
        http.newBuilder().authenticator(BasicAuthenticator(data)).build()
}
