package net.dean.jraw.references

import com.squareup.moshi.Types
import net.dean.jraw.Endpoint
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.models.Emoji

class EmojiReference(reddit: RedditClient, val subreddit: String) : AbstractReference(reddit) {
    /**
     * Lists all emojis available for use in this subreddit
     */
    fun list(): List<Emoji> {
        return reddit.request {
            it.endpoint(Endpoint.GET_SUBREDDIT_EMOJIS_ALL, subreddit)
        }.deserializeWith(emojiListAdapter)
    }

    companion object {
        private val emojiListAdapter by lazy {
            JrawUtils.moshi.adapter<List<Emoji>>(Types.newParameterizedType(List::class.java, Emoji::class.java))
        }
    }
}
