package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.UnixTime;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AutoValue
public abstract class OAuthData implements Serializable {
    /** A token that can be sent with an Authorization header to access oauth.reddit.com */
    public abstract String getAccessToken();

    /** A list in scopes the access token has permission for */
    public abstract List<String> getScopes();

    /** A token that can be used to request a new access token after the current one has expired, if one was requested */
    @Nullable
    public abstract String getRefreshToken();

    /** The date at which the access token will expire */
    @UnixTime(precision = TimeUnit.MILLISECONDS)
    public abstract Date getExpiration();

    public final boolean isExpired() {
        return getExpiration().before(new Date());
    }

    public static OAuthData create(String accessToken, List<String> scopes, String refreshToken, Date expiration) {
        return new AutoValue_OAuthData(accessToken, scopes, refreshToken, expiration);
    }

    public static JsonAdapter<OAuthData> jsonAdapter(Moshi moshi) {
        return new AutoValue_OAuthData.MoshiJsonAdapter(moshi);
    }
}
