package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Thing;
import net.dean.jraw.pagination.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class PaginationTest {
	private static RedditClient reddit;

	@BeforeClass
	public static void setUp() {
		reddit = TestUtils.client(PaginationTest.class);
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
		UserPaginatorSubmission paginator = new UserPaginatorSubmission.Builder(reddit)
				.username("Unidan")
				.where(Where.SUBMITTED)
				.build();
		commonTest(paginator);
	}

	@Test
	public void testById() throws NetworkException {
		List<String> fullNames = Arrays.asList("t3_92dd8", "t3_290287", "t3_28zy98", "t3_28zh9i");
		SpecificPaginator paginator = new SpecificPaginator.Builder(reddit,	fullNames.toArray(new String[fullNames.size()]))
				.build();

		Listing<Submission> submissions = paginator.next();
		for (Submission s : submissions.getChildren()) {
			Assert.assertTrue(fullNames.contains(s.getName()));
		}
	}

	private <T extends Thing> void commonTest(AbstractPaginator<T> p) throws NetworkException {
		// Test that the paginator can retrieve the data
		Listing<T> firstPage = p.next();
		ThingFieldTest.fieldValidityCheck(firstPage);
		ThingFieldTest.fieldValidityCheck(firstPage.getChildren().get(0));
	}
}
