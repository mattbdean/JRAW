package net.dean.jraw.models;

/**
 * Represents the base class of all objects defined in the Reddit API. More information is available
 * <a href="https://github.com/reddit/reddit/wiki/JSON">here</a>.
 *
 * @author Matthew Dean
 */
public abstract class Thing {
	/** This item's full identifier, e.g. "8xwlg" */
	@JsonAttribute(jsonName = "id")
	protected String id;

	/** Fullname of the thing, e.g. "t1_c3v7f8u" */
	@JsonAttribute(jsonName = "name")
	protected String name;

	/** Gets this Thing's full identifier, e.g. "8xwlg" */
	public String getId() {
		return id;
	}

	/** Gets the fullname of this Thing, e.g. "t1_c3v7f8u" */
	public String getName() {
		return name;
	}

	/**
	 * Gets the type of this Thing. Will always be constant for every class. For example, every
	 * ${@link net.dean.jraw.models.Account} class will always return ${@link net.dean.jraw.models.ThingType#ACCOUNT}.
	 */
	public abstract ThingType getType();
}
