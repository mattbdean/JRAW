package net.dean.jraw.databind

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import net.dean.jraw.models.DistinguishedStatus
import net.dean.jraw.models.VoteDirection

/**
 * A NullAwareEnumAdapter is a special JsonAdapter for enums that represent exactly one of its values in JSON as null.
 * For example, reddit uses "true" for an upvote, "false" for a downvote, and null for no vote. This class takes care of
 * handling that special case where the value is null and delegates the reset of the work to [read] and [write].
 */
sealed class NullAwareEnumAdapter<T : Enum<T>> : JsonAdapter<T>() {
    /** @inheritDoc */
    final override fun fromJson(reader: JsonReader): T {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<T>()
            return nullValue
        }

        return read(reader)
    }

    /** @inheritDoc */
    final override fun toJson(writer: JsonWriter, value: T?) {
        if (value == nullValue) {
            writer.nullValue()
        } else {
            write(writer, value!!)
        }
    }

    /** The special enum value that is represented as null in the JSON structure */
    protected abstract val nullValue: T

    /** Reads a T from the reader. This T can be any value in the enumeration besides [nullValue]. */
    protected abstract fun read(reader: JsonReader): T

    /** Writes a T to its JSON representation. `value` is guaranteed not to be equal to [nullValue]. */
    protected abstract fun write(writer: JsonWriter, value: T)
}

/**
 * Handles reading and writing VoteDirections. reddit represents an upvote with "true", a downvote with "false", and no
 * vote with a null value.
 */
class VoteDirectionAdapter : NullAwareEnumAdapter<VoteDirection>() {
    override val nullValue: VoteDirection = VoteDirection.NONE

    override fun read(reader: JsonReader): VoteDirection {
        return if (reader.nextBoolean()) VoteDirection.UP else VoteDirection.DOWN
    }

    override fun write(writer: JsonWriter, value: VoteDirection) {
        when (value) {
            VoteDirection.UP -> writer.value(true)
            VoteDirection.DOWN -> writer.value(false)
            else -> throw IllegalStateException("Unknown vote direction: $value")
        }
    }
}

/**
 * Handles reading and writing DistinguishedStatuses. [DistinguishedStatus.NORMAL] is represented by a null value in
 * JSON while every other value is represented as the lowercase name. For example, [DistinguishedStatus.ADMIN] is
 * represented in JSON as the string "admin".
 */
class DistinguishedStatusAdapter : NullAwareEnumAdapter<DistinguishedStatus>() {
    override val nullValue: DistinguishedStatus = DistinguishedStatus.NORMAL

    override fun read(reader: JsonReader): DistinguishedStatus {
        val value = reader.nextString()
        return if (value == "gold-auto")
            DistinguishedStatus.GOLD
        else
            DistinguishedStatus.valueOf(value.toUpperCase())
    }

    override fun write(writer: JsonWriter, value: DistinguishedStatus) {
        writer.value(value.name.toLowerCase())
    }
}
