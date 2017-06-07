package net.dean.jraw.http;

interface HttpAdapter {
    var userAgent: UserAgent

    @Throws(NetworkException::class)
    fun execute(r: HttpRequest): HttpResponse
}
