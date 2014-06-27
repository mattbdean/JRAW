package net.dean.jraw.models;

import net.dean.jraw.models.core.Thing;
import org.codehaus.jackson.JsonNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MultiReddit extends Thing implements Created {

	/**
	 * Instantiates a new Thing
	 *
	 * @param dataNode The node to parse data from
	 */
	public MultiReddit(JsonNode dataNode) {
		super(dataNode);
	}

	@Override
	public ThingType getType() {
		return ThingType.MULTI;
	}

	@JsonInteraction
	public boolean canEdit() {
		return data("can_edit", Boolean.class);
	}

	@JsonInteraction
	public String getName() {
		return data("name");
	}

	@JsonInteraction
	public List<String> getSubreddits() {
		List<String> subreddits = new ArrayList<>();

		JsonNode node = data.get("subreddits");
		for (JsonNode subredditNode : node) {
			subreddits.add(subredditNode.get("name").asText());
		}

		return subreddits;
	}

	@JsonInteraction
	public boolean isPrivate() {
		return data("visibility", Boolean.class);
	}

	@JsonInteraction
	public URI getPath() {
		return data("path", URI.class);
	}
}
