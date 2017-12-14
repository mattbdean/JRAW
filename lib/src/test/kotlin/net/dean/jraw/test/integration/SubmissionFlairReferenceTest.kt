package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.Flair
import net.dean.jraw.references.SubmissionFlairReference
import net.dean.jraw.test.SharedObjects
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.assume
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class SubmissionFlairReferenceTest : Spek({
    val ref: SubmissionFlairReference by lazy {
        SharedObjects.submittedSelfPost!!.flair(SharedObjects.submittedSelfPostSubreddit)
    }

    val flairOptions: List<Flair> by lazy {
        reddit
            .subreddit(SharedObjects.submittedSelfPostSubreddit)
            .linkFlairOptions()
    }

    describe("updateTo") {
        assume({ SharedObjects.submittedSelfPost != null }, "should update the flair for a given submission") {
            val template = flairOptions.first()
            ref.updateToTemplate(template.id)
            ref.submission().inspect().linkFlairText.should.equal(template.text)
        }

        assume({ SharedObjects.submittedSelfPost != null }, "should update the flair for a given submission with editable text") {
            val template = flairOptions.first { it.isTextEditable }
            val flairText = "foo bar baz"

            ref.updateToTemplate(template.id, flairText)
            ref.submission().inspect().linkFlairText.should.equal(flairText)
        }
    }
})
