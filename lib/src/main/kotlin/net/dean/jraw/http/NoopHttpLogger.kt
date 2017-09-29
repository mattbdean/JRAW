package net.dean.jraw.http

import java.util.*

/** An HttpLogger implementation that does nothing. */
class NoopHttpLogger : HttpLogger {
    override fun request(r: HttpRequest, sent: Date): HttpLogger.Tag {
        return TAG
    }

    override fun response(tag: HttpLogger.Tag, res: HttpResponse) {
    }

    companion object {
        @JvmField internal val TAG = HttpLogger.Tag(-1, Date(0L))
    }
}
