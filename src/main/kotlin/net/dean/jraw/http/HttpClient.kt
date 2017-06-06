package net.dean.jraw.http;

interface HttpClient {
    var userAgent: String
    fun execute(r: HttpRequest)
    fun executeSync(r: HttpRequest): HttpResponse
}
