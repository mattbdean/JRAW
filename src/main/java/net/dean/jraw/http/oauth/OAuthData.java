package net.dean.jraw.http.oauth;

import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.models.JsonModel;
import net.dean.jraw.models.meta.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/**
 * This class represents the data provided from a successful request to {@code /api/v1/access_token}. See
 * <a href="https://github.com/reddit/reddit/wiki/OAuth2#token-retrieval">here</a> for an example.
 */
public class OAuthData extends JsonModel {
    private final AuthenticationMethod method;

    /**
     * Instantiates a new AuthData
     *
     * @param dataNode The node to parse data from
     */
    public OAuthData(AuthenticationMethod method, JsonNode dataNode) {
        super(dataNode);
        this.method = method;
    }

    @JsonProperty
    public String getAccessToken() {
        return data("access_token");
    }

    /**
     * Gets the token type
     * @return The string "bearer"
     */
    @JsonProperty
    public String getTokenType() {
        return data("token_type");
    }

    /**
     * Gets the date at which the access token expires, which will be in one hour from when it was originally requested.
     */
    @JsonProperty
    public Date getExpirationDate() {
        Date tokenExpiration = new Date();
        // Add the time the token expires
        tokenExpiration.setTime(tokenExpiration.getTime() + data("expires_in", Integer.class) * 1000);
        return tokenExpiration;
    }

    /**
     * Gets the OAuth scopes in which the access token enables.
     */
    @JsonProperty
    public String[] getScopes() {
        return data("scope").split(",");
    }

    /**
     * Gets the token which may be exchanged for another access token, granted one was requested during the time of
     * authorization
     */
    @JsonProperty(nullable = true)
    public String getRefreshToken() {
        return data("refresh_token");
    }

    public AuthenticationMethod getAuthenticationMethod() {
        return method;
    }
}
