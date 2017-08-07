package net.dean.jraw.docs

import com.github.javaparser.Position
import com.github.javaparser.Range
import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import net.dean.jraw.models.RedditObject
import net.dean.jraw.oauth.OAuthHelper
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ProjectTypeFinderTest : Spek({
    describe("find") {
        it("should correctly identify the JRAW-specific types in the given code sample") {
            val code = CodeSampleRef("test", listOf(
                "// do some work",
                "RedditClient myRedditClient = OAuthHelper.automatic();"
            ))

            ProjectTypeFinder.find(code).should.equal(mapOf(
                Range(Position(2, 1), Position(2, 12)) to RedditClient::class.java,
                Range(Position(2, 31), Position(2, 41)) to OAuthHelper::class.java
            ))
        }

        it("should not include non-JRAW types in its findings") {
            val code = CodeSampleRef("test", listOf(
                "List<RedditObject> list = myRedditClient.doSomething();"
            ))

            ProjectTypeFinder.find(code).should.equal(mapOf(
                Range(Position(1, 6), Position(1, 17)) to RedditObject::class.java
            ))
        }
    }

    describe("isProjectType") {
        it("should return true for JRAW classes") {
            ProjectTypeFinder.isProjectType("RedditClient").should.be.`true`
            ProjectTypeFinder.isProjectType("HttpAdapter").should.be.`true`
            ProjectTypeFinder.isProjectType("String").should.be.`false`
        }
    }

    describe("fromSimpleName") {
        it("should return the class associated with a JRAW class") {
            ProjectTypeFinder.fromSimpleName("RedditObject").should.equal(RedditObject::class.java)
        }

        it("should return null if there is no JRAW class with the given simple name") {
            ProjectTypeFinder.fromSimpleName("Foobarbaz").should.be.`null`
            ProjectTypeFinder.fromSimpleName("TestClass").should.be.`null`
        }
    }
})
