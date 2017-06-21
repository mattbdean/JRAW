package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.test.util.CredentialsUtil
import net.dean.jraw.test.util.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class UserReferenceTest : Spek({
    describe("about") {
        it("should return an Account") {
            // We authenticated `reddit` using the script credentials, should have the same username
            reddit.me().about().name.should.equal(CredentialsUtil.script.username)

            val name = "_vargas_"
            reddit.user(name).about().name.should.equal(name)
        }
    }

    describe("trophies") {
        it("should return a List of Trophies") {
            // Just make sure it deserializes
            reddit.me().trophies()
            reddit.user("Shitty_Watercolour").trophies()
        }
    }
})
