package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.WikiPage
import net.dean.jraw.test.CredentialsUtil.moderationSubreddit
import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class WikiReferenceTest : Spek({
    val ref = reddit.subreddit("RocketLeague").wiki()
    val pages: List<String> by lazy {
        ref.pages()
    }

    describe("pages") {
        it("should return a list of wiki pages") {
            ref.pages().should.have.size.above(0)
        }
    }

    describe("page") {
        it("should return a specific page") {
            val pageName = pages.first()
            val page = ref.page(pageName)

            // Nothing to really test besides that it deserializes properly
            page.should.be.an.instanceof(WikiPage::class.java)
        }
    }

    describe("revisions/revisionsFor") {
        it("should return a list of wiki revisions") {
            val revsAllWiki = ref.revisions().build().next()
            revsAllWiki.should.have.size.above(0)

            // If any page has revisions, it's the home page
            val revsSpecificPage = ref.revisionsFor("index").build().next()
            revsSpecificPage.should.have.size.above(0)
        }
    }

    describe("discussionsAbout") {
        it("should return a list of submissions that link to the wiki") {
            val discussions = ref.discussionsAbout("index").build().next()
            discussions.should.have.size.above(0)
        }
    }

    val moddedSubreddit = moderationSubreddit
    val moddedWiki = reddit.subreddit(moddedSubreddit).wiki()
    describe("updatePage") {
        val page = "jraw_testing_page"
        val content = "Some test content, page updated at ${Date()}"
        val reason = "Reason #${Random().nextInt(100)}"
        val currentUser = reddit.me().username

        it("should create or update an existing testing page") {
            moddedWiki.update(page, content, reason)
            val updatedPage = moddedWiki.page(page)

            updatedPage.mayRevise().should.be.`true`
            updatedPage.content.should.equal(content)
            updatedPage.revionBy!!.name.should.equal(currentUser)
        }
    }
})
