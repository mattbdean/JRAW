package net.dean.jraw.references

import net.dean.jraw.RedditClient

class OtherUserReference(reddit: RedditClient, username: String) : UserReference(reddit, username) {
    override val isSelf = false
}
