package net.dean.jraw.models.core;

import net.dean.jraw.RedditObjectParser;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.ThingType;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a listing of Things. A Listing has four main keys: before, after, modhash, and its children.
 *
 * @param <T> The type of elements that will be in this listing
 * @author Matthew Dean
 */
public class Listing<T extends RedditObject> extends RedditObject {
	/**
	 * The RedditObjectParser which will be used to parse JSON values into RedditObjects
	 */
	private static final RedditObjectParser PARSER = new RedditObjectParser();

	/**
	 * The class of the contents of the listing
	 */
	private Class<T> thingClass;

	/**
	 * Whether this listing contains a "more" element in its children
	 */
	private boolean hasChildren;

	/**
	 * Instantiates a new listing
	 *
	 * @param dataNode   The node to get data from
	 * @param thingClass The class which will be the type of the children in this listing
	 */
	public Listing(JsonNode dataNode, Class<T> thingClass) {
		super(dataNode);

		this.thingClass = thingClass;
		this.hasChildren = data.has("children");
	}

	/**
	 * Gets a list of children RedditObjects
	 *
	 * @return A list of children
	 */
	public List<T> getChildren() {
		List<T> things = new ArrayList<>();

		if (!hasChildren) {
			return things;
		}

		// rootNode is a JSON array
		for (JsonNode childNode : data.get("children")) {
			if (childNode.get("kind").getTextValue().equalsIgnoreCase("more")) {
				// Ignore "more" objects, use getChildrenMore()
				continue;
			}
			things.add(PARSER.parse(childNode, thingClass));
		}
		return things;
	}

	/**
	 * Whether or not this listing has a "more" element in its children
	 */
	@JsonInteraction
	public Boolean hasMore() {
		if (!hasChildren) {
			return false;
		}

		JsonNode childrenNode = data.get("children");
		// Lookup data->children->last element->kind and check if it equals "more"
		return childrenNode.get(childrenNode.size() - 1).get("kind").getTextValue().equals(ThingType.MORE.getPrefix());
	}

	/**
	 * Gets the "more" element (the last element in the children)
	 *
	 * @return A More object
	 */
	@JsonInteraction(nullable = true)
	public More getMoreChildren() {
		if (!hasChildren || !hasMore()) {
			return null;
		}

		return new More(data.get("children"));
	}

	/**
	 * The full name of the listing that follows before this page, or null if there is no previous page
	 */
	@JsonInteraction(nullable = true)
	public String getBefore() {
		return data("before", String.class);
	}

	/**
	 * The full name of the listing that follows after this page, or null if there is no following page
	 */
	@JsonInteraction(nullable = true)
	public String getAfter() {
		return data("after", String.class);
	}

	/**
	 * Not the same modhash provided upon login. You can reuse the modhash given upon login
	 */
	@JsonInteraction
	public String getModhash() {
		return data("modhash", String.class);
	}

	@Override
	public ThingType getType() {
		return ThingType.LISTING;
	}
}
