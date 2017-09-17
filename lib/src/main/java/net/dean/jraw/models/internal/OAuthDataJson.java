package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.models.OAuthData;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@AutoValue
public abstract class OAuthDataJson {
    /** A token that can be sent with an Authorization header to access oauth.reddit.com */
    @Json(name = "access_token") public abstract String getAccessToken();

    /** The time in seconds that the access token will be valid for */
    @Json(name = "expires_in") public abstract long getExpiresIn();

    /** A comma-separated list of OAuth2 scopes the application has permission to use */
    @Json(name = "scope") public abstract String getScopeList();

    /** A refresh token, if one was requested */
    @Nullable @Json(name = "refresh_token") public abstract String getRefreshToken();

    public final OAuthData toOAuthData() {
        return OAuthData.create(
            /*accessToken = */ getAccessToken(),
            /*scopes = */ Arrays.asList(getScopeList().split(",")),
            /*refreshToken = */ getRefreshToken(),
            /*expiration = */ new Date(new Date().getTime() + TimeUnit.MILLISECONDS.convert(getExpiresIn(), TimeUnit.SECONDS))
        );
    }

    public static JsonAdapter<OAuthDataJson> jsonAdapter(Moshi moshi) {
        return new AutoValue_OAuthDataJson.MoshiJsonAdapter(moshi);
    }
}
