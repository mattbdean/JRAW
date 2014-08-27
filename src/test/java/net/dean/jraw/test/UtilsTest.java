package net.dean.jraw.test;

import static org.testng.Assert.*;
import static net.dean.jraw.JrawUtils.*;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class UtilsTest {
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
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testFullNameEmpty() {
		isFullName("");
	}
}
