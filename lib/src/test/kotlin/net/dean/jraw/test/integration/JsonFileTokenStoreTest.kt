package net.dean.jraw.test.integration

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import com.winterbe.expekt.should
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.PersistedAuthData
import net.dean.jraw.oauth.JsonFileTokenStore
import net.dean.jraw.test.createMockOAuthData
import net.dean.jraw.test.randomName
import okio.Okio
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.util.*

class JsonFileTokenStoreTest : Spek({
    /** Creates a new file and writes the given data as JSON to it */
    fun tempFile(data: Map<String, PersistedAuthData> = mapOf(), createFile: Boolean = true): File {
        val tmp = File(System.getProperty("java.io.tmpdir"), randomName() + ".json")
        tmp.deleteOnExit()

        if (createFile)
            if (data.isNotEmpty()) {
                // Write the given data
                val adapter = JrawUtils.moshi.adapter<Map<String, PersistedAuthData>>(
                    Types.newParameterizedType(Map::class.java, String::class.java, PersistedAuthData::class.java))
                val sink = Okio.buffer(Okio.sink(tmp))
                adapter.indent("  ").toJson(sink, data)
                sink.close()
            } else {
                // Create the file, aka `touch $file` for the *nix guys (https://stackoverflow.com/q/1406473)
                tmp.setLastModified(Date().time)
            }
        return tmp
    }

    fun newStore(f: File, initialData: Map<String, PersistedAuthData> = mapOf()) = JsonFileTokenStore(f, initialData)

    val oauthData = createMockOAuthData()
    val refreshToken = "<refresh token>"
    val username = "username"
    val data = mapOf(username to PersistedAuthData.create(oauthData, refreshToken))

    describe("persist") {
        it("should save the data as parsable JSON") {
            val f = tempFile()
            val store = JsonFileTokenStore(f, data)
            f.length().should.equal(0L)

            store.persist()
            f.length().should.be.above(0)

            val adapter: JsonAdapter<Map<String, PersistedAuthData>> =
                JrawUtils.moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, PersistedAuthData::class.java))

            adapter.fromJson(Okio.buffer(Okio.source(f))).should.equal(data)
        }
    }

    describe("load") {
        it("should do nothing if the file doesn't exist") {
            val store = newStore(tempFile(createFile = false))
            store.load().data().should.be.`empty`
        }

        it("should load data from a JSON file") {
            val store = newStore(tempFile(data))
            store.load().data().should.equal(data)
        }

        it("should handle null PersistedAuthData properties") {
            // Should be fine, just make sure nothing weird is going in with our Moshi configuration
            val dataWithNulls = PersistedAuthData.create(null, refreshToken)
            newStore(tempFile(mapOf(username to dataWithNulls)))
                .load()
                .inspect(username)
                .should.equal(dataWithNulls)
        }
    }

    describe("indent") {
        it("should use an indent when specified") {
            val f = tempFile()
            val store = newStore(f, data).indent(null).persist()

            // We can't easily test to make sure that it's using the correct indent, but we can observe the change in
            // file size. If we increase the indent by 1 character every time we persist, we expect to see a constant
            // increase in the file size. If the increase is constant, we can trust that Moshi is doing its job properly.

            // Increase the indent by 1 and record the file size after persisting
            val lengths = (1 until 5).map {
                store.indent(" ".repeat(it)).persist()
                f.length()
            }

            // Compute the difference in size for each indent setting.
            val sizeDifferences = lengths.mapIndexed { index, length ->
                // Don't care about the first element since we have nothing to compare it with
                if (index == 0) -1 else length - lengths[index - 1]
            }.drop(1)

            sizeDifferences.distinct().should.have.size(1)
        }
    }

    describe("persist + load") {
        it("should have the same data after consecutively persisting and loading for significant data") {
            // Assumption
            data.forEach { it.value.isSignificant.should.be.`true` }

            val f = tempFile()
            newStore(f, data).persist()
            newStore(f).load().data().should.equal(data)
        }
    }
})
