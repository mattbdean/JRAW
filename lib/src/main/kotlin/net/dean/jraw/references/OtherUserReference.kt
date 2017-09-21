package net.dean.jraw.references

import net.dean.jraw.RedditClient

class OtherUserReference(reddit: RedditClient, username: String) : UserReference<OtherUserFlairReference>(reddit, username) {
    override val isSelf = false

    override fun flairOn(subreddit: String): OtherUserFlairReference = OtherUserFlairReference(reddit, subreddit, username)
}
