package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.EmbeddedMedia;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.JsonModel;
import net.dean.jraw.models.OEmbed;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Subreddit;
import net.dean.jraw.pagination.SimplePaginator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ThingFieldTest {
	private static final String SUBMISSION_ID = "92dd8";
	private static RedditClient reddit;

	@BeforeClass
	public static void setUp() {
		reddit = TestUtils.client(ThingFieldTest.class);
	}

	static <T extends JsonModel> void fieldValidityCheck(T thing) {
		List<Method> jsonInteractionMethods = JsonModel.getJsonInteractionMethods(thing.getClass());

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
						Throwable cause = e.getCause();
						cause.printStackTrace();
						Assert.fail(cause.getClass().getName() + ": " + cause.getMessage());
					}
				}
			}
		} catch (IllegalAccessException e) {
			Assert.fail(e.getMessage());
		}
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
			e.printStackTrace(System.err);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testOEmbed() {
		try {
			SimplePaginator frontPage = new SimplePaginator.Builder(reddit).build();
			Listing<Submission> submissions = frontPage.next();

			submissions.getChildren().stream().filter(s -> s.getOEmbedMedia() != null).forEach(s -> {
				OEmbed o = s.getOEmbedMedia();
				fieldValidityCheck(o);
			});
		} catch (IllegalStateException e) {
			if (e.getCause().getClass().equals(NetworkException.class)) {
				Assert.fail(e.getMessage());
			}
		}
	}

	@Test
	public void testEmbeddedMedia() {
		try {
			SimplePaginator frontPage = new SimplePaginator.Builder(reddit).build();
			Listing<Submission> submissions = frontPage.next();

			submissions.getChildren().stream().filter(s -> s.getEmbeddedMedia() != null).forEach(s -> {
				EmbeddedMedia m = s.getEmbeddedMedia();
				fieldValidityCheck(m);
			});
		} catch (IllegalStateException e) {
			if (e.getCause().getClass().equals(NetworkException.class)) {
				Assert.fail(e.getMessage());
			}
		}
	}

	@Test
	public void testSubreddit() {
		try {
			Subreddit sr = reddit.getSubreddit("pics");
			fieldValidityCheck(sr);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}
}
