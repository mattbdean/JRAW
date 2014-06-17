package net.dean.jraw;

import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ListingsTest {
	private static RedditClient client;

	@BeforeClass
	public static void setUp() {
		client = TestUtils.client(ListingsTest.class);
	}

	@Test
	public void testFrontPage() throws NetworkException {
		Paginator frontPage = client.getFrontPage();
		commonTest(frontPage);
	}

	@Test
	public void testSubreddit() throws NetworkException {
		Paginator pics = client.getSubreddit("pics");
		commonTest(pics);
	}

	private void commonTest(Paginator p) throws NetworkException {
		// Test that the paginator can retrieve the data
		Listing<Submission> firstPage = p.first();
		ThingFieldTest.fieldValidityCheck(firstPage);
		ThingFieldTest.fieldValidityCheck(firstPage.getChildren().get(0));

		// Test if the second page is valid
		Listing<Submission> secondPage = p.next();
		ThingFieldTest.fieldValidityCheck(secondPage);

		// Check if the pagination functionality is working
		Assert.assertNotEquals(firstPage.getChildren().get(0), secondPage.getChildren().get(0));
	}
}
