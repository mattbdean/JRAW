package net.dean.jraw.oauth

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.PersistedAuthData
import okio.Okio
import java.io.File
import java.io.IOException

class JsonFileTokenStore @JvmOverloads constructor(
    private val saveLocation: File,
    initialData: Map<String, PersistedAuthData> = mapOf()
) : DeferredPersistentTokenStore<JsonFileTokenStore>(initialData) {
    private val baseAdapter: JsonAdapter<Map<String, PersistedAuthData>> =
        JrawUtils.moshi.adapter<Map<String, PersistedAuthData>>(ADAPTER_TYPE)

    private var adapter = baseAdapter.indent("") // no indent (compact) by default

    fun indent(with: String?): JsonFileTokenStore {
        this.adapter = baseAdapter.indent(with ?: "")
        return this
    }

    @Throws(IOException::class)
    override fun doPersist(data: Map<String, PersistedAuthData?>): JsonFileTokenStore {
        // Make sure the file's directory exists
        if (!saveLocation.exists() && saveLocation.absoluteFile.parentFile != null) {
            val parent = saveLocation.absoluteFile.parentFile!!
            if (!parent.exists() && !parent.mkdirs())
                throw IOException("Unable to create parent directory $parent")
        }

        val sink = Okio.buffer(Okio.sink(saveLocation))
        adapter.toJson(sink, memoryData)
        sink.close()
        return this
    }

    override fun doLoad(): JsonFileTokenStore {
        if (!saveLocation.exists()) {
            this.memoryData = mutableMapOf()
            return this
        }

        val persisted = adapter.fromJson(Okio.buffer(Okio.source(saveLocation)))!!
        this.memoryData = persisted as MutableMap<String, PersistedAuthData>
        return this
    }

    companion object {
        private val ADAPTER_TYPE =
            Types.newParameterizedType(Map::class.java, String::class.java, PersistedAuthData::class.java)
    }
}
