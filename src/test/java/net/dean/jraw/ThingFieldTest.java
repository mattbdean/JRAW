package net.dean.jraw;

import junit.framework.Assert;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.core.Link;
import net.dean.jraw.models.core.Thing;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ThingFieldTest {
	private RedditClient reddit;
	private static final String LINK_ID = "92dd8";

	@BeforeTest
	public void setUp() {
		this.reddit = new RedditClient(TestUtils.getUserAgent(getClass()));
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
			Link link = reddit.getLink(LINK_ID);
			fieldValidityCheck(link);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "testLink")
	public void testComment() {
		try {
			Link link = reddit.getLink(LINK_ID);
			fieldValidityCheck(link.getComments().getChildren().get(0));
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	static <T extends Thing> void fieldValidityCheck(T thing) {
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
						e.getCause().printStackTrace();
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a list of fields that have the AttributeField annotation attached to them. Also searches the superclass up
	 * until ${@link net.dean.jraw.models.core.Thing} for fields.
	 *
	 * @param thingClass The class to search for
	 * @return A list of fields that have the JsonAttribute annotation
	 */
	private static List<Method> getJsonInteractionMethods(Class<? extends Thing> thingClass) {
		List<Method> getterMethods = new ArrayList<>();

		Class clazz = thingClass;

		while (clazz != null) {
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.isAnnotationPresent(JsonInteraction.class)) {
					getterMethods.add(m);
				}
			}

			if (clazz.equals(Thing.class)) {
				// Already at the highest level and we don't need to scan Object
				break;
			}

			// Can still go deeper...
			clazz = clazz.getSuperclass();
		}

		return getterMethods;
	}

}
