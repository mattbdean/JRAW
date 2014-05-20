package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

public interface Votable {
	/** Gets the amount of upvotes the object has received */
	default Integer getUpvotes(JsonNode data) {
		return data.get("ups").getIntValue();
	}

	/** Gets the amount of downvotes the object has received */
	default Integer getDownvotes(JsonNode data) {
		return data.get("downs").getIntValue();
	}

	/** The way in which the logged in user voted */
	default VoteType getVote(JsonNode data) {
		JsonNode likes = data.get("likes");
		if (likes.isNull()) {
			return VoteType.NO_VOTE;
		}

		return likes.getBooleanValue() ? VoteType.UPVOTE : VoteType.DOWNVOTE;
	}
}
