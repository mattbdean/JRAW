package net.dean.jraw.http.oauth;

import com.google.common.collect.ImmutableMap;
import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;

import java.util.Map;

/**
 * Thrown when an error relating to authenticating with OAuth2 presents itself
 */
public final class OAuthException extends ApiException {
    public static final Map<String, String> REASONS = ImmutableMap.copyOf(JrawUtils.mapOf(
            "access_denied", "User chose not to grant permission",
            "unsupported_response_type", "Invalid 'response_type' parameter in initial authorization",
            "invalid_scope", "Invalid scope",
            "invalid_request", "Invalid request. Please use the URL provided by OAuthHelper.getAuthorizationUrl()"
    ));

    /**
     * Instantiates a new OAuthException. If the reason is a common reason (such as 'invalid_scope'), an explanation
     * will be given automatically
     * @param reason Why the exception was thrown
     */
    public OAuthException(String reason) {
        super(reason, REASONS.containsKey(reason) ? REASONS.get(reason) : "(no or unknown reason)");
    }

    public OAuthException(String reason, String explanation) {
        super(reason, explanation);
    }

    /**
     * Instantiates a new OAuthException
     * @param reason Why the exception was thrown
     * @param cause The Throwable that caused this exception
     */
    public OAuthException(String reason, Throwable cause) {
        super(reason, cause);
    }

}
