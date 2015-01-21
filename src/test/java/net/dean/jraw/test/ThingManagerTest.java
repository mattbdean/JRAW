package net.dean.jraw.test;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.ThingManager;
import net.dean.jraw.models.Submission;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the ThingManager
 *
 * @author Phani Gaddipati
 */
public class ThingManagerTest extends RedditTest {

    private static final String SUBMISSION_ID = "92dd8";

    @Test
    public void testEnabledThingMapping() {
        ThingManager.get().setEnabled(true);

        Submission submission;
        try {
            submission = reddit.getSubmission(SUBMISSION_ID);
            validateModel(submission);
            String submissionName = submission.getFullName();
            //Test that the found reference matches the expected reference
            Assert.assertEquals(submission, ThingManager.get().getThing(submissionName));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testDisabledThingMapping() {
        ThingManager.get().setEnabled(false);
        ThingManager.get().clearMap();

        Submission submission;
        try {
            submission = reddit.getSubmission(SUBMISSION_ID);
            validateModel(submission);
            String submissionName = submission.getFullName();
            //Test that there is no reference
            Assert.assertNull(ThingManager.get().getThing(submissionName));
        } catch (NetworkException e) {
            handle(e);
        }
    }
}
