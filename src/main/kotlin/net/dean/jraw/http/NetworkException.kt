package net.dean.jraw.http

import okhttp3.Request
import okhttp3.Response

class NetworkException(val req: Request, val res: Response): RuntimeException(
    "HTTP request created unsuccessful response: ${req.method()} ${req.url()} --> ${res.code()}"
)
