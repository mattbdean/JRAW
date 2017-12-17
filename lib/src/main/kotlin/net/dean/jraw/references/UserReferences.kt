package net.dean.jraw.references

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import net.dean.jraw.*
import net.dean.jraw.JrawUtils.urlEncode
import net.dean.jraw.databind.Enveloped
import net.dean.jraw.models.*
import net.dean.jraw.models.internal.GenericJsonResponse
import net.dean.jraw.models.internal.RedditModelEnvelope
import net.dean.jraw.models.internal.TrophyList
import net.dean.jraw.pagination.BarebonesPaginator
import net.dean.jraw.pagination.DefaultPaginator
import okhttp3.MediaType
import okhttp3.RequestBody

sealed class UserReference<out T : UserFlairReference>(reddit: RedditClient, val username: String) : AbstractReference(reddit) {
    abstract val isSelf: Boolean

    @EndpointImplementation(Endpoint.GET_ME, Endpoint.GET_USER_USERNAME_ABOUT)
    fun about(): Account {
        val body = reddit.request {
            it.path(if (isSelf) "/api/v1/me" else "/user/$username/about")
        }.body

        // /api/v1/me returns an Account that isn't wrapped with the data/kind nodes
        if (isSelf)
            return JrawUtils.adapter<Account>().fromJson(body)!!
        return JrawUtils.adapter<Account>(Enveloped::class.java).fromJson(body)!!
    }

    @EndpointImplementation(Endpoint.GET_ME_TROPHIES, Endpoint.GET_USER_USERNAME_TROPHIES)
    fun trophies(): List<Trophy> {
        return reddit.request {
            if (isSelf)
                it.endpoint(Endpoint.GET_ME_TROPHIES)
            else
                it.endpoint(Endpoint.GET_USER_USERNAME_TROPHIES, username)
        }.deserializeEnveloped<TrophyList>().trophies
    }

    /**
     * Creates a new [Paginator.Builder] which can iterate over a user's public history.
     *
     * Possible `where` values:
     *
     * - `overview` — submissions and comments
     * - `submitted` — only submissions
     * - `comments` — only comments
     * - `gilded` — submissions and comments which have received reddit Gold
     *
     * If this user reference is for the currently logged-in user, these `where` values can be used:
     *
     * - `upvoted`
     * - `downvoted`
     * - `hidden`
     * - `saved`
     *
     * Only `overview`, `submitted`, and `comments` are sortable.
     */
    @EndpointImplementation(Endpoint.GET_USER_USERNAME_WHERE, type = MethodType.NON_BLOCKING_CALL)
    fun history(where: String): DefaultPaginator.Builder<PublicContribution<*>, UserHistorySort> {
        // Encode URLs to prevent accidental malformed URLs
        return DefaultPaginator.Builder.create(reddit, "/user/${urlEncode(username)}/${urlEncode(where)}",
            sortingAlsoInPath = false)
    }

    /**
     * Creates a [MultiredditReference] for a multireddit that belongs to this user.
     */
    fun multi(name: String) = MultiredditReference(reddit, username, name)

    /**
     * Lists the multireddits this client is able to view.
     *
     * If this UserReference is for the logged-in user, all multireddits will be returned. Otherwise, only public
     * multireddits will be returned.
     */
    @EndpointImplementation(Endpoint.GET_MULTI_MINE, Endpoint.GET_MULTI_USER_USERNAME)
    fun listMultis(): List<Multireddit> {
        val res = reddit.request {
            if (isSelf) {
                it.endpoint(Endpoint.GET_MULTI_MINE)
            } else {
                it.endpoint(Endpoint.GET_MULTI_USER_USERNAME, username)
            }
        }

        val type = Types.newParameterizedType(List::class.java, Multireddit::class.java)
        val adapter = JrawUtils.moshi.adapter<List<Multireddit>>(type, Enveloped::class.java)

        return res.deserializeWith(adapter)
    }

    /**
     * Returns a [UserFlairReference] for this user for the given subreddit. If this user is not the authenticated user,
     * the authenticated must be a moderator of the given subreddit to access anything specific to this user.
     */
    abstract fun flairOn(subreddit: String): T
}

class SelfUserReference(reddit: RedditClient) : UserReference<SelfUserFlairReference>(reddit, reddit.requireAuthenticatedUser()) {
    override val isSelf = true

    private val prefsAdapter: JsonAdapter<Map<String, Any>> by lazy {
        val type = Types.newParameterizedType(Map::class.java, String::class.java, Object::class.java)
        JrawUtils.moshi.adapter<Map<String, Any>>(type)
    }

    fun inbox() = InboxReference(reddit)

    /**
     * Creates a Multireddit (or updates it if it already exists).
     *
     * This method is equivalent to
     *
     * ```kotlin
     * userReference.multi(name).createOrUpdate(patch)
     * ```
     *
     * and provided for semantics.
     */
    fun createMulti(name: String, patch: MultiredditPatch) = multi(name).createOrUpdate(patch)

    /**
     * Creates a live thread. The only property that's required to be non-null in the LiveThreadPatch is
     * [title][LiveThreadPatch.title].
     *
     * @see LiveThreadReference.edit
     */
    @EndpointImplementation(Endpoint.POST_LIVE_CREATE)
    fun createLiveThread(data: LiveThreadPatch): LiveThreadReference {
        val res = reddit.request {
            it.endpoint(Endpoint.POST_LIVE_CREATE)
                .post(data.toRequestMap())
        }.deserialize<GenericJsonResponse>()

        val id = res.json?.data?.get("id") as? String ?:
            throw IllegalArgumentException("Could not find ID")

        return LiveThreadReference(reddit, id)
    }

    /**
     * Gets a Map of preferences set at [https://www.reddit.com/prefs].
     *
     * Likely to throw an [ApiException] if authenticated via application-only credentials
     */
    @EndpointImplementation(Endpoint.GET_ME_PREFS)
    @Throws(ApiException::class)
    fun prefs(): Map<String, Any> {
        return reddit.request { it.endpoint(Endpoint.GET_ME_PREFS) }.deserializeWith(prefsAdapter)
    }

    /**
     * Patches over certain user preferences and returns all preferences.
     *
     * Although technically you can send any value as a preference value, generally only strings and booleans are used.
     * See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_prefs) for a list of all available preferences.
     *
     * Likely to throw an [ApiException] if authenticated via application-only credentials
     */
    @EndpointImplementation(Endpoint.PATCH_ME_PREFS)
    @Throws(ApiException::class)
    fun patchPrefs(newPrefs: Map<String, Any>): Map<String, Any> {
        val body = RequestBody.create(MediaType.parse("application/json"), prefsAdapter.toJson(newPrefs))
        return reddit.request { it.endpoint(Endpoint.PATCH_ME_PREFS).patch(body) }.deserialize()
    }

    /**
     * Returns a Paginator builder for subreddits the user is associated with
     *
     * Possible `where` values:
     *
     * - `contributor`
     * - `moderator`
     * - `subscriber`
     */
    @EndpointImplementation(Endpoint.GET_SUBREDDITS_MINE_WHERE, type = MethodType.NON_BLOCKING_CALL)
    fun subreddits(where: String): BarebonesPaginator.Builder<Subreddit> {
        return BarebonesPaginator.Builder.create(reddit, "/subreddits/mine/${JrawUtils.urlEncode(where)}")
    }

    /**
     * Fetches a breakdown of comment and link karma by subreddit for the user
     */
    @EndpointImplementation(Endpoint.GET_ME_KARMA)
    fun karma(): List<KarmaBySubreddit> {
        val json = reddit.request {
            it.endpoint(Endpoint.GET_ME_KARMA)
        }

        // Our data is represented by RedditModelEnvelope<List<KarmaBySubreddit>> so we need to create a Type instance
        // that reflects that
        val listType = Types.newParameterizedType(List::class.java, KarmaBySubreddit::class.java)
        val type = Types.newParameterizedType(RedditModelEnvelope::class.java, listType)

        // Parse the envelope and return its data
        val adapter = JrawUtils.moshi.adapter<RedditModelEnvelope<List<KarmaBySubreddit>>>(type)
        val parsed = adapter.fromJson(json.body)!!
        return parsed.data
    }

    override fun flairOn(subreddit: String): SelfUserFlairReference = SelfUserFlairReference(reddit, subreddit)
}

class OtherUserReference(reddit: RedditClient, username: String) : UserReference<OtherUserFlairReference>(reddit, username) {
    override val isSelf = false

    override fun flairOn(subreddit: String): OtherUserFlairReference = OtherUserFlairReference(reddit, subreddit, username)
}
