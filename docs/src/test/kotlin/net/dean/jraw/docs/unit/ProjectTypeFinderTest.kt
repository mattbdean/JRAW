package net.dean.jraw.docs.unit

import com.winterbe.expekt.should
import net.dean.jraw.docs.ProjectTypeFinder
import net.dean.jraw.models.RedditObject
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
