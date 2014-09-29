package net.dean.jraw.test;

import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.JsonModel;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.More;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.pagination.SubredditPaginator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
        String expected = "MockJsonModel {getBar()=\"0\", getFoo()=\"foo\", getSomeModel()=[MockJsonModel], " +
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
                    "],\n" +
                    "\"id\":\"ckuzahs\"," +
                    "\"name\":\"t1_ckuzahs\"" +
                    "}";
            JsonNode dataNode = objectMapper.readTree(json);

            More more = new More(dataNode);

            assertTrue(more.getCount() == 20);

            List<String> expectedChildren = Arrays.asList("ckuzahs", "ckuzll8", "ckuzt7k", "ckv4o7l", "ckv5qjp");
            assertEquals(more.getChildrenIds(), expectedChildren);
        } catch (IOException e) {
            handle(e);
        }
    }

    // Normally this class would be private but JsonModel.toString() will throw an IllegalAccessException
    public class MockJsonModel extends JsonModel {

        public MockJsonModel() {
            super(null);
        }

        @JsonInteraction
        public String getFoo() {
            return "foo";
        }

        @JsonInteraction
        public Integer getBar() {
            return 0;
        }

        @JsonInteraction
        public String throwsException() {
            throw new UnsupportedOperationException("exception message");
        }

        @JsonInteraction
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
