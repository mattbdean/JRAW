package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for a JSON-object based API error. Expected formats are:
 *
 * <pre>
 * {
 *   "fields": ["multipath"],
 *   "explanation": "you can't change that multireddit",
 *   "message": "Forbidden",
 *   "reason": "MULTI_CANNOT_EDIT"
 * }
 * </pre>
 *
 * <pre>
 * {
 *   "message": "Forbidden",
 *   "reason": 403
 * }
 * </pre>
 */
@AutoValue
public abstract class ObjectBasedApiExceptionStub implements RedditExceptionStub<ApiException> {
    @Nullable
    @Json(name = "fields") public abstract List<String> getRelevantFields();

    @Nullable
    public abstract String getExplanation();

    @Nullable
    public abstract String getMessage();

    @Nullable
    @Json(name = "reason") public abstract String getCode();

    @Nullable
    @Json(name = "error") public abstract Integer getHttpStatusCode();

    @Override
    @Nullable
    public final ApiException create(NetworkException cause) {
        if (getRelevantFields() != null && getExplanation() != null && getMessage() != null && getCode() != null)
            return new ApiException(getCode(), getExplanation(), getRelevantFields(), cause);

        if (getMessage() != null && getHttpStatusCode() != null)
            return new ApiException(getHttpStatusCode().toString(), getMessage(), new ArrayList<String>(), cause);

        return null;
    }

    public static JsonAdapter<ObjectBasedApiExceptionStub> jsonAdapter(Moshi moshi) {
        return new AutoValue_ObjectBasedApiExceptionStub.MoshiJsonAdapter(moshi);
    }
}
