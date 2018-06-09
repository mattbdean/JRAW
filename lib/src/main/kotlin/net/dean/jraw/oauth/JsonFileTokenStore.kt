package net.dean.jraw.oauth

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.PersistedAuthData
import okio.Okio
import java.io.File
import java.io.IOException

/**
 * This class stores its data in a file encoded as JSON. It uses Moshi to persist and load data from that file.
 *
 * ```kt
 * val tokenStore = JsonFileTokenStore.create(someFile)
 *
 * // Make some changes (this is probably done by AuthManager)
 * tokenStore.storeRefreshToken("username", "foobar")
 *
 * // Save changes to disk
 * tokenStore.persist()
 * ```
 *
 * @see DeferredPersistentTokenStore
 */
class JsonFileTokenStore
@JvmOverloads
@Deprecated("Use JsonFileTokenStore.create() instead", replaceWith = ReplaceWith("JsonFileTokenStore.create(saveLocation, initialData)"))
constructor(
    /** Where the persisted data is to be saved to/loaded from */
    private val saveLocation: File,
    initialData: Map<String, PersistedAuthData> = mapOf()
) : DeferredPersistentTokenStore(initialData) {
    private val baseAdapter: JsonAdapter<Map<String, PersistedAuthData>> =
        JrawUtils.moshi.adapter<Map<String, PersistedAuthData>>(ADAPTER_TYPE)

    private var adapter = baseAdapter.indent("") // no indent (compact) by default

    /**
     * Sets the character(s) to indent with. By default, there is no indent; JSON is written in a compact form.
     *
     * @return This TokenStore for chaining.
     */
    fun indent(with: String?): JsonFileTokenStore {
        val indent = with ?: ""
        this.adapter = baseAdapter.indent(indent)
        return this
    }

    @Throws(IOException::class)
    override fun doPersist(data: Map<String, PersistedAuthData>) {
        // Make sure the file's directory exists
        if (!saveLocation.exists() && saveLocation.absoluteFile.parentFile != null) {
            val parent = saveLocation.absoluteFile.parentFile!!
            if (!parent.exists() && !parent.mkdirs())
                throw IOException("Unable to create parent directory $parent")
        }

        val sink = Okio.buffer(Okio.sink(saveLocation))
        adapter.toJson(sink, data)
        sink.close()
    }

    @Throws(IOException::class)
    override fun doLoad(): Map<String, PersistedAuthData> {
        return if (saveLocation.exists() && !saveLocation.isFile)
            throw IOException("Not a file: ${saveLocation.absolutePath}")
        else if (!saveLocation.exists())
            return mapOf()
        else adapter.fromJson(Okio.buffer(Okio.source(saveLocation)))!!
    }

    /** */
    companion object {
        private val ADAPTER_TYPE =
            Types.newParameterizedType(Map::class.java, String::class.java, PersistedAuthData::class.java)

        /**
         * Creates a new JsonFileTokenStore and load whatever data is stored at saveLocation, if that file exists. This
         * will perform I/O and should be considered a blocking call.
         */
        @JvmStatic @JvmOverloads fun create(saveLocation: File, initialData: Map<String, PersistedAuthData> = mapOf()): JsonFileTokenStore {
            @Suppress("DEPRECATION")
            val store = JsonFileTokenStore(saveLocation, initialData)
            store.load()
            return store
        }
    }
}
