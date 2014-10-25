package net.dean.jraw.paginators;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Thing;

/**
 * This class provides a template for Paginators that use only a "where" attribute, such as {@code /user/username/{where}}.
 * The way this class assembles the base URI ({@link #getBaseUri()}) works somewhat differently than other paginators.
 * The formula for creating this URI is as follows: {@code {prefix}{where.name().toLowerCase()}{postfix}}. So if {@link #getUriPrefix()}
 * resulted in "/user/username/", the enum value's name was "SUBMITTED", and {@link #getUriPostfix()} resulted in ".json",
 * then the base URI would be "/user/username/submitted.json".
 *
 * @param <T> The type of Thing that
 * @param <U> The type of enum that will be used in place of the "where" parameter.
 */
public abstract class GenericPaginator<T extends Thing, U extends Enum<U>> extends Paginator<T> {
    protected U where;

    /**
     * Instantiates a new GenericPaginator
     * @param creator The RedditClient that will be used to send requests
     * @param thingClass The type of Thing to return
     * @param where The "where" enum value to use
     */
    protected GenericPaginator(RedditClient creator, Class<T> thingClass, U where) {
        super(creator, thingClass);
        this.where = where;
    }

    @Override
    protected final String getBaseUri() {
        String pre = getUriPrefix();
        if (!pre.endsWith("/")) {
            pre += "/";
        }

        return pre + getAsString(where) + getUriPostfix();
    }

    /**
     * Gets the String that will come before all paths for this paginator
     * @return The String that will come before all paths for this paginator
     */
    protected abstract String getUriPrefix();

    /**
     * Gets the String that will come after all paths for this paginator. The default value is ".json".
     * @return The String that will come after all paths for this paginator
     */
    protected String getUriPostfix() { return ".json"; }

    protected String getAsString(U where) {
        return where.name().toLowerCase();
    }

    /**
     * Gets the enum value that will be appended to the base URI
     * @return The enum value that will be appended to the base URI
     */
    public final U getWhere() {
        return where;
    }

    public void setWhere(U where) {
        this.where = where;
        invalidate();
    }
}
