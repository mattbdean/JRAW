package net.dean.jraw.references

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import net.dean.jraw.*
import net.dean.jraw.databind.RedditModelEnvelope
import net.dean.jraw.models.KarmaBySubreddit
import net.dean.jraw.models.LiveThreadPatch
import net.dean.jraw.models.MultiredditPatch
import net.dean.jraw.models.Subreddit
import net.dean.jraw.models.internal.GenericJsonResponse
import net.dean.jraw.pagination.DefaultPaginator
import okhttp3.MediaType
import okhttp3.RequestBody

class SelfUserReference(reddit: RedditClient) : UserReference(reddit, reddit.requireAuthenticatedUser()) {
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
    fun subreddits(where: String): DefaultPaginator.Builder<Subreddit> {
        return DefaultPaginator.Builder.create(reddit, "/subreddits/mine/${JrawUtils.urlEncode(where)}")
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
}
