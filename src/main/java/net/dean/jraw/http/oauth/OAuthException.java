package net.dean.jraw.http.oauth;

import com.google.common.collect.ImmutableMap;
import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;

import java.util.Map;

/**
 * Thrown when an error relating to authenticing with OAuth2 presents itself
 */
public class OAuthException extends ApiException {
    static final Map<String, String> reasons = ImmutableMap.copyOf(JrawUtils.args(
            "access_denied", "User chose not to grant permission",
            "unsupported_response_type", "Invalid 'response_type' parameter in initial authorization",
            "invalid_scope", "Invalid scope",
            "invalid_request", "Invalid request. Please use the URL provided by OAuthorizationFlow.getAuthorizationUrl()"
    ));

    /**
     * Instantiates a new OAuthException. If the reason is a common reason (such as 'invalid_scope'), an explanation
     * will be given automatically
     * @param reason Why the exception was thrown
     */
    public OAuthException(String reason) {
        super(reason, reasons.containsKey(reason) ? reasons.get(reason) : "(no reason given)");
    }

    /**
     * Instantiates a new OAuthException
     * @param reason Why the exception was thrown
     * @param explanation A more detailed explanation
     */
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
