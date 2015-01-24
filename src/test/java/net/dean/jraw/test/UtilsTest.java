package net.dean.jraw.test;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static net.dean.jraw.JrawUtils.args;
import static net.dean.jraw.JrawUtils.isFullName;
import static net.dean.jraw.JrawUtils.join;
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
    public void testOddArgLength() {
        args("only one element");
    }

    @Test
    public void testJoin() {
        char sep = ',';
        assertEquals(join(), "");
        assertEquals(join(sep, "one"), "one");
        assertEquals(join(sep, "one", "two", "three"), "one,two,three");
    }
}
