package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.Comment
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.test.TestConfig
import net.dean.jraw.tree.RootCommentNode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class GildingsTest : Spek({

    describe("test new submission gildings, they should not be null") {

        val ref = TestConfig.reddit.subreddit("iama").posts()
            .limit(Paginator.RECOMMENDED_MAX_LIMIT)
            .sorting(SubredditSort.TOP)
            .timePeriod(TimePeriod.ALL)
            .build()

        it("should have gildings") {

            val sub = ref.next()

            sub.forEach {

                it.should.not.be.`null`

                it!!.gildings.silvers.should.not.be.`null`
                it.gildings.golds.should.not.be.`null`
                it.gildings.platinums.should.not.be.`null`

                it.gildings.silvers.should.be.least(0)
                it.gildings.golds.should.be.least(0)
                it.gildings.platinums.should.be.least(0)
            }
        }
    }


    describe("test new comment gildings, they should not be null") {

        val ref = TestConfig.reddit.subreddit("iama").posts()
            .limit(Paginator.RECOMMENDED_MAX_LIMIT)
            .sorting(SubredditSort.TOP)
            .timePeriod(TimePeriod.ALL)
            .build()

        val subs = ref.next()
        val submission = subs[0]

        val comments = submission.toReference(TestConfig.reddit).comments()

        it("should have gildings") {

            val sequence = comments.walkTree().take(20)
            sequence.forEach {

                if((it !is RootCommentNode)) {
                    val comment = it.subject as? Comment

                    comment.should.not.be.`null`

                    comment!!.gildings.silvers.should.not.be.`null`
                    comment.gildings.golds.should.not.be.`null`
                    comment.gildings.platinums.should.not.be.`null`

                    comment.gildings.silvers.should.be.least(0)
                    comment.gildings.golds.should.be.least(0)
                    comment.gildings.platinums.should.be.least(0)
                }
            }
        }
    }
})
