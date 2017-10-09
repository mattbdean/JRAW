package net.dean.jraw.docs.unit

import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import net.dean.jraw.docs.ProjectTypeFinder
import net.dean.jraw.docs.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ProjectTypeFinderTest : Spek({
    describe("isProjectType") {
        it("should return true for JRAW classes") {
            ProjectTypeFinder.isProjectType("RedditClient").should.be.`true`
            ProjectTypeFinder.isProjectType("NetworkAdapter").should.be.`true`
            ProjectTypeFinder.isProjectType("String").should.be.`false`
        }
    }

    describe("from") {
        it("should return the class associated with a JRAW class") {
            ProjectTypeFinder.from("RedditClient").should.equal(RedditClient::class.java)
        }

        it("should return null if there is no JRAW class with the given (simple) name") {
            ProjectTypeFinder.from("Foobarbaz").should.be.`null`
            ProjectTypeFinder.from("TestClass").should.be.`null`
            ProjectTypeFinder.from("net.dean.jraw.Foo").should.be.`null`
        }

        it("should return a JRAW class when given the fully qualified name") {
            ProjectTypeFinder.from(RedditClient::class.java.name).should.equal(RedditClient::class.java)
        }

        it("should throw an IllegalArgumentException when a fully qualified class name is not a JRAW type") {
            expectException(IllegalArgumentException::class) {
                ProjectTypeFinder.from("java.lang.Object")
            }
        }
    }
})
