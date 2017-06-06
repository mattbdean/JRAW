package net.dean.jraw.test.util

import com.fasterxml.jackson.databind.JsonNode
import net.dean.jraw.http.HttpAdapter
import net.dean.jraw.http.HttpRequest
import org.awaitility.Awaitility.await

fun httpAsync(http: HttpAdapter, r: HttpRequest.Builder, handle: (body: JsonNode) -> Unit) {
    var json: JsonNode? = null

    http.execute(r
        .success { json = it.json }
        .build())
    await().until({ json != null })
    handle(json!!)
}

