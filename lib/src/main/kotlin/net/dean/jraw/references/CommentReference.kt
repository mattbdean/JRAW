package net.dean.jraw.references

import net.dean.jraw.RedditClient
import net.dean.jraw.models.KindConstants

/** A reference to a reply to a submission or another comment */
class CommentReference(reddit: RedditClient, id: String) : PublicContributionReference(reddit, id, KindConstants.COMMENT)
