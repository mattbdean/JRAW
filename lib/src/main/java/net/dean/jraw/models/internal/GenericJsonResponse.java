package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.ApiException;
import net.dean.jraw.RateLimitException;
import net.dean.jraw.RedditException;
import net.dean.jraw.http.NetworkException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Used to model a JSON response like this:
 *
 * <pre>
 * {
 *   "json":{
 *     "errors":[],
 *     "data":{
 *       "foo": "bar",
 *       "baz": "qux"
 *     }
 *   }
 * }
 * </pre>
 */
@AutoValue
public abstract class GenericJsonResponse implements RedditExceptionStub<RedditException> {
    @Nullable
    public abstract Inner getJson();

    @Nullable
    @Override
    public RedditException create(NetworkException cause) {
        if (getJson() == null) return null;
        if (getJson().ratelimit != null)
            return new RateLimitException(getJson().ratelimit, cause);

        if (getJson().errors != null && !getJson().errors.isEmpty()) {
            // We only really care about the first error and since there's rarely a time where there are more than one
            // errors being returned, it doesn't matter anyway
            List<String> error = getJson().errors.get(0);
            // TODO
            if (error.size() > 2)
                throw new IllegalArgumentException(error.toString());
            return new ApiException(error.get(0), error.get(1), new ArrayList<String>(), cause);
        }

        return null;
    }

    @Override
    public boolean containsError() {
        return getJson() != null && (getJson().ratelimit != null || !getJson().errors.isEmpty());
    }

    @Override
    public String toString() {
        return "GenericJsonResponse{" +
            "getJson()=" + getJson() +
            '}';
    }

    public static final class Inner {
        /**
         * A two-dimensional list of Strings. Each child of this list is its own error. Each error (usually) has two
         * properties: an error code and a human-readable message.
         */
        public List<List<String>> errors;
        public Map<String, Object> data;

        @Nullable
        public Double ratelimit;

        @Override
        public String toString() {
            return "Inner{" +
                "errors=" + errors +
                ", data=" + data +
                ", ratelimit=" + ratelimit +
                '}';
        }
    }

    public static JsonAdapter<GenericJsonResponse> jsonAdapter(Moshi moshi) {
        return new AutoValue_GenericJsonResponse.MoshiJsonAdapter(moshi);
    }
}
