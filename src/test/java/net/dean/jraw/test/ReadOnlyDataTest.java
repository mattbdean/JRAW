package net.dean.jraw.test;

import com.google.common.base.Optional;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.*;
import net.dean.jraw.paginators.SubredditPaginator;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

/**
 * This class tests data that is accessible to everyone, such as submissions and basic user information.
 */
public class ReadOnlyDataTest extends RedditTest {
    private static final String SUBMISSION_ID = "92dd8";

    @Test
    public void testOEmbed() {
        try {
            SubredditPaginator frontPage = new SubredditPaginator(reddit);
            Listing<Submission> submissions = frontPage.next();

            int count = 0;
            // Validate all the EmbeddedMedia models on the front page
            for (Submission s : submissions) {
                if (s.getOEmbedMedia() != null) {
                    validateModel(s.getOEmbedMedia());
                    count++;
                }
            }

            if (count == 0) {
                // Did not perform any validation, so the test was essentially skipped.
                throw new SkipException("No OEmbed models were found on the front page");
            }

        } catch (IllegalStateException e) {
            handle(e);
        }
    }

    @Test
    public void testSubmission() {
        try {
            Submission submission = reddit.getSubmission(SUBMISSION_ID);
            validateModel(submission);

            CommentNode comments = submission.getComments();
            // This is one of the most upvoted links in reddit history, there's bound to be more than one comment
            assertFalse(comments.isEmpty());

            validateModel(comments);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testThumbnails() {
        try {
            Submission s = reddit.getSubmission("40z3ti");
            Thumbnails prev = s.getThumbnails();
            validateModel(prev);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testRandomSubmission() {
        try {
            // From anywhere
            Submission s = reddit.getRandomSubmission();
            validateModel(s);

            // From /r/pics
            s = reddit.getRandomSubmission("pics");
            validateModel(s);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testRandomSubreddit() {
        try {
            Subreddit s = reddit.getRandomSubreddit();
            validateModel(s);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testSubreddit() {
        try {
            validateModel(reddit.getSubreddit("pics"));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testUser() {
        try {
            // He's an admin, so probably not going away anytime soon (unlike Unidan)
            validateModel(reddit.getUser("spladug"));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testSubmitText() {
        try {
            assertNotNull(reddit.getSubmitText("videos"));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testSubredditsByTopic() {
        try {
            List<String> subs = reddit.getSubredditsByTopic("programming");

            assertTrue(subs.size() > 0);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testSearchSubreddits() {
        try {
            List<String> subs = reddit.searchSubreddits("fun", false);

            assertTrue(subs.size() > 0);
            // Make sure the items aren't null
            for (String s : subs) {
                Assert.assertNotNull(s);
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testStylesheet() {
        try {
            reddit.getStylesheet("pics");
            // Just make sure a NetworkException isn't thrown
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testTrendingSubs() {
        List<String> trending = reddit.getTrendingSubreddits();

        try {
            for (String sub : trending) {
                validateModel(reddit.getSubreddit(sub));
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testGet() {
        try {
            String[] nameArray = {"t5_31qvo", "t3_92dd8", "t1_c0b6xx0"};
            List<String> names = Arrays.asList(nameArray);
            Listing<Thing> listing = reddit.get(nameArray);
            assertEquals(listing.size(), names.size());

            for (Thing t : listing) {
                assertTrue(names.contains(t.getFullName()));
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testGetRecommendations() {
        try {
            List<String> subs = Arrays.asList("programming", "java", "git");
            List<String> omit = Collections.singletonList("git");
            List<String> recommendations = reddit.getRecommendations(subs, omit);
            // This endpoint is extremely unstable and sometimes yields no result. The test will pass if the request
            // succeeds and the Content-Type is application/json.
            //assertFalse(recommendations.isEmpty());
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testSubmissionRemovalReason() {
        try {
            Submission removedSubmission = reddit.getSubmission("35urvq");
            assertEquals(removedSubmission.getRemovalReason(), "legal");
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testCommentRemovalReason() {
        try {
            CommentNode root = reddit.getSubmission("35urvq").getComments();
            Optional<CommentNode> illegalComment = root.findChild("t1_cr7z8fp");

            assertTrue(illegalComment.isPresent());
            assertEquals(illegalComment.get().getComment().getRemovalReason(), "legal");
        } catch (NetworkException e) {
            handle(e);
        }
    }
}
