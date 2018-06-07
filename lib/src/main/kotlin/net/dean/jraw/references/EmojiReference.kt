package net.dean.jraw.references

import com.squareup.moshi.Types
import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException
import net.dean.jraw.models.Emoji
import net.dean.jraw.models.internal.EmojiS3UploadLeaseResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * A reference to emojis on a particular subreddit. Emojis are mainly used in user and submission flair. See
 * [here](https://www.reddit.com/comments/7xjt6g) for more information.
 */
class EmojiReference(reddit: RedditClient, val subreddit: String) : AbstractReference(reddit) {
    /**
     * Lists all emojis available for use in this subreddit
     */
    fun list(): List<Emoji> {
        return reddit.request {
            it.endpoint(Endpoint.GET_SUBREDDIT_EMOJIS_ALL, subreddit)
        }.deserializeWith(emojiListAdapter)
    }

    /**
     * Uploads the given file to be used in rich submission and user flair. Convenience method for calling the method
     * with the same name that takes a byte array instead of a file.
     *
     * @param file A
     * @param emojiName How users will reference this emoji. For example, an emoji with the name "test" would be used
     * like this: `:test:`
     * @param contentType For manually specifying a Content-Type. Not necessary if the file's name ends with ".png",
     * ".jpg", or ".jpeg".
     */
    @JvmOverloads
    fun upload(file: File, emojiName: String, contentType: String? = null) = upload(
        bytes = file.readBytes(),
        emojiName = emojiName,
        contentType = contentType ?: determineMediaType(file),
        fileName = file.name
    )

    /**
     * Uploads the given byte array that contains a PNG or JPEG to be used as an emoji for rich flair in this subreddit.
     *
     * @param bytes An array of bytes that represents a PNG or JPEG image.
     * @param emojiName How users will reference this emoji. For example, an emoji with the name "test" would be used
     * like this: `:test:`
     * @param contentType The Content-Type of the image. The Content-Type for a PNG is "image/png" and "image/jpeg" for
     * a JPEG.
     * @param fileName The original name of the file from where the bytes were read from.
     */
    @EndpointImplementation(Endpoint.POST_SUBREDDIT_EMOJI_ASSET_UPLOAD_S3, Endpoint.POST_SUBREDDIT_EMOJI)
    fun upload(bytes: ByteArray, emojiName: String, contentType: String, fileName: String) {
        val mediaType = MediaType.parse(contentType) ?:
            throw IllegalArgumentException("Not a well-formed Content-Type: '$contentType'")

        val uploadLeaseRes = reddit.request {
            it.endpoint(Endpoint.POST_SUBREDDIT_EMOJI_ASSET_UPLOAD_S3, subreddit)
                .post(mapOf(
                    "filepath" to fileName,
                    "mimetype" to mediaType.toString()
                ))
        }.deserialize<EmojiS3UploadLeaseResponse>()

        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        // All of these fields should be sent in the multipart/form-data request
        val fields = uploadLeaseRes.s3UploadLease.fields
            .map { it.name to it.value }
            .toMap()

        fields.forEach {
            builder.addFormDataPart(it.key, it.value)
        }

        // The file must be specified after all the metadata
        builder.addFormDataPart("file", fileName, RequestBody.create(mediaType, bytes))

        // Upload the file to the temp S3 bucket provided by reddit
        val s3Res = reddit.http.execute(HttpRequest.Builder()
            // There's no protocol provided, add our own
            .url("https:" + uploadLeaseRes.s3UploadLease.action)
            .post(builder.build())
            .build())

        // Make sure the upload succeeded
        if (!s3Res.successful) {
            throw NetworkException(s3Res)
        }

        // Let reddit know we've uploaded the asset to S3
        reddit.request {
            it.endpoint(Endpoint.POST_SUBREDDIT_EMOJI, subreddit)
                .post(mapOf(
                    "name" to emojiName,
                    "s3_key" to fields["key"]!!
                ))
        }
    }

    /**
     * Deletes an emoji with the given name. Throws an ApiException if that emoji doesn't exist.
     */
    @EndpointImplementation(Endpoint.DELETE_SUBREDDIT_EMOJI_EMOJI_NAME)
    fun delete(name: String) {
        reddit.request {
            it.endpoint(Endpoint.DELETE_SUBREDDIT_EMOJI_EMOJI_NAME, subreddit, name)
                .delete()
        }
    }

    private fun determineMediaType(file: File): String {
        // It'd probably be cooler to use read the first 8 or so bytes of the file to know for sure, but this is a lot
        // faster (in practice and to type)
        return if (file.extension == "png")
            "image/png"
        else if (file.extension == "jpg" || file.extension == "jpeg")
            "image/jpeg"
        else
            throw IllegalArgumentException("Unknown extension: ${file.extension} (${file.absolutePath})")
    }

    companion object {
        private val emojiListAdapter by lazy {
            JrawUtils.moshi.adapter<List<Emoji>>(Types.newParameterizedType(List::class.java, Emoji::class.java))
        }
    }
}
