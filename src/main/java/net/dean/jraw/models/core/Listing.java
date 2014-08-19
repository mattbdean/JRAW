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

	private List<T> children;
	private More more;

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
		initChildren();
	}

	private void initChildren() {
		children = new ArrayList<>();

		// rootNode is a JSON array
		try {
			for (JsonNode childNode : data.get("children")) {
				if (childNode.get("kind").getTextValue().equalsIgnoreCase("more")) {
					this.more = new More(childNode.get("data"));
				}
				children.add(PARSER.parse(childNode, thingClass));
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a list of children RedditObjects
	 *
	 * @return A list of children
	 */
	@JsonInteraction
	public List<T> getChildren() {
		return children;
	}


	/**
	 * Gets the "more" element (the last element in the children)
	 *
	 * @return A More object
	 */
	@JsonInteraction(nullable = true)
	public More getMoreChildren() {
		return more;
	}

	/**
	 * The full name of the Thing that follows before this page, or null if there is no previous page
	 * @return The full name of the Thing that comes before this one
	 */
	@JsonInteraction(nullable = true)
	public String getBefore() {
		return data("before");
	}

	/**
	 * The full name of the Thing that follows after this page, or null if there is no following page
	 * @return The full name of the Thing that comes after this page
	 */
	@JsonInteraction(nullable = true)
	public String getAfter() {
		return data("after");
	}

	/**
	 * Not the same modhash provided upon login. You can reuse the modhash given upon login
	 * @return A modhash
	 */
	@JsonInteraction
	public String getModhash() {
		return data("modhash");
	}

	@Override
	public ThingType getType() {
		return ThingType.LISTING;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Listing listing = (Listing) o;

		return data.equals(listing.data);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + thingClass.hashCode();
		result = 31 * result + children.hashCode();
		result = 31 * result + (hasChildren ? 1 : 0);
		return result;
	}
}
