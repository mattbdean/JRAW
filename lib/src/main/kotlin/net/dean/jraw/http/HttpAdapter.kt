package net.dean.jraw.http

/**
 * Standard interface for sending HTTP requests.
 *
 * Since this project is tightly bound to OkHttp, this interface exists primarily for testing.
 */
interface HttpAdapter {
    var userAgent: UserAgent

    /** Executes the HTTP request represended by the given data */
    fun execute(r: HttpRequest): HttpResponse
}
