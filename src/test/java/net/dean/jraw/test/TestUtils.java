package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.models.RenderStringPair;
import org.testng.Assert;
import org.testng.SkipException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public final class TestUtils {
    private static Random random = new Random();
    private static final DateFormat df = new SimpleDateFormat();
    private static RedditClient client;

    public static RedditClient client(Class<?> testClass) {
        String generatedUserAgent = getUserAgent(testClass);

        if (client == null) {
            client = new RedditClient(generatedUserAgent);
        } else {
            client.setUserAgent(generatedUserAgent);
        }

        return client;
    }

    public static String[] getCredentials() {
        try {
            // If running locally, use credentials file
            // If running with Travis-CI, use env variables
            if (System.getenv("TRAVIS") != null && Boolean.parseBoolean(System.getenv("TRAVIS"))) {
                return new String[] {System.getenv("USERNAME"), System.getenv("PASS")};
            } else {
                String[] details = new String[2];
                InputStream in = TestUtils.class.getResourceAsStream("/credentials.txt");
                if (in == null) {
                    throw new FileNotFoundException("credentials.txt could not be found. See " +
                            "https://github.com/thatJavaNerd/JRAW#contributing for more information.");
                }
                Scanner s = new Scanner(in);
                details[0] = s.nextLine(); 
                details[1] = s.nextLine();
                s.close();
                return details;
            }
        } catch (Exception e) {
            TestUtils.handle(e);
            return null;
        }
    }

    public static String getUserAgent(Class<?> clazz) {
        return clazz.getSimpleName() + " for JRAW v" + Version.get().formatted();
    }

    public static int randomInt() {
        return random.nextInt(1_000_000_000);
    }

    public static String curDate() {
        return df.format(new Date());
    }

    public static void handle(Throwable t) {
        t.printStackTrace();
        Assert.fail(t.getMessage() == null ? t.getClass().getName() : t.getMessage(), t);
    }

    public static void handleApiException(ApiException e) {

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

    public static void testRenderString(RenderStringPair strings) {
        Assert.assertNotNull(strings);
        Assert.assertNotNull(strings.md());
        Assert.assertNotNull(strings.html());
    }

    private static String getCallingMethod() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        // [0] = Thread.getStackTrace()
        // [1] = this method
        // [2] = handleApiException
        // [3] = Caller of handleApiException
        return elements[3].getMethodName();
    }
}
