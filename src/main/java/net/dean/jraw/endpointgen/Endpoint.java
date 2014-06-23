package net.dean.jraw.endpointgen;

/**
 * This class represents a Reddit API endpoint such as {@code /api/login}
 */
public class Endpoint {
	private final String uri;
	private final String category;

	/**
	 * Instantiates a new Endpoint
	 * @param uri The URI that this endpoint uses (such as {@code /api/login})
	 * @param category This endpoint's category, such as "accounts". Can be found <a href="http://www.reddit.com/dev/api">here</a>
	 */
	public Endpoint(String uri, String category) {
		this.uri = uri;
		this.category = category;
	}

	/**
	 * Gets this endpoint's URI
	 *
	 * @return This endpoint's URI
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Gets this endpoint's category
	 *
	 * @return This endpoint's category
	 */
	public String getCategory() {
		return category;
	}

	@Override
	public String toString() {
		return "Endpoint {" +
				"category='" + category + '\'' +
				", uri='" + uri + '\'' +
				'}';
	}
}
