package net.dean.jraw.http.oauth;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;

import java.util.Map;

public class OAuthException extends ApiException {
    static final Map<String, String> reasons = JrawUtils.args(
            "access_denied", "User chose not to grant permission",
            "unsupported_response_type", "Invalid 'response_type' parameter in initial authorization",
            "invalid_scope", "Invalid scope",
            "invalid_request", "Invalid request. Please use the URL provided by OAuthorizationFlow.getAuthorizationUrl()"
    );

    public OAuthException(String reason) {
        super(reason, reasons.containsKey(reason) ? reasons.get(reason) : "(no reason given)");
    }

    public OAuthException(String reason, String explanation) {
        super(reason, explanation);
    }

    public OAuthException(String reason, Throwable cause) {
        super(reason, cause);
    }

}
