package net.dean.jraw.http;

interface HttpAdapter {
    var userAgent: UserAgent
    fun execute(r: HttpRequest)
    fun executeSync(r: HttpRequest): HttpResponse
}
