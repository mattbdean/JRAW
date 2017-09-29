package net.dean.jraw.databind

import com.squareup.moshi.*
import net.dean.jraw.models.Listing
import net.dean.jraw.models.NestedIdentifiable
import net.dean.jraw.models.Submission
import net.dean.jraw.models.internal.SubmissionData
import java.lang.reflect.Type

class SubmissionDataAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>?, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        if (rawType != SubmissionData::class.java)
            return null

        val submissionsAdapter = moshi.adapter<Listing<Submission>>(
            Types.newParameterizedType(Listing::class.java, Submission::class.java), Enveloped::class.java)
        val commentsAdapter = moshi.adapter<Listing<NestedIdentifiable>>(
            Types.newParameterizedType(Listing::class.java, NestedIdentifiable::class.java), Enveloped::class.java)

        return SubmissionDataAdapter(submissionsAdapter, commentsAdapter)
    }

    private class SubmissionDataAdapter(
        val submissionsAdapter: JsonAdapter<Listing<Submission>>,
        val commentsAdapter: JsonAdapter<Listing<NestedIdentifiable>>
    ) : JsonAdapter<SubmissionData>() {
        override fun toJson(writer: JsonWriter?, value: SubmissionData?) {
            TODO("not implemented")
        }

        override fun fromJson(reader: JsonReader): SubmissionData? {
            reader.beginArray()
            val submissions = submissionsAdapter.fromJson(reader)
            val comments = commentsAdapter.fromJson(reader)
            reader.endArray()

            return SubmissionData.create(submissions, comments)
        }
    }
}
