package net.dean.jraw.paginators;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Thing;

import java.util.Arrays;

/**
 * This class provides a template for Paginators that use only a "where" attribute, such as {@code /user/username/{where}}.
 * The way this class assembles the base URI ({@link #getBaseUri()}) works somewhat differently than other paginators.
 * The formula for creating this URI is as follows: {@code {prefix}{where.name().toLowerCase()}{postfix}}. So if
 * {@link #getUriPrefix()} resulted in "/user/username/", the enum value's name was "SUBMITTED", and
 * {@link #getUriPostfix()} resulted in ".json", then the base URI would be "/user/username/submitted.json".
 *
 * @param <T> The type of Thing that
 */
public abstract class GenericPaginator<T extends Thing> extends Paginator<T> {
    protected String where;

    /**
     * Instantiates a new GenericPaginator
     * @param creator The RedditClient that will be used to send requests
     * @param thingClass The type of Thing to return
     * @param where The "where" enum value to use
     */
    protected GenericPaginator(RedditClient creator, Class<T> thingClass, String where) {
        super(creator, thingClass);
        if (!isValidWhereValue(where))
            throw new IllegalArgumentException(String.format("Invalid 'where' value: \"%s\". Expecting one of %s",
                    where, Arrays.toString(getWhereValues())));
        this.where = where;
    }

    @Override
    protected final String getBaseUri() {
        String pre = getUriPrefix();
        if (!pre.endsWith("/")) {
            pre += "/";
        }

        return pre + where + getUriPostfix();
    }

    /**
     * Gets the String that will come before all paths for this paginator
     * @return The String that will come before all paths for this paginator
     */
    protected abstract String getUriPrefix();

    /**
     * Gets the String that will come after all paths for this paginator. The default value is an empty string.
     * @return The String that will come after all paths for this paginator
     */
    protected String getUriPostfix() { return ""; }

    /**
     * Gets the enum value that will be appended to the base URI
     * @return The enum value that will be appended to the base URI
     */
    public final String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
        invalidate();
    }

    /**
     * Gets all the acceptable values of the 'where' value for this GenericPaginator
     * @return A String array of possible 'where' values
     */
    public abstract String[] getWhereValues();

    /**
     * Checks if the given String is a valid 'where' value
     * @param where The String to test
     * @return True if the given String is found in the array returned by {@link #getWhereValues()} (ignoring case),
     *         false if else
     */
    public final boolean isValidWhereValue(String where) {
        if (where == null) {
            return false;
        }
        String[] wheres = getWhereValues();
        for (String str : wheres) {
            if (str.equalsIgnoreCase(where)) {
                return true;
            }
        }

        return false;
    }
}
