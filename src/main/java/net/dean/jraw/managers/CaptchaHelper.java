package net.dean.jraw.managers;

import net.dean.jraw.ApiException;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.util.JrawUtils;

/**
 * Provides access to API methods related primarily to CAPTCHAs.
 */
public class CaptchaHelper extends AbstractManager {
    public CaptchaHelper(RedditClient reddit) {
        super(reddit);
    }

    /**
     * Checks if the current user needs a captcha to do specific actions such as submit links and compose private
     * messages. This will always be true if there is no logged in user. Usually, this method will return {@code true}
     * if the current logged in user has more than 10 link karma
     *
     * @return True if the user needs a captcha to do a specific action, else if not or not logged in.
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.NEEDS_CAPTCHA)
    public boolean isNecessary() throws NetworkException {
        // This endpoint does not return JSON, but rather just "true" or "false"
        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.NEEDS_CAPTCHA)
                .get()
                .build());
        return Boolean.parseBoolean(response.getRaw());
    }
}
