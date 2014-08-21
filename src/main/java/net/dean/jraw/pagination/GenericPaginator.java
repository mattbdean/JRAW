package net.dean.jraw.pagination;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.core.Thing;

/**
 * This class provides a template for Paginators that use only a "where" attribute, such as {@code /user/username/{where}}.
 * The way this class assembles the base URI ({@link #getBaseUri()}) works somewhat differently than other paginators.
 * The formula for creating this URI is as follows: {@code {prefix}{where.name().toLowerCase()}{postfix}}. So if {@link #getUriPrefix()}
 * resulted in "/user/username", the enum value's name was "SUBMITTED", and {@link #getUriPostfix()} resulted in ".json",
 * then the base URI would be "/user/username/submitted.json".
 *
 * @param <T> The type that the listings will contain
 * @param <U> The type of enum that will be used in place of the "where" parameter.
 */
public abstract class GenericPaginator<T extends Thing, U extends Enum<U>, V extends GenericPaginator<T, U, V>> extends AbstractPaginator<T> {
	private final U where;

	protected GenericPaginator(Builder<T, U, V> b) {
		super(b);
		this.where = b.where;
	}

	@Override
	protected final String getBaseUri() {
		String pre = getUriPrefix();
		if (!pre.endsWith("/")) {
			pre += "/";
		}

		return String.format("%s%s%s", pre, where.name().toLowerCase(), getUriPostfix());
	}

	/**
	 * Gets the String that will come before all paths for this paginator
	 * @return The String that will come before all paths for this paginator
	 */
	public abstract String getUriPrefix();

	/**
	 * Gets the String that will come after all paths for this paginator. The default value is ".json".
	 * @return The String that will come after all paths for this paginator
	 */
	public String getUriPostfix() { return ".json"; }

	public final U getWhere() {
		return where;
	}

	/**
	 * This class is a generic Builder class that is designed to work with {@link net.dean.jraw.pagination.GenericPaginator}
	 * so that classes that extend it do not necessarily need to create their own Builder class
	 *
	 * @param <T> The type that the listings will contain
	 * @param <U> The type of enum that will be used in place of the "where" parameter.
	 * @param <V> The type of GenericPaginator that this will return
	 */
	public static abstract class Builder<T extends Thing, U extends Enum<U>, V extends GenericPaginator<T, U, V>> extends AbstractPaginator.Builder<T> {
		private final U where;

		/**
		 * Instantiates a new Builder
		 *
		 * @param reddit    The RedditClient to send requests with
		 * @param thingType The type of object to return in the built paginator
		 * @param where     The enum that will be appended to the
		 */
		public Builder(RedditClient reddit, Class<T> thingType, U where) {
			super(reddit, thingType);
			this.where = where;
		}

		@Override
		public abstract V build();
	}
}
