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
	private static RedditClient client;

	@BeforeClass
	public static void setUp() {
		client = TestUtils.client(PaginationTest.class);
	}

	@Test
	public void testFrontPage() throws NetworkException {
		SimplePaginator frontPage = client.getFrontPage();
		commonTest(frontPage);
	}

	@Test
	public void testSubreddit() throws NetworkException {
		SimplePaginator pics = client.getSubreddit("pics");
		commonTest(pics);
	}

	@Test
	public void testSubmitted() throws NetworkException {
		UserPaginatorSubmission paginator = client.getUserPaginator("Unidan", Where.SUBMITTED);
		commonTest(paginator);
	}

	private <T extends Thing> void commonTest(AbstractPaginator<T> p) throws NetworkException {
		// Test that the paginator can retrieve the data
		Listing<T> firstPage = p.next();
		ThingFieldTest.fieldValidityCheck(firstPage);
		ThingFieldTest.fieldValidityCheck(firstPage.getChildren().get(0));
	}
}
