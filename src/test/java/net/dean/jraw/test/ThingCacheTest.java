package net.dean.jraw.test;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.ThingCache;
import net.dean.jraw.models.Submission;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the ThingManager
 *
 * @author Phani Gaddipati
 */
public class ThingCacheTest extends RedditTest {

    private static final String SUBMISSION_ID = "92dd8";

    @Test
    public void testThingMap() {
        Submission submission;
        try {
            ThingCache.instance().setEnabled(true);
            submission = reddit.getSubmission(SUBMISSION_ID);
            validateModel(submission);
            String submissionName = submission.getFullName();
            //Test that the found reference matches the expected reference
            Assert.assertEquals(submission, ThingCache.instance().getThing(submissionName));

            //Test that when disabled/cleared, getThing will return null

            ThingCache.instance().setEnabled(false);
            ThingCache.instance().clearMap();

            submission = reddit.getSubmission(SUBMISSION_ID);
            validateModel(submission);
            submissionName = submission.getFullName();
            //Test that there is no reference
            Assert.assertNull(ThingCache.instance().getThing(submissionName));
        } catch (NetworkException e) {
            handle(e);
        }
    }
}
