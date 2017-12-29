package net.dean.jraw

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import net.dean.jraw.databind.*
import net.dean.jraw.models.*
import net.dean.jraw.models.internal.LabeledMultiDescription
import net.dean.jraw.models.internal.TrophyList
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

/**
 * A set of utility methods and properties used throughout the project
 */
object JrawUtils {
    /** A Moshi instance configured with all the proper JsonAdapter(Factory) instances to handle all JRAW types. */
    @JvmField val moshi: Moshi = Moshi.Builder()
        .add(UnixDateAdapterFactory())
        .add(RepliesAdapterFactory())
        .add(SubmissionDataAdapterFactory())
        .add(SimpleFlairInfoListingAdapterFactory())
        .add(EnvelopedListAdapterFactory())
        .add(RedditModelAdapterFactory(mapOf(
            KindConstants.COMMENT to Comment::class.java,
            KindConstants.ACCOUNT to Account::class.java,
            KindConstants.SUBMISSION to Submission::class.java,
            KindConstants.SUBREDDIT to Subreddit::class.java,
            KindConstants.TROPHY to Trophy::class.java,
            KindConstants.TROPHY_LIST to TrophyList::class.java,
            KindConstants.LABELED_MULTI_DESC to LabeledMultiDescription::class.java,
            KindConstants.LIVE_THREAD to LiveThread::class.java,
            KindConstants.LIVE_UPDATE to LiveUpdate::class.java,
            KindConstants.MORE_CHILDREN to MoreChildren::class.java,
            KindConstants.MESSAGE to Message::class.java,
            KindConstants.WIKI_PAGE to WikiPage::class.java
        )))
        .add(ModelAdapterFactory.create())
        .add(DistinguishedStatus::class.java, DistinguishedStatusAdapter())
        .add(VoteDirection::class.java, VoteDirectionAdapter())
        .add(RedditExceptionStubAdapterFactory())
        .add(LiveWebSocketUpdateAdapterFactory())
        .build()

    /** Creates a JsonAdapter for an implied type. Convenience function using reified generics. */
    @JvmStatic inline fun <reified T> adapter(): JsonAdapter<T> = moshi.adapter(T::class.java)

    /**
     * Creates a JsonAdapter for an implied type with the given annotation. Convenience function using reified generics.
     */
    @JvmStatic inline fun <reified T> adapter(annotationType: Class<out Annotation>): JsonAdapter<T> =
        moshi.adapter(T::class.java, annotationType)

    /**
     * Parses a URL-encoded string into a map. Most commonly used when parsing a URL's query or a
     * `application/x-www-form-urlencoded` HTTP body.
     *
     * ```kotlin
     * val map = parseUrlEncoded("foo=bar&baz=qux")
     * map.get("foo") // -> "bar"
     * map.get("baz") // -> "qux"
     * ```
     */
    @JvmStatic fun parseUrlEncoded(str: String): Map<String, String> {
        if (str.isBlank()) return emptyMap()
        val map: MutableMap<String, String> = HashMap()
        val pairs = str.split("&")
        for (pair in pairs) {
            val parts = pair.split("=")
            if (parts.size != 2)
                throw IllegalArgumentException("Invalid number of elements, expected 2, got $parts from input segment" +
                    " '$pair'")
            map[urlDecode(parts[0])] = urlDecode(parts[1])
        }

        return map
    }

    /**
     * Constructs a Map<String, String> based on the given keys and values. `keysAndValues[0]` is the key for
     * `keysAndValues[1]`, and so on.
     *
     * ```kotlin
     * val map = mapOf(
     *     "foo", "bar",
     *     "baz", "qux"
     * )
     *
     * map.get("foo") -> "bar"
     * map.get("baz") -> "qux"
     * ```
     */
    @JvmStatic fun mapOf(vararg keysAndValues: String): Map<String, String> {
        if (keysAndValues.isEmpty()) return emptyMap()
        if (keysAndValues.size % 2 == 1) throw IllegalArgumentException("Expecting an even amount of keys and values")

        val map: MutableMap<String, String> = HashMap()
        for (i in keysAndValues.indices step 2) {
            map[keysAndValues[i]] = keysAndValues[i + 1]
        }

        return map
    }

    /**
     * URL-encodes the given String in UTF-8. Equivalent to:
     *
     * ```kotlin
     * URLEncoder.encode(str, "UTF-8")
     * ```
     */
    @JvmStatic fun urlEncode(str: String): String = URLEncoder.encode(str, "UTF-8")

    /**
     * URL-decodes the given String in UTF-8. Equivalent to:
     *
     * ```kotlin
     * URLDecoder.decode(str, "UTF-8")
     * ```
     */
    @JvmStatic fun urlDecode(str: String): String = URLDecoder.decode(str, "UTF-8")
}
