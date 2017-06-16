package net.dean.jraw.http

import net.dean.jraw.JrawUtils
import net.dean.jraw.http.SimpleHttpLogger.Companion.LINE_LENGTH
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import java.io.PrintStream
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Bare-bones implementation of HttpLogger
 *
 * This logger will print the request's method ("GET", "POST", etc.) and URL, as well as the response's base media type
 * (e.g. "application/json") and body. The response line will be truncated to 100 lines (the value of [LINE_LENGTH]).
 *
 * Here's an example of the 12th request logged by a [SimpleHttpLogger]
 *
 * ```
 * [12 ->] POST https://oauth.reddit.com/foo?bar=baz
 *         form: form=foo
 *               abc=123
 * [12 <-] 200 application/json: '{"foo":"bar"}'
 * ```
 *
 * @see LINE_LENGTH
 */
class SimpleHttpLogger(val out: PrintStream = System.out) : HttpLogger {
    private val counter: AtomicInteger = AtomicInteger(1)
    private val lock = Any()

    override fun request(r: HttpRequest, sent: Date): HttpLogger.Tag {
        val id = counter.getAndIncrement()

        synchronized(lock) {
            val tag = "[$id ->]"

            out.println("$tag ${r.method} ${r.url}")

            val form = parseForm(r)
            if (!form.isEmpty()) {
                logMap(
                    baseIndentLength = tag.length,
                    header = "form:",
                    pairs = form
                )
            }
        }

        return HttpLogger.Tag(id, sent)
    }

    override fun response(tag: HttpLogger.Tag, res: HttpResponse) {
        val contentType = MediaType.parse(res.contentType)!!
        val formattedType = "${contentType.type()}/${contentType.subtype()}"
        val body = res.body.replace("\n", "")
        val formattedTag = "[<- ${tag.requestId}]"

        synchronized(lock) {
            out.println(truncate("$formattedTag ${res.code} $formattedType: '$body'", LINE_LENGTH))
        }
    }

    private fun truncate(str: String, limit: Int) = if (str.length > limit)
        str.substring(0, limit - ELLIPSIS.length) + ELLIPSIS
    else
        str

    private fun parseForm(r: HttpRequest): Map<String, String> {
        if (r.body == null) return mapOf()

        val type = r.body.contentType()!!
        // Make sure we have URL-encoded data before we try to parse it
        if (type.type() != "application" || type.subtype() != "x-www-form-urlencoded") return mapOf()

        return JrawUtils.parseUrlEncoded(readRequestBody(r.body))
    }

    private fun readRequestBody(body: RequestBody): String {
        val buff = Buffer()
        body.writeTo(buff)
        return buff.readUtf8().replace("\n", "")
    }

    private fun logMap(baseIndentLength: Int, header: String, pairs: Map<String, String>) {
        var needsHeader = true
        val baseIndent = " ".repeat(baseIndentLength)
        for ((k, v) in pairs) {
            val prefix = if (needsHeader) header else " ".repeat(header.length)
            if (needsHeader) needsHeader = false
            out.println("$baseIndent $prefix $k=$v")
        }
    }

    companion object {
        private val ELLIPSIS = "(...)"
        const val LINE_LENGTH = 100
    }
}

/*
 */
