package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Thing;
import net.dean.jraw.pagination.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaginationTest {
	private static RedditClient reddit;
	private static LoggedInAccount account;

	@BeforeClass
	public static void setUp() throws NetworkException, ApiException {
		reddit = TestUtils.client(PaginationTest.class);
		String[] creds = TestUtils.getCredentials();
		account = reddit.login(creds[0], creds[1]);
	}

	@Test
	public void testFrontPage() throws NetworkException {
		SimplePaginator frontPage = new SimplePaginator.Builder(reddit).build();
		commonTest(frontPage);
	}

	@Test
	public void testSubreddit() throws NetworkException {
		SimplePaginator pics = new SimplePaginator.Builder(reddit).subreddit("pics").build();
		commonTest(pics);
	}

	@Test
	public void testSubmitted() throws NetworkException {
		UserPaginatorSubmission paginator = new UserPaginatorSubmission.Builder(reddit, UserPaginatorSubmission.Where.SUBMITTED)
				.username("way_fairer")
				.build();
		commonTest(paginator);
	}

	@Test
	public void testById() throws NetworkException {
		// It would be easier to declare fullNames as an array, but we want to use List.contains()
		List<String> fullNames = Arrays.asList("t3_92dd8", "t3_290287", "t3_28zy98", "t3_28zh9i");
		SpecificPaginator paginator = new SpecificPaginator.Builder(reddit,	fullNames.toArray(new String[fullNames.size()]))
				.build();

		Listing<Submission> submissions = paginator.next();
		for (Submission s : submissions.getChildren()) {
			Assert.assertTrue(fullNames.contains(s.getName()));
		}
	}

	@Test(timeOut = 15_000)
	public void testPaginationTerminates() throws NetworkException {
		UserPaginatorSubmission paginator = new UserPaginatorSubmission.Builder(reddit, UserPaginatorSubmission.Where.SUBMITTED)
				.username(TestUtils.getCredentials()[0])
				.build();

		while (paginator.hasNext()) {
			paginator.next();
		}
	}

	@Test
	public void testMySubredditsPaginator() throws NetworkException {
		// Test all Where values
		for (MySubredditsPaginator.Where where : MySubredditsPaginator.Where.values()) {
			MySubredditsPaginator paginator = new MySubredditsPaginator.Builder(account, where).build();
			commonTest(paginator);
		}
	}


	@Test
	public void testAllSubredditsPaginator() throws NetworkException {
		// Test all Where values
		for (AllSubredditsPaginator.Where where : AllSubredditsPaginator.Where.values()) {
			AllSubredditsPaginator paginator = new AllSubredditsPaginator.Builder(reddit, where).build();
			commonTest(paginator);
		}
	}

	private <T extends Thing> void commonTest(AbstractPaginator<T> p) throws NetworkException {
		// Test that the paginator can retrieve the data
		Listing<T> firstPage = p.next();
		ThingFieldTest.fieldValidityCheck(firstPage);
		ThingFieldTest.fieldValidityCheck(firstPage.getChildren().get(0));
	}
}
