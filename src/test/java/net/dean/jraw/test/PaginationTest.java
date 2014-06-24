package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Thing;
import net.dean.jraw.pagination.AbstractPaginator;
import net.dean.jraw.pagination.SimplePaginator;
import net.dean.jraw.pagination.UserPaginatorSubmission;
import net.dean.jraw.pagination.Where;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

	private <T extends Thing> void commonTest(AbstractPaginator<T> p) throws NetworkException {
		// Test that the paginator can retrieve the data
		Listing<T> firstPage = p.next();
		ThingFieldTest.fieldValidityCheck(firstPage);
		ThingFieldTest.fieldValidityCheck(firstPage.getChildren().get(0));
	}
}
