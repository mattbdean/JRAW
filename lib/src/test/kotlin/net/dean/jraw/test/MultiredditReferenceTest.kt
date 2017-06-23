package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.ApiException
import net.dean.jraw.models.MultiredditPatch
import net.dean.jraw.references.MultiredditReference
import net.dean.jraw.test.util.TestConfig.reddit
import net.dean.jraw.test.util.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*

class MultiredditReferenceTest : Spek({
    val rand = SecureRandom()
    fun randomName() = BigInteger(130, rand).toString(32).substring(0..20)

    val undeletedRefs = mutableListOf<MultiredditReference>()

    val me = reddit.me()

    describe("createOrUpdate, about, and delete") {
        it("should create a new multireddit") {
            val ref = me.multi(randomName())
            val desc = "Created ${Date()}"

            // Create the new multireddit (or overwrite it if in the extremely unlikely chance we've generated a name
            // that the user already has)
            val multi = ref.createOrUpdate(MultiredditPatch.Builder()
                .description(desc)
                .displayName(ref.multiredditName)
                .iconName("grooming")
                .keyColor("#FFFFFF")
                .subreddits(listOf("pics", "videos", "funny"))
                .visibility("private")
                .weightingScheme("fresh")
                .build())

            // Make sure we can fetch the data from about()
            ref.about().should.equal(multi)

            // Clean up (and test the delete() method)
            ref.delete()
        }

        it("should throw an ApiException when trying to create a multireddit for another user") {
            expectException(ApiException::class) {
                reddit.user("_vargas_").multi(randomName()).createOrUpdate(MultiredditPatch.Builder().build())
            }
        }
    }

    describe("description and updateDescription") {
        it("should read/update the description") {
            val original = "original description"
            val ref = me.multi(randomName())
            val multi = ref.createOrUpdate(MultiredditPatch.Builder()
                .description(original)
                .build())

            multi.description.should.equal(original)
            val newDesc = "new description"
            ref.updateDescription(newDesc)
            ref.description().should.equal(newDesc)

            undeletedRefs.add(ref)
        }
    }

    afterGroup {
        // Clean up undeleted multireddits
        for (ref in undeletedRefs) {
            try {
                ref.delete()
            } catch (e: Exception) {
                throw IllegalStateException("Unable to delete multireddit ${ref.multiPath}", e)
            }
        }
    }
})
