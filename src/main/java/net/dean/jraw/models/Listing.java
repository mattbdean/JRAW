package net.dean.jraw.models;

import java.util.ArrayList;

/**
 * Represents a listing of Things. A Listing has four main keys: before, after, modhash, and its children
 *
 * @author Matthew Dean
 */
public class Listing<T extends Thing> extends ArrayList<T> {
	/** The full name of the Thing that comes before this listing */
	@JsonAttribute(jsonName = "before")
	private String before;

	/** The full name of the Thing that comes directly after this listing*/
	@JsonAttribute(jsonName = "after")
	private String after;

	/** Not the same modhash as the one given upon login. You do not need to update your modhash to this one */
	@JsonAttribute(jsonName = "modhash")
	private String modhash;
}
