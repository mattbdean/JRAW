package net.dean.jraw.http;

/**
 * This class represents the value of the User-Agent header. It attempts to adhere to Reddit's recommended value for
 * User-Agent headers.
 *
 * <p>The Reddit Wiki states:</p>
 *
 * <blockquote>
 * Change your client's User-Agent string to something unique and descriptive,
 * including the target platform, a unique application identifier, a version string,
 * and your username as contact information, in the following format:
 * {@code <platform>:<app ID>:<version string> (by /u/<reddit username>)}
 *
 * <ul>
 *     <li>Example: {@code android:com.example.myredditapp:v1.2.3 (by /u/kemitche)}
 *     <li>Many default User-Agents (like "Python/urllib" or "Java") are drastically limited to encourage unique and
 *         descriptive user-agent strings.
 *     <li>Including the version number and updating it as your build your application allows us to safely block old
 *         buggy/broken versions of your app.
 *     <li><strong>NEVER lie about your user-agent.</strong> This includes spoofing popular browsers and spoofing other
 *         bots. We will ban liars with extreme prejudice.
 * </ul>
 * </blockquote>
 */
public final class UserAgent {
    private final String val;
    private UserAgent(String val) {
        checkPresent(val, "val");
        this.val = val;
    }

    /**
     * Instantiates a new UserAgent in the format recommended by Reddit:
     * {@code <platform>:<app ID>:<version string> (by /u/<reddit username>)}. For example,
     * {@code android:com.example.myredditapp:v1.2.3 (by /u/kemitche)}. Each argument must not be null nor empty.
     *
     * @param platform What this app is running on. Usually something like "android" or "desktop."
     * @param appId The app's unique identifier. Commonly the package name.
     * @param version The app's version.
     * @param redditUsername The primary creator/owner of the app
     * @return A new UserAgent
     */
    public static UserAgent of(String platform, String appId, String version, String redditUsername) {
        checkPresent(platform, "platform");
        checkPresent(appId, "appId");
        checkPresent(version, "version");
        checkPresent(redditUsername, "redditUsername");
        return new UserAgent(String.format("%s:%s:%s (by /u/%s)", platform, appId, version, redditUsername));
    }

    /**
     * Instantiates a new UserAgent with a custom value. This is not recommended, but provided for flexibility.
     * @return A new UserAgent
     */
    public static UserAgent of(String val) {
        checkPresent(val, "val");
        return new UserAgent(val);
    }

    private static void checkPresent(String val, String varName) {
        if (val == null || val.trim().length() == 0) {
            throw new IllegalArgumentException(varName + " was null or empty");
        }
    }

    @Override
    public String toString() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAgent userAgent = (UserAgent) o;

        return val.equals(userAgent.val);

    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }
}
