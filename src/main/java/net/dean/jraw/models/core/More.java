package net.dean.jraw.models.core;

import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.ThingType;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class More extends RedditObject {

	public More(JsonNode dataNode) {
		super(dataNode);
	}

	@Override
	public ThingType getType() {
		return ThingType.MORE;
	}

	@JsonInteraction
	public Integer getCount() {
		return data("count").getIntValue();
	}

	@JsonInteraction
	public String getParentId() {
		return data("parent_id").getTextValue();
	}

	@JsonInteraction
	public List<String> getChildrenIds() {
		List<String> ids = new ArrayList<>();
		for (JsonNode child : data("children")) {
			ids.add(child.getTextValue());
		}

		return ids;
	}

	@JsonInteraction
	public String getId() {
		return data("id").getTextValue();
	}

	@JsonInteraction
	public String getName() {
		return data("name").getTextValue();
	}
}
