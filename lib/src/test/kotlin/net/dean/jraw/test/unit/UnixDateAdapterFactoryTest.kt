package net.dean.jraw.test.unit

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.winterbe.expekt.should
import net.dean.jraw.databind.UnixDateAdapterFactory
import net.dean.jraw.databind.UnixTime
import net.dean.jraw.models.Flair
import net.dean.jraw.test.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.lang.reflect.Proxy
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

class UnixDateAdapterFactoryTest : Spek({
    val moshi = Moshi.Builder()
        .add(UnixDateAdapterFactory())
        .build()

    /**
     * Creates a UnixTime instance. Necessary for invoking Moshi.adapter since the version of that method where the
     * consumer provides an annotation class (UnixTime::class.java) will fail to create a UnixTime because it declared
     * methods (precision()). Adapted from Moshi's Types.createJsonQualifierImplementation().
     */
    fun unixTime(precision: TimeUnit = TimeUnit.SECONDS): UnixTime {
        val type = UnixTime::class.java

        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), { _, method, args ->
            when (method.name) {
                "annotationType" -> type
                "toString" -> "@${type.name}()"
                "hashCode" -> 0
                "equals" -> type.isInstance(args!!.first())
                "precision" -> precision
                else -> {
                    throw UnsupportedOperationException("Not implemented: ${method.name}")
                }
            }
        }) as UnixTime
    }

    it("should only produce an Adapter when the @UnixTime annotation is present") {
        // No adapter, Moshi will throw an exception saying it can't serialize platform types (java.*, android.*, etc)
        // without annotations or an explicit JsonAdapter registered for that type. That means UnixDateAdapterFactory
        // didn't produce an adapter.
        expectException(IllegalArgumentException::class) {
            moshi.adapter<Date>(Date::class.java)
        }

        // This should work fine
        moshi.adapter<Date>(Date::class.java, setOf(unixTime()))
            .should.be.an.instanceof(UnixDateAdapterFactory.Adapter::class.java)
    }

    it("should only produce an Adapter for java.util.Date") {
        expectException(IllegalArgumentException::class) {
            moshi.adapter<Flair>(Flair::class.java, setOf(unixTime()))
        }
    }

    describe("Adapter") {
        fun adapter(precision: TimeUnit = TimeUnit.SECONDS) = UnixDateAdapterFactory.Adapter(precision)

        describe("fromJson") {
            it("should return null for boolean or null values") {
                val adapter = adapter()
                listOf("null", "false", "true").forEach {
                    adapter.fromJson(it).should.equal(null)
                }
            }

            it("should properly create a Date object for the source precision") {
                val adapter = adapter(precision = TimeUnit.DAYS)

                // The adapter should convert the given numeric value into the amount of time units after Jan 1, 1970.
                // When the time unit is a day, the value "1" should mean Jan 2, 1970
                val expectedMillis = Date(0L).toInstant().plus(1, ChronoUnit.DAYS).toEpochMilli()
                adapter.fromJson("1").should.equal(Date(expectedMillis))
            }

            it("should throw an IllegalArgumentException if given anything but a null, boolean, or numeric value") {
                val adapter = adapter()
                val values = mapOf(
                    "\"foo\"" to JsonReader.Token.STRING,
                    "{}" to JsonReader.Token.BEGIN_OBJECT,
                    "[]" to JsonReader.Token.BEGIN_ARRAY
                )

                for ((json, token) in values) {
                    val ex = expectException(JsonDataException::class) {
                        adapter.fromJson(json)
                    }
                    ex.message.should.equal("Expected a null, boolean, or numeric value, got ${token.name} at $")
                }
            }
        }

        describe("toJson") {
            it("should serialize nulls") {
                adapter().toJson(null).should.equal("null")
            }

            it("should write non-null values as unix time in the source precision") {
                val epoch = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant()
                fun epochPlusOne(unit: ChronoUnit) = Date(epoch.plus(1, unit).toEpochMilli())

                val units = mapOf(
                    TimeUnit.MINUTES to ChronoUnit.MINUTES,
                    TimeUnit.SECONDS to ChronoUnit.SECONDS,
                    TimeUnit.MILLISECONDS to ChronoUnit.MILLIS
                )

                for ((tu, cu) in units) {
                    adapter(tu).toJson(epochPlusOne(cu)).should.equal("1")
                }

            }
        }

        it("should be able to deserialize JSON created by toJson() without loss of meaning") {
            val adapter = adapter()
            adapter.fromJson(adapter.toJson(Date(0L))).should.equal(Date(0L))
        }
    }
})
