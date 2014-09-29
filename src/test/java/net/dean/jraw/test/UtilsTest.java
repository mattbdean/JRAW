package net.dean.jraw.test;

import net.dean.jraw.models.Contribution;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static net.dean.jraw.JrawUtils.*;
import static org.testng.Assert.*;

/** Tests methods found in {@link net.dean.jraw.JrawUtils} */
public class UtilsTest extends RedditTest {
    @Test
    public void testArgs() {
        Map<String, String> expected = new HashMap<>();
        expected.put("hello", "world");
        expected.put("key", "value");

        Map<String, String> generated = args(
                "hello", "world",
                "key", "value"
        );

        assertEquals(expected, generated, "Expected and generated maps were not the same");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testArgsWithNullObject() {
        args("hello", null);
    }

    @Test
    public void testFullName() {
        assertTrue(isFullName("t2_f25asl"));
        assertFalse(isFullName("t9_s9al4"));
        assertFalse(isFullName("t0_ula8k"));
        assertFalse(isFullName("jfdklsa"));
        assertFalse(isFullName("t7_fd01ll"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFullNameEmpty() {
        isFullName("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testParseJsonBadType() throws IOException {
        String json = "{" +
                "\"kind\": \"t6\"," + // t6 = award
                "\"data\": {}" +
                "}";
        JsonNode mockNode = new ObjectMapper().readTree(json);
        // Should throw an IllegalArgumentException
        parseJson(mockNode, Contribution.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOddArgLength() {
        args("only one element");
    }
}
