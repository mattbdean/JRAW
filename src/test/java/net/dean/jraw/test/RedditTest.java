package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.JsonModel;
import net.dean.jraw.models.RenderStringPair;
import org.testng.Assert;
import org.testng.SkipException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * This class is the base class of all JRAW test classes. It provides dynamic User-Agents based on the name of the class
 * and several utility methods.
 */
public abstract class RedditTest {
    protected static final Random random = new Random();
    protected static final RedditClient reddit = new RedditClient("");

    protected RedditTest() {
        reddit.setUserAgent(getUserAgent(getClass()));
    }

    protected String getUserAgent(Class<?> clazz) {
        return clazz.getSimpleName() + " for JRAW v" + Version.get().formatted();
    }

    protected static final int randomInt() {
        return random.nextInt(1_000_000_000);
    }

    protected void handle(Throwable t) {
        if (t instanceof NetworkException) {
            NetworkException e = (NetworkException) t;
            if (e.getCode() == 504) {
                throw new SkipException("Received 504, skipping");
            }
        }
        t.printStackTrace();
        Assert.fail(t.getMessage() == null ? t.getClass().getName() : t.getMessage(), t);
    }

    protected final boolean isRateLimit(ApiException e) {
        return e.getCode().equals("QUOTA_FILLED") || e.getCode().equals("RATELIMIT");
    }

    protected void handlePostingQuota(ApiException e) {
        if (!isRateLimit(e)) {
            Assert.fail(e.getMessage());
        }

        String msg = null;
        // toUpperCase just in case (no pun intended)
        String method = getCallingMethod();
        switch (e.getCode().toUpperCase()) {
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

    protected final void validateRenderString(RenderStringPair strings) {
        Assert.assertNotNull(strings);
        Assert.assertNotNull(strings.md());
        Assert.assertNotNull(strings.html());
    }

    protected String getCallingMethod() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        // [0] = Thread.currentThread().getStackTrace()
        // [1] = this method
        // [2] = caller of this method
        // [3] = Caller of the caller of this method
        return elements[3].getMethodName();
    }

    protected final <T extends JsonModel> void validateModels(Collection<T> models) {
        for (JsonModel model : models) {
            validateModel(model);
        }
    }

    protected final <T extends JsonModel> void validateModel(T model) {
        Assert.assertNotNull(model);
        List<Method> jsonInteractionMethods = JsonModel.getJsonInteractionMethods(model.getClass());

        try {
            for (Method method : jsonInteractionMethods) {
                JsonInteraction jsonInteraction = method.getAnnotation(JsonInteraction.class);
                try {
                    method.invoke(model);
                } catch (InvocationTargetException e) {
                    // InvocationTargetException thrown when the method.invoke() returns null and @JsonInteraction "nullable"
                    // property is false
                    if (e.getCause().getClass().equals(NullPointerException.class) && !jsonInteraction.nullable()) {
                        Assert.fail("Non-nullable JsonInteraction method returned null: " + model.getClass().getName() + "." + method.getName() + "()");
                    } else {
                        // Other reason for InvocationTargetException
                        Throwable cause = e.getCause();
                        cause.printStackTrace();
                        Assert.fail(cause.getClass().getName() + ": " + cause.getMessage());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            handle(e);
        }
    }
}
