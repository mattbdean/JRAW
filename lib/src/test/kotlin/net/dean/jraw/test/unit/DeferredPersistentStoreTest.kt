package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.filterValuesNotNull
import net.dean.jraw.models.PersistedAuthData
import net.dean.jraw.oauth.AuthManager
import net.dean.jraw.oauth.DeferredPersistentTokenStore
import net.dean.jraw.test.createMockOAuthData
import net.dean.jraw.test.expectException
import net.dean.jraw.test.withExpiration
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class DeferredPersistentStoreTest : Spek({
    // Sample data
    val oauthData = createMockOAuthData()
    val refreshToken = "<refresh token>"
    val username = "username"
    val data = mapOf(username to PersistedAuthData.create(oauthData, refreshToken))

    // Constructor shortcut
    fun newStore(initialData: Map<String, PersistedAuthData> = mapOf()) = MockDeferredPersistentTokenStore(initialData)

    describe("load") {
        it("should make the persisted data available") {
            val store = newStore()
            store._persisted = data.toMutableMap()
            store.load()

            store.fetchCurrent(username).should.equal(oauthData)
            store.fetchRefreshToken(username).should.equal(refreshToken)
        }
    }

    describe("persist") {
        it("should save the data") {
            val store = newStore(data)
            store._persisted.should.be.empty

            store.persist()
            store._persisted.should.not.be.empty

            store._persisted.should.equal(data)

            store.hasUnsaved().should.be.`false`
        }

        it("shouldn't save usernames with expired data by default") {
            val insignifcantData = mapOf(username to PersistedAuthData.create(oauthData.withExpiration(Date(0L)), null))
            insignifcantData[username]!!.isSignificant.should.be.`false`
            val store = newStore(insignifcantData)

            store.persist()
            store._persisted.should.be.empty
        }

        it("should persist expired data as null") {
            val store = newStore(mapOf(username to PersistedAuthData.create(oauthData.withExpiration(Date(0L)), refreshToken)))
                .persist()

            store._persisted[username]!!.should.equal(PersistedAuthData.create(null, refreshToken))
        }
    }

    describe("hasUnsaved") {
        it("should change based on whether there are unsaved changes") {
            val store = newStore(data).persist()
            val name = store.usernames[0]

            val prev = store.fetchRefreshToken(name)!!
            store.storeRefreshToken(name, prev.repeat(2))

            store.hasUnsaved().should.be.`true`

            store.storeRefreshToken(name, prev)
            store.hasUnsaved().should.be.`false`
        }
    }

    describe("autoPersist") {
        it("should persist changes immediately after storing data") {
            val store = newStore()
            store.autoPersist = true

            store._persisted[username]?.refreshToken.should.be.`null`
            store.storeRefreshToken(username, "foo")
            store._persisted[username]?.refreshToken.should.equal("foo")

        }
    }

    describe("inspect") {
        it("shouldn't keep empty auth data references") {
            val store = newStore()
            store.inspect(username).should.be.`null`

            store.storeRefreshToken(username, "foo")
            store.inspect(username).should.equal(PersistedAuthData.create(null, "foo"))

            store.deleteRefreshToken(username)
            store.inspect(username).should.be.`null`
        }
    }

    describe("usernames") {
        it("shouldn't include usernames that once had data but now don't") {
            val store = newStore()
            store.storeRefreshToken(username, "foo")
            store.usernames.should.equal(listOf(username))

            store.deleteRefreshToken(username)
            store.usernames.should.be.empty
        }
    }

    describe("storeCurrent/storeRefreshToken") {
        it("should not accept data for a username of USERNAME_USERLESS") {
            val store = newStore()
            expectException(IllegalArgumentException::class) {
                store.storeRefreshToken(AuthManager.USERNAME_UNKOWN, "")
            }
            expectException(IllegalArgumentException::class) {
                store.storeCurrent(AuthManager.USERNAME_UNKOWN, createMockOAuthData())
            }
        }
    }
})

class MockDeferredPersistentTokenStore(initialData: Map<String, PersistedAuthData>) :
    DeferredPersistentTokenStore<MockDeferredPersistentTokenStore>(initialData) {

    var _persisted: MutableMap<String, PersistedAuthData?> = HashMap()

    override fun doPersist(data: Map<String, PersistedAuthData?>): MockDeferredPersistentTokenStore {
        this._persisted = data.toMutableMap()
        return this
    }

    override fun doLoad(): MockDeferredPersistentTokenStore {
        this.memoryData = this._persisted.filterValuesNotNull().toMutableMap()
        return this
    }
}
