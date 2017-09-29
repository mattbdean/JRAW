package net.dean.jraw.http

/**
 * This data class provides convenience methods to create a value for the User-Agent header in the format recommended by
 * the reddit documentation.
 *
 * The Reddit Wiki states:
 *
 * Change your client's User-Agent string to something unique and descriptive,
 * including the target platform, a unique application identifier, a version string,
 * and your username as contact information, in the following format:
 * `<platform>:<app ID>:<version string> (by /u/<reddit username>)`
 *
 * - Example: {@code android:com.example.myredditapp:v1.2.3 (by /u/kemitche)}
 * - Many default User-Agents (like "Python/urllib" or "Java") are drastically limited to encourage unique and
 *   descriptive user-agent strings.
 * - Including the version number and updating it as your build your application allows us to safely block old
 *   buggy/broken versions of your app.
 * - **NEVER lie about your user-agent.** This includes spoofing popular browsers and spoofing other
 *         bots. We will ban liars with extreme prejudice.
 */
data class UserAgent(val value: String) {
    constructor(platform: String, appId: String, version: String, redditUsername: String) : this(
        "$platform:$appId:$version (by /u/$redditUsername)"
    )
}
