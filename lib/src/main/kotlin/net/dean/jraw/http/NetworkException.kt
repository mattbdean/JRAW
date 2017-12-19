package net.dean.jraw.http

/**
 * Thrown when an HTTP response contains a failing error code (4xx or 5xx)
 *
 * @property res The response that initiated this exception
 */
class NetworkException(val res: HttpResponse): RuntimeException(
    "HTTP request created unsuccessful response: ${res.request.method()} ${res.request.url()} -> ${res.code}"
)
