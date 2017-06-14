package net.dean.jraw.test.util

import net.dean.jraw.RedditClient
import net.dean.jraw.Version
import net.dean.jraw.http.UserAgent
import net.dean.jraw.http.oauth.OAuthHelper

object TestConfig {
    /** UserAgent used by all HttpAdapters used for testing */
    val userAgent = UserAgent("lib", "net.dean.jraw.test", Version.get(), "thatJavaNerd")

    /** Lazy-initialized RedditClient authorized by a script app */
    val reddit: RedditClient by lazy { OAuthHelper.script(CredentialsUtil.script, newOkHttpAdapter()) }
}
