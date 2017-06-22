package net.dean.jraw.references

import net.dean.jraw.RedditClient
import net.dean.jraw.models.KindConstants

class CommentReference(reddit: RedditClient, id: String) : PublicContributionReference(reddit, id, KindConstants.COMMENT)
