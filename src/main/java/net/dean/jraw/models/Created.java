package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

import java.util.Date;

/**
 * Indicates that this Thing was created
 */
public interface Created {
	/** Registration date in local time */
	@JsonInteraction
	default Date getCreated(JsonNode data) {
		// created in seconds, Date constructor wants milliseconds
		return new Date(data.get("created").getLongValue() * 1000);
	}

	/** Registration date in UTC */
	@JsonInteraction
	default Date getCreatedUtc(JsonNode data) {
		// created in seconds, Date constructor wants milliseconds
		return new Date(data.get("created_utc").getLongValue() * 1000);
	}
}
