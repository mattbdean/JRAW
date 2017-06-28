package net.dean.jraw.http

class NetworkException(val res: HttpResponse): RuntimeException(
    "HTTP request created unsuccessful response: ${res.request.method()} ${res.request.url()} -> ${res.code}"
)
