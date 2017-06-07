package net.dean.jraw.http

import com.fasterxml.jackson.databind.JsonNode
import net.dean.jraw.JrawUtils
import okhttp3.Response

/**
 * Simple wrapper around an [okhttp3.Response]
 */
class HttpResponse internal constructor(val raw: Response) {
    /** The JSON value of the response body. Lazy initialized. */
    val json: JsonNode by lazy { JrawUtils.parseJson(raw.body()!!.string() ) }
    val body: String by lazy { raw.body()!!.string() }

    /** HTTP status code */
    val code = raw.code()
}
