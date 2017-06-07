package net.dean.jraw.http

import java.io.IOException

/**
 * Provides a generic implementation of an HttpAdapter.
 *
 * Handles throwing [NetworkException]s and RuntimeExceptions for internal HTTP library failures. This class provides
 * the [executeRequest] method, which the subclass will implement and use to send requests using 3rd party libraries.
 */
abstract class AbstractHttpAdapter(override var userAgent: UserAgent) : HttpAdapter {
    final override fun execute(r: HttpRequest): HttpResponse {
        try {
            val res = executeRequest(r)
            if (!res.successful)
                throw NetworkException(res)

            return res
        } catch (e: IOException) {
            throw RuntimeException("HTTP request engine encountered an error: ${r.method} ${r.url}", e)
        }
    }

    /**
     * Sends a HTTP request using a 3rd party library
     */
    protected abstract fun executeRequest(r: HttpRequest): HttpResponse
}
