package net.dean.jraw.test.util

import net.dean.jraw.Version
import net.dean.jraw.http.UserAgent

object TestConfig {
    val userAgent = UserAgent.of("lib", "net.dean.jraw.test", Version.get(), "thatJavaNerd")
}
