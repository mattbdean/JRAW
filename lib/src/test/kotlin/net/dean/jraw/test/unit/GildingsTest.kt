package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.Comment
import net.dean.jraw.models.Submission
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class GildingsTest : Spek({

    describe("test new submission gildings, they should not be null") {

        val json = "{" +
            "\"approved_at_utc\": null," +
            "\"subreddit\": \"IAmA\"," +
            "\"selftext\": \"I am Meghan Murphy, a Canadian writer and the founder and editor of Feminist Current. My recent and permanent Twitter ban received international media attention. \\n\\nMy upcoming talk in Vancouver, Canada on gender identity ideology and women's rights has also received media attention. Activists have attempted to have this talk shut down. People have emailed violent threats. One person falsely claimed to be me and emailed several media outlets stating I had cancelled. The Justice Centre for Constitutional Freedoms is helping the women who organized the event take legal action against the VPL for attempting to censor my talk. Efforts to silence me are off the hook but I'm here and I want to answer your questions! Ask me anything! https://www.instagram.com/p/BrjHHRGFZuw/\\n\\n\\nEDIT: FYI, MEGHAN MURPHY IS ANSWERING QUESTIONS FROM THE MeghanEmilyMurphy ACCOUNT. YES, THAT IS HER.\\n\\nUpdate: Meghan will be answering questions for another 35 minutes--until 5 p.m. PST/8 p.m. EST. Thanks so much to everyone for such thoughtful and engaging questions!    \"," +
            "\"author_fullname\": \"t2_2sxh96y9\"," +
            "\"saved\": false," +
            "\"mod_reason_title\": null," +
            "\"gilded\": 2," +
            "\"clicked\": false," +
            "\"title\": \"I am Meghan Murphy, journalist, feminist and founder of Feminist Current. Ask me anything.\"," +
            "\"link_flair_richtext\": []," +
            "\"subreddit_name_prefixed\": \"r/IAmA\"," +
            "\"hidden\": false," +
            "\"pwls\": 6," +
            "\"link_flair_css_class\": \"journalist\"," +
            "\"downs\": 0," +
            "\"thumbnail_height\": null," +
            "\"hide_score\": false," +
            "\"name\": \"t3_a83kug\"," +
            "\"quarantine\": false," +
            "\"link_flair_text_color\": \"dark\"," +
            "\"author_flair_background_color\": null," +
            "\"subreddit_type\": \"public\"," +
            "\"ups\": 141," +
            "\"domain\": \"self.IAmA\"," +
            "\"media_embed\": {}," +
            "\"thumbnail_width\": null," +
            "\"author_flair_template_id\": null," +
            "\"is_original_content\": false," +
            "\"user_reports\": []," +
            "\"secure_media\": null," +
            "\"is_reddit_media_domain\": false," +
            "\"is_meta\": false," +
            "\"category\": null," +
            "\"secure_media_embed\": {}," +
            "\"link_flair_text\": \"Journalist\"," +
            "\"can_mod_post\": false," +
            "\"score\": 141," +
            "\"approved_by\": null," +
            "\"thumbnail\": \"self\"," +
            "\"edited\": 1545352061," +
            "\"author_flair_css_class\": null," +
            "\"author_flair_richtext\": []," +
            "\"gildings\": {" +
            "\"gid_1\": 3," +
            "\"gid_2\": 2," +
            "\"gid_3\": 0" +
            "}," +
            "\"content_categories\": null," +
            "\"is_self\": true," +
            "\"mod_note\": null," +
            "\"created\": 1545375205," +
            "\"link_flair_type\": \"text\"," +
            "\"wls\": 6," +
            "\"banned_by\": null," +
            "\"author_flair_type\": \"text\"," +
            "\"contest_mode\": false," +
            "\"selftext_html\": \"&lt;!-- SC_OFF --&gt;&lt;div class=\\\"md\\\"&gt;&lt;p&gt;I am Meghan Murphy, a Canadian writer and the founder and editor of Feminist Current. My recent and permanent Twitter ban received international media attention. &lt;/p&gt;\\n\\n&lt;p&gt;My upcoming talk in Vancouver, Canada on gender identity ideology and women&amp;#39;s rights has also received media attention. Activists have attempted to have this talk shut down. People have emailed violent threats. One person falsely claimed to be me and emailed several media outlets stating I had cancelled. The Justice Centre for Constitutional Freedoms is helping the women who organized the event take legal action against the VPL for attempting to censor my talk. Efforts to silence me are off the hook but I&amp;#39;m here and I want to answer your questions! Ask me anything! &lt;a href=\\\"https://www.instagram.com/p/BrjHHRGFZuw/\\\"&gt;https://www.instagram.com/p/BrjHHRGFZuw/&lt;/a&gt;&lt;/p&gt;\\n\\n&lt;p&gt;EDIT: FYI, MEGHAN MURPHY IS ANSWERING QUESTIONS FROM THE MeghanEmilyMurphy ACCOUNT. YES, THAT IS HER.&lt;/p&gt;\\n\\n&lt;p&gt;Update: Meghan will be answering questions for another 35 minutes--until 5 p.m. PST/8 p.m. EST. Thanks so much to everyone for such thoughtful and engaging questions!    &lt;/p&gt;\\n&lt;/div&gt;&lt;!-- SC_ON --&gt;\"," +
            "\"likes\": null," +
            "\"suggested_sort\": null," +
            "\"banned_at_utc\": null," +
            "\"view_count\": null," +
            "\"archived\": false," +
            "\"no_follow\": false," +
            "\"is_crosspostable\": true," +
            "\"pinned\": false," +
            "\"over_18\": false," +
            "\"media_only\": false," +
            "\"link_flair_template_id\": null," +
            "\"can_gild\": true," +
            "\"spoiler\": false," +
            "\"locked\": false," +
            "\"author_flair_text\": null," +
            "\"visited\": false," +
            "\"num_reports\": null," +
            "\"distinguished\": null," +
            "\"subreddit_id\": \"t5_2qzb6\"," +
            "\"mod_reason_by\": null," +
            "\"removal_reason\": null," +
            "\"link_flair_background_color\": \"\"," +
            "\"id\": \"a83kug\"," +
            "\"is_robot_indexable\": true," +
            "\"report_reasons\": null," +
            "\"author\": \"_meghanmurphy\"," +
            "\"num_crossposts\": 5," +
            "\"num_comments\": 1657," +
            "\"send_replies\": true," +
            "\"whitelist_status\": \"all_ads\"," +
            "\"mod_reports\": []," +
            "\"author_patreon_flair\": false," +
            "\"author_flair_text_color\": null," +
            "\"permalink\": \"/r/IAmA/comments/a83kug/i_am_meghan_murphy_journalist_feminist_and/\"," +
            "\"parent_whitelist_status\": \"all_ads\"," +
            "\"stickied\": false," +
            "\"url\": \"https://www.reddit.com/r/IAmA/comments/a83kug/i_am_meghan_murphy_journalist_feminist_and/\"," +
            "\"subreddit_subscribers\": 18621904," +
            "\"created_utc\": 1545346405," +
            "\"media\": null," +
            "\"is_video\": false" +
            "}"

        val adapter = JrawUtils.adapter<Submission>()
        val submission = adapter.fromJson(json)

        println("[ silver: ${submission?.gildings?.silvers}, gold: ${submission?.gildings?.golds}, platinum: ${submission?.gildings?.platinums} ]")

        it("should have gildings") {

            submission.should.not.be.`null`

            submission?.gildings?.silvers.should.not.be.`null`
            submission?.gildings?.golds.should.not.be.`null`
            submission?.gildings?.platinums.should.not.be.`null`

            submission?.gildings?.silvers.should.be.least(0)
            submission?.gildings?.golds.should.be.least(0)
            submission?.gildings?.platinums.should.be.least(0)
        }
    }


    describe("test new comment gildings, they should not be null") {

        val json = "{" +
            "\"subreddit_id\": \"t5_2qh3l\"," +
            "\"approved_at_utc\": null," +
            "\"ups\": 23," +
            "\"mod_reason_by\": null," +
            "\"banned_by\": null," +
            "\"author_flair_type\": \"text\"," +
            "\"removal_reason\": null," +
            "\"link_id\": \"t3_a7yjzk\"," +
            "\"author_flair_template_id\": null," +
            "\"likes\": null," +
            "\"no_follow\": false," +
            "\"count\": 58," +
            "\"name\": \"t1_ec8hjwm\"," +
            "\"id\": \"ec8hjwm\"," +
            "\"parent_id\": \"t1_ec6n2c5\"," +
            "\"depth\": 1," +
            "\"children\": [" +
            "  \"ec8hjwm\"," +
            "  \"ec7ayrx\"," +
            "  \"ec7esun\"," +
            "  \"ec7idqs\"," +
            "  \"ec6rv6h\"," +
            "  \"ec7igii\"," +
            "  \"ec7g10c\"," +
            "  \"ec7ibvo\"," +
            "  \"ec7miat\"," +
            "  \"ec7blij\"," +
            "  \"ec82c6k\"," +
            "  \"ec7tskg\"," +
            "  \"ec783e6\"," +
            "  \"ec6xz5g\"," +
            "  \"ec6w3xb\"," +
            "  \"ec6sijk\"," +
            "  \"ec7gyx0\"," +
            "  \"ec7xgsl\"," +
            "  \"ec7upje\"," +
            "  \"ec7dop8\"," +
            "  \"ec71h07\"," +
            "  \"ec7h55k\"," +
            "  \"ec74hcs\"," +
            "  \"ec7bpb3\"," +
            "  \"ec6snkw\"," +
            "  \"ec7kig3\"," +
            "  \"ec7emli\"," +
            "  \"ec73017\"," +
            "  \"ec78fzm\"," +
            "  \"ec789rn\"," +
            "  \"ec7tb0c\"" +
            "]," +
            "\"user_reports\": []," +
            "\"saved\": false," +
            "\"id\": \"ec6n2c5\"," +
            "\"banned_at_utc\": null," +
            "\"mod_reason_title\": null," +
            "\"gilded\": 0," +
            "\"archived\": false," +
            "\"report_reasons\": null," +
            "\"author\": \"nampafh\"," +
            "\"can_mod_post\": false," +
            "\"send_replies\": true," +
            "\"parent_id\": \"t3_a7yjzk\"," +
            "\"score\": 7271," +
            "\"author_fullname\": \"t2_imv7c\"," +
            "\"replies\": {" +
            "  \"kind\": \"Listing\"," +
            "  \"data\": {" +
            "    \"modhash\": \"6bw86se3ip6bdd8c84cda2182053c820a44f4cf79b2378cf1b\"," +
            "    \"dist\": null," +
            "    \"children\": []," +
            "    \"after\": null," +
            "    \"before\": null" +
            "   }" +
            " }," +
            "\"approved_by\": null," +
            "\"downs\": 0," +
            "\"body\": \"For all those saying \\\"they actually need water\\\", he already is helping with that since October.\\n\\n\\\"Tesla CEO Elon Musk to pay for water stations, filtration at Flint schools as promised\\\"\\n\\nSource: https://www.usatoday.com/story/tech/nation-now/2018/10/07/elon-musk-flint-water-schools/1560391002/\"," +
            "\"edited\": false," +
            "\"author_flair_css_class\": null," +
            "\"is_submitter\": false," +
            "\"collapsed\": false," +
            "\"author_flair_richtext\": []," +
            "\"author_patreon_flair\": false," +
            "\"collapsed_reason\": null," +
            "\"body_html\": \"&lt;div class=\\\"md\\\"&gt;&lt;p&gt;For all those saying &amp;quot;they actually need water&amp;quot;, he already is helping with that since October.&lt;/p&gt;\\n\\n&lt;p&gt;&amp;quot;Tesla CEO Elon Musk to pay for water stations, filtration at Flint schools as promised&amp;quot;&lt;/p&gt;\\n\\n&lt;p&gt;Source: &lt;a href=\\\"https://www.usatoday.com/story/tech/nation-now/2018/10/07/elon-musk-flint-water-schools/1560391002/\\\"&gt;https://www.usatoday.com/story/tech/nation-now/2018/10/07/elon-musk-flint-water-schools/1560391002/&lt;/a&gt;&lt;/p&gt;\\n&lt;/div&gt;\"," +
            "\"stickied\": false," +
            "\"subreddit_type\": \"public\"," +
            "\"can_gild\": true," +
            "\"gildings\": {" +
            "  \"gid_1\": 1," +
            "  \"gid_2\": 0," +
            "  \"gid_3\": 0" +
            "}," +
            "\"author_flair_text_color\": null," +
            "\"score_hidden\": false," +
            "\"permalink\": \"/r/news/comments/a7yjzk/elon_musk_provides_423k_to_buy_laptops_for_all/ec6n2c5/\"," +
            "\"num_reports\": null," +
            "\"name\": \"t1_ec6n2c5\"," +
            "\"created\": 1545347012," +
            "\"subreddit\": \"news\"," +
            "\"author_flair_text\": null," +
            "\"created_utc\": 1545318212," +
            "\"subreddit_name_prefixed\": \"r/news\"," +
            "\"controversiality\": 0," +
            "\"depth\": 0," +
            "\"author_flair_background_color\": null," +
            "\"mod_reports\": []," +
            "\"mod_note\": null," +
            "\"distinguished\": null" +
            "}"

        val adapter = JrawUtils.adapter<Comment>()
        val comment = adapter.fromJson(json)

        println("[ silver: ${comment?.gildings?.silvers}, gold: ${comment?.gildings?.golds}, platinum: ${comment?.gildings?.platinums} ]")

        it("should have gildings") {

            comment.should.not.be.`null`

            comment?.gildings?.silvers.should.not.be.`null`
            comment?.gildings?.golds.should.not.be.`null`
            comment?.gildings?.platinums.should.not.be.`null`

            comment?.gildings?.silvers.should.be.least(0)
            comment?.gildings?.golds.should.be.least(0)
            comment?.gildings?.platinums.should.be.least(0)
        }
    }
})
