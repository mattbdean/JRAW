package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.http.UserAgent
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class UserAgentTest: Spek({
    describe("of") {
        it("should format a User-Agent string") {
            UserAgent(
                platform = "lib",
                appId = "net.dean.jraw",
                version = "the best",
                redditUsername = "thatJavaNerd"
            ).should.equal(UserAgent("lib:net.dean.jraw:the best (by /u/thatJavaNerd)"))
        }
    }
})
