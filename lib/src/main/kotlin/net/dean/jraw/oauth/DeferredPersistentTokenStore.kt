package net.dean.jraw.oauth

import net.dean.jraw.filterValuesNotNull
import net.dean.jraw.models.OAuthData
import net.dean.jraw.models.PersistedAuthData

/**
 * This specific TokenStore abstraction is a way of dealing with the assumption that all `store*` and `fetch*`
 * operations happen in insignificant time.
 *
 * All data is saved in memory until manually told to persist to a data source. This data source may be a file,
 * database, or a cloud storage provider. If the time it takes to persist the data is insignificant, you can enable
 * [autoPersist].
 *
 * When first created, it might be necessary to load the persisted data into memory using [load]. Otherwise the
 * TokenStore could be missing out on some data.
 */
abstract class DeferredPersistentTokenStore<T : DeferredPersistentTokenStore<T>> @JvmOverloads constructor(
    initialData: Map<String, PersistedAuthData> = mapOf()
) : TokenStore {
    protected var memoryData: MutableMap<String, PersistedAuthData> = initialData.toMutableMap()
    private var lastPersistedData: Map<String, PersistedAuthData>? = null

    /** If true, [persist] will automatically be called after the in-memory data is mutated. */
    var autoPersist: Boolean = false

    /** Fetches any data stored in memory about the given username. Useful for debugging. */
    fun inspect(username: String): PersistedAuthData? = memoryData[username]

    /** A list of all usernames that have either a refresh token or some OAuthData associated with it */
    val usernames: List<String>
        get() = memoryData.keys.toList()

    /** Returns true if the in-memory copy of the data differs from the last persisted data */
    fun hasUnsaved() = lastPersistedData != memoryData

    /**
     * Persists the in-memory data to somewhere more permanent. Assume this is a blocking operation. Returns this
     * instance for chaining.
     */
    fun persist(): T {
        // Do less work in the long run
        val actualData = memoryData
            .mapValues { it.value.simplify() }
            .filterValuesNotNull()

        val result = doPersist(actualData)
        this.lastPersistedData = HashMap(memoryData)
        return result
    }

    /**
     * Loads the data from its persistent source. Overwrites any existing data in memory. Assume this is a blocking
     * operation. Returns this instance for chaining.
     */
    fun load() = doLoad()

    /**
     * Does the actual work for persisting data. The given data may contain null values depending on if the user asked
     * to keep insignificant values.
     */
    protected abstract fun doPersist(data: Map<String, PersistedAuthData>): T

    /** Does the actual loading of the persisted data. Assign the new data to [memoryData]. */
    protected abstract fun doLoad(): T

    /** Returns a copy of the data. For testing only. */
    internal fun data(): Map<String, PersistedAuthData> = HashMap(this.memoryData)

    override final fun storeCurrent(username: String, data: OAuthData) {
        if (username == AuthManager.USERNAME_UNKOWN)
            throw IllegalArgumentException("Refusing to store data for unknown username")
        val stored = this.memoryData[username]
        val new = PersistedAuthData.create(data, stored?.refreshToken)
        this.memoryData[username] = new

        if (this.hasUnsaved() && autoPersist)
            persist()
    }

    override final fun storeRefreshToken(username: String, token: String) {
        if (username == AuthManager.USERNAME_UNKOWN)
            throw IllegalArgumentException("Refusing to store data for unknown username")
        val stored = this.memoryData[username]
        val new = PersistedAuthData.create(stored?.current, token)
        this.memoryData[username] = new

        if (this.hasUnsaved() && autoPersist)
            persist()
    }

    override final fun fetchCurrent(username: String): OAuthData? {
        return memoryData[username]?.current
    }

    override final fun fetchRefreshToken(username: String): String? {
        return memoryData[username]?.refreshToken
    }

    override fun deleteCurrent(username: String) {
        val saved = memoryData[username] ?: return

        if (saved.refreshToken == null) {
            memoryData.remove(username)
        } else {
            memoryData[username] = PersistedAuthData.create(null, saved.refreshToken)
        }
    }

    override fun deleteRefreshToken(username: String) {
        val saved = memoryData[username] ?: return

        if (saved.current == null)
            memoryData.remove(username)
        else
            memoryData[username] = PersistedAuthData.create(saved.current, null)
    }
}
