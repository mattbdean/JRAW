package net.dean.jraw.test

import net.dean.jraw.RedditClient
import net.dean.jraw.Version
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.http.UserAgent
import net.dean.jraw.oauth.OAuthHelper

object TestConfig {
    /** UserAgent used by all HttpAdapters used for testing */
    val userAgent = UserAgent("lib", "net.dean.jraw.test", Version.get(), "thatJavaNerd")

    /** Lazy-initialized RedditClient authorized by a script app */
    private val logger = SimpleHttpLogger()
    val reddit: RedditClient by lazy {
        val r = OAuthHelper.automatic(newOkHttpAdapter(), CredentialsUtil.script)
        r.logger = logger
        r
    }
    val redditUserless: RedditClient by lazy {
        val r = OAuthHelper.automatic(newOkHttpAdapter(), CredentialsUtil.applicationOnly)
        r.logger = logger
        r
    }
}
