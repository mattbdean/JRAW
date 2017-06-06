package net.dean.jraw.http;

interface HttpAdapter {
    var userAgent: String
    fun execute(r: HttpRequest)
    fun executeSync(r: HttpRequest): HttpResponse
}
