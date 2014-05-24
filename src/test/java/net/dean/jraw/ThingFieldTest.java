package net.dean.jraw;

import junit.framework.Assert;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Submission;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThingFieldTest {
	private static final String SUBMISSION_ID = "92dd8";
	private static RedditClient reddit;

	static <T extends RedditObject> void fieldValidityCheck(T thing) {
		List<Method> jsonInteractionMethods = getJsonInteractionMethods(thing.getClass());

		try {
			for (Method method : jsonInteractionMethods) {
				JsonInteraction jsonInteraction = method.getAnnotation(JsonInteraction.class);
				try {
					method.invoke(thing);
				} catch (InvocationTargetException e) {
					// InvocationTargetException thrown when the method.invoke() returns null and @JsonInteraction "nullable"
					// property is false
					if (e.getCause().getClass().equals(NullPointerException.class) && !jsonInteraction.nullable()) {
						Assert.fail("Non-nullable JsonInteraction method returned null: " + thing.getClass().getName() + "." + method.getName() + "()");
					} else {
						// Other reason for InvocationTargetException
						Assert.fail(e.getCause().getMessage());
					}
				}
			}
		} catch (IllegalAccessException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Gets a list of fields that have the AttributeField annotation attached to them. Also searches the superclass up
	 * until ${@link net.dean.jraw.models.core.Thing} for fields.
	 *
	 * @param thingClass The class to search for
	 * @return A list of fields that have the JsonAttribute annotation
	 */
	private static List<Method> getJsonInteractionMethods(Class<? extends RedditObject> thingClass) {
		List<Method> methods = new ArrayList<>();

		Class clazz = thingClass;
		List<Method> toObserve = new ArrayList<>();

		while (clazz != null) {
			toObserve.addAll(Arrays.asList(clazz.getDeclaredMethods()));
			for (Class<?> interf : clazz.getInterfaces()) {
				toObserve.addAll(Arrays.asList(interf.getDeclaredMethods()));
			}

			if (clazz.equals(RedditObject.class)) {
				// Already at the highest level and we don't need to scan Object
				break;
			}

			// Can still go deeper...
			clazz = clazz.getSuperclass();
		}

		for (Method m : toObserve) {
			if (m.isAnnotationPresent(JsonInteraction.class)) {
				methods.add(m);
			}
		}

		return methods;
	}

	@BeforeClass
	public static void setUp() {
		reddit = new RedditClient(TestUtils.getUserAgent(ThingFieldTest.class));
	}

	@Test
	public void testAccount() {
		try {
			Account redditAccount = reddit.getUser("spladug");
			fieldValidityCheck(redditAccount);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLink() {
		try {
			Submission submission = reddit.getSubmission(SUBMISSION_ID);
			fieldValidityCheck(submission);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "testLink")
	public void testComment() {
		try {
			Submission submission = reddit.getSubmission(SUBMISSION_ID);
			fieldValidityCheck(submission.getComments().getChildren().get(0));
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

}
