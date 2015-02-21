package net.dean.jraw.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.managers.ModerationManager;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.JsonModel;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.paginators.UserSubredditsPaginator;
import org.testng.Assert;
import org.testng.SkipException;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * This class is the base class of all JRAW test classes. It provides dynamic User-Agents based on the name of the class
 * and several utility methods.
 */
public abstract class RedditTest {
    protected static final RedditClient reddit = new RedditClient(UserAgent.of("desktop",
            "net.dean.jraw.test",
            "v" + Version.get().formatted(),
            "thatJavaNerd"));
    private static Credentials credentials;
    private static ObjectMapper objectMapper = new ObjectMapper();
    protected final AccountManager account;
    protected final ModerationManager moderation;

    protected RedditTest() {
        reddit.setLoggingMode(LoggingMode.ON_FAIL);
        Credentials creds = getCredentials();
        if (!reddit.isLoggedIn()) {
            try {
                reddit.authenticate(reddit.getOAuthHelper().easyAuth(creds));
            } catch (NetworkException | ApiException e) {
                handle(e);
            }
        }
        this.account = new AccountManager(reddit);
        this.moderation = new ModerationManager(reddit);
    }

    public long epochMillis() {
        return new Date().getTime();
    }

    protected void handle(Throwable t) {
        if (t instanceof NetworkException) {
            NetworkException e = (NetworkException) t;
            int code = e.getResponse().getStatusCode();
            if (code >= 500 && code < 600)
                throw new SkipException("Received " + code + ", skipping");
        }
        t.printStackTrace();
        Assert.fail(t.getMessage() == null ? t.getClass().getName() : t.getMessage(), t);
    }

    protected final boolean isRateLimit(ApiException e) {
        return e.getReason().equals("QUOTA_FILLED") || e.getReason().equals("RATELIMIT");
    }

    protected void handlePostingQuota(ApiException e) {
        if (!isRateLimit(e)) {
            Assert.fail(e.getMessage());
        }

        String msg = null;
        // toUpperCase just in case (no pun intended)
        String method = getCallingMethod();
        switch (e.getReason().toUpperCase()) {
            case "QUOTA_FILLED":
                msg = String.format("Skipping %s(), link posting quota has been filled for this user", method);
                break;
            case "RATELIMIT":
                msg = String.format("Skipping %s(), reached ratelimit (%s)", method, e.getExplanation());
                break;
        }

        if (msg != null) {
            JrawUtils.logger().error(msg);
            throw new SkipException(msg);
        } else {
            Assert.fail(e.getMessage());
        }
    }

    protected String getCallingMethod() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        // [0] = Thread.currentThread().getStackTrace()
        // [1] = this method
        // [2] = caller of this method
        // [3] = Caller of the caller of this method
        return elements[3].getMethodName();
    }

    protected final <T extends JsonModel> void validateModels(Iterable<T> iterable) {
    	for (T model : iterable) {
    		validateModel(model);
    	}
    }

    /**
     * Validates all of the CommentNode's children's Comments
     */
    protected final void validateModel(CommentNode root) {
        for (CommentNode node : root.walkTree()) {
            validateModel(node.getComment());
        }
    }

    protected final <T extends JsonModel> void validateModel(T model) {
        Assert.assertNotNull(model);
        List<Method> jsonInteractionMethods = JsonModel.getJsonProperties(model.getClass());

        try {
            for (Method method : jsonInteractionMethods) {
                JsonProperty jsonProperty = method.getAnnotation(JsonProperty.class);
                Object returnVal = null;
                try {
                    returnVal = method.invoke(model);
                } catch (InvocationTargetException e) {
                    // InvocationTargetException thrown when the method.invoke() returns null and @JsonInteraction "nullable"
                    // property is false
                    if (e.getCause().getClass().equals(NullPointerException.class) && !jsonProperty.nullable()) {
                        Assert.fail("Non-nullable JsonInteraction method returned null: " + model.getClass().getName() + "." + method.getName() + "()");
                    } else {
                        // Other reason for InvocationTargetException
                        Throwable cause = e.getCause();
                        cause.printStackTrace();
                        Assert.fail(cause.getClass().getName() + ": " + cause.getMessage());
                    }
                }
                if (returnVal != null && returnVal instanceof JsonModel) {
                    validateModel((JsonModel) returnVal);
                }
            }
        } catch (IllegalAccessException e) {
            handle(e);
        }
    }

    /**
     * Gets a Credentials object that represents the properties found in credentials.json
     * (located at /src/test/resources). The Credential's AuthenticationMethod will always be
     * {@link AuthenticationMethod#SCRIPT}
     *
     * @return A Credentials object parsed form credentials.json
     */
    protected final Credentials getCredentials() {
        if (credentials != null) {
            return credentials;
        }

        try {
            // If running locally, use credentials file
            // If running with Travis-CI, use env variables
            if (System.getenv("TRAVIS") != null && Boolean.parseBoolean(System.getenv("TRAVIS"))) {
                credentials = Credentials.script(System.getenv("USERNAME"),
                        System.getenv("PASS"),
                        System.getenv("CLIENT_ID"),
                        System.getenv("CLIENT_SECRET"));
                return credentials;
            } else {
                InputStream in = RedditTest.class.getResourceAsStream("/credentials.json");
                if (in == null) {
                    throw new SetupRequiredException("credentials.json could not be found. See " +
                            "https://github.com/thatJavaNerd/JRAW#contributing for more information.");
                }

                JsonNode data = objectMapper.readTree(in);
                credentials = Credentials.script(data.get("username").asText(),
                        data.get("password").asText(),
                        data.get("client_id").asText(),
                        data.get("client_secret").asText());
                return credentials;
            }
        } catch (Exception e) {
            handle(e);
            return null;
        }
    }

    /**
     * Gets a subreddit that the testing user moderates
     * @return A subreddit
     */
    protected final Subreddit getModeratedSubreddit() {
        Listing<Subreddit> moderatorOf = new UserSubredditsPaginator(reddit, "moderator").next();
        if (moderatorOf.size() == 0) {
            throw new IllegalStateException("Must be a moderator of at least one subreddit");
        }

        return moderatorOf.get(0);
    }
}
