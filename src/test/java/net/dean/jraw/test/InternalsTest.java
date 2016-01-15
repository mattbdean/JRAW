package net.dean.jraw.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dean.jraw.Endpoint;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.*;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.util.Version;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class InternalsTest extends RedditTest {
	
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testModifyListing() {
        Listing<Submission> submissions = new SubredditPaginator(reddit).next();
        submissions.remove(0);
    }

    @Test
    public void testJsonModelToString() {
        MockJsonModel model = new MockJsonModel();
        String actual = model.toString();
        String expected = "MockJsonModel {getBar()=\"0.0\", getFoo()=\"foo\", getSomeModel()=[MockJsonModel], " +
                "throwsException()=[threw java.lang.UnsupportedOperationException: exception message]}";
        assertEquals(actual, expected);
    }

    @Test
    public void testJsonModelAsString() {
        MockJsonModel model = new MockJsonModel();
        assertEquals(model.asString(null), "null");
        assertEquals(model.asString("hello"), "hello");
    }

    @Test
    public void testModifyListingData() {
        final Listing<Submission> listing = new SubredditPaginator(reddit).next();
        List<CodeBlock> codeBlocks = new ArrayList<>();
        
        // List of CodeBlocks that will modify the listing
        codeBlocks.addAll(Arrays.asList(
        		new CodeBlock() { @Override public void execute() { listing.add(null); }},
        		new CodeBlock() { @Override public void execute() { listing.add(0, null); }},
        		new CodeBlock() { @Override public void execute() { listing.addAll(0, null); }},
        		new CodeBlock() { @Override public void execute() { listing.addAll(null); }},
        		new CodeBlock() { @Override public void execute() { listing.clear(); }},
        		new CodeBlock() { @Override public void execute() { listing.remove(null); }},
        		new CodeBlock() { @Override public void execute() { listing.remove(0); }},
        		new CodeBlock() { @Override public void execute() { listing.removeAll(null); }},
        		new CodeBlock() { @Override public void execute() { listing.retainAll(null); }},
        		new CodeBlock() { @Override public void execute() { listing.set(0, null); }}
        ));
        
        for (CodeBlock e : codeBlocks) {
            assertNotNull(getException(e));
        }
    }

    @Test
    public void testFlair() {
        String css = "css";
        String text = "text";
        Flair f = new Flair(css, text);
        assertEquals(f.getCssClass(), css);
        assertEquals(f.getText(), text);

        Flair f2 = new Flair(css, text);
        basicObjectTest(f, f2);
    }

    @Test
    public void testDistinguishedStatus() {
        DistinguishedStatus status = DistinguishedStatus.ADMIN;
        assertEquals(status, DistinguishedStatus.getByJsonValue(status.getJsonValue()));
    }

    @Test
    public void testOAuthException() {
        OAuthException e = new OAuthException("invalid_scope");
        assertEquals(e.getExplanation(), OAuthException.REASONS.get("invalid_scope"));

        e = new OAuthException("test");
        assertEquals(e.getReason(), "test");
        assertEquals(e.getExplanation(), "(no or unknown reason)");

        NullPointerException cause = new NullPointerException();
        e = new OAuthException("test", cause);
        assertEquals(e.getCause(), cause);
    }

    @Test
    public void testVersion() {
        MockVersion v = new MockVersion(1, 2, 3);
        assertEquals(v.getMajor(), 1);
        assertEquals(v.getMinor(), 2);
        assertEquals(v.getPatch(), 3);
        assertEquals(v.formatted(), "1.2.3");
        assertEquals(v.formatted(), v.toString());

        MockVersion v2 = new MockVersion(1, 2, 3, 4);
        assertEquals(v2.getBuild(), 4);
        assertEquals(v2.formatted(), "1.2.3.4");
        basicObjectTest(v2, v2);
    }

    /**
     * Makes sure that equals(), hashCode(), and toString() were overridden and tests them
     *
     * @param o1 An object
     * @param o2 An object which is supposedly equal to o1
     * @param <T> The type of object
     */
    private <T> void basicObjectTest(T o1, T o2) {
        // Make sure the default toString() was overridden
        assertFalse(o1.toString().equals(o1.getClass().getName() + "@" + Integer.toHexString(o1.hashCode())),
                "toString() not overridden");
        assertEquals(o1.hashCode(), o2.hashCode(), "hashCode() not overridden");
        assertEquals(o1, o2, "equals() not overridden");
    }

    private class MockVersion extends Version {
        public MockVersion(int major, int minor, int patch) {
            super(major, minor, patch);
        }

        public MockVersion(int major, int minor, int patch, int build) {
            super(major, minor, patch, build);
        }
    }

    @Test
    public void testMore() {
        try {
            String json = "{" +
                    "\"count\":20," +
                    "\"parent_id\":\"t1_ckuxak4\"," +
                    "\"children\":[" +
                    "    \"ckuzahs\"," +
                    "    \"ckuzll8\"," +
                    "    \"ckuzt7k\"," +
                    "    \"ckv4o7l\"," +
                    "    \"ckv5qjp\"" +
                    "]," +
                    "\"id\":\"ckuzahs\"," +
                    "\"name\":\"t1_ckuzahs\"" +
                    "}";
            JsonNode dataNode = objectMapper.readTree(json);

            MoreChildren more = new MoreChildren(dataNode);

            assertTrue(more.getCount() == 20);

            List<String> expectedChildren = Arrays.asList("ckuzahs", "ckuzll8", "ckuzt7k", "ckv4o7l", "ckv5qjp");
            assertEquals(more.getChildrenIds(), expectedChildren);
        } catch (IOException e) {
            handle(e);
        }
    }

    private Exception getException(CodeBlock e) {
        try {
            e.execute();
            return null;
        } catch (Exception ex) {
            return ex;
        }
    }

    @Test
    public void testEndpoint() {
        String requestDescriptor = "GET /api/{foo}/{bar}/baz";
        String category = "cat";

        Endpoint e = new Endpoint(requestDescriptor, category);
        assertEquals(e.getScope(), "cat");
        assertEquals(e.getUri(), "/api/{foo}/{bar}/baz");
        assertEquals(e.getVerb(), "GET");
        assertEquals(e.getRequestDescriptor(), requestDescriptor);

        Endpoint e2 = new Endpoint(requestDescriptor, category);
        basicObjectTest(e, e2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEndpointInvalidRequestDescriptor() {
        new Endpoint("part1 part2 part3");
    }

    private interface CodeBlock {
        void execute();
    }

    // Normally this class would be private but JsonModel.toString() will throw an IllegalAccessException because it
    // can't access this class' @JsonInteraction methods
    public class MockJsonModel extends JsonModel {

        public MockJsonModel() {
            super(null);
        }

        @JsonProperty
        public String getFoo() {
            return "foo";
        }

        @JsonProperty
        public Float getBar() {
            return 0f;
        }

        @JsonProperty
        public String throwsException() {
            throw new UnsupportedOperationException("exception message");
        }

        @JsonProperty
        public JsonModel getSomeModel() {
            return new MockJsonModel();
        }

        // Override to raise privacy level from protected to public
        @Override
        public String asString(Object val) {
            return super.asString(val);
        }
    }
}
