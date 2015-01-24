package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.managers.ThingCache;
import net.dean.jraw.models.*;
import net.dean.jraw.paginators.Paginators;
import net.dean.jraw.paginators.SubredditPaginator;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
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
            SubredditPaginator frontPage = Paginators.frontPage(reddit);
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
    public void testEmbeddedMedia() {
        try {
            SubredditPaginator frontPage = Paginators.frontPage(reddit);
            Listing<Submission> submissions = frontPage.next();

            int count = 0;
            // Validate all the EmbeddedMedia models on the front page
            for (Submission s : submissions) {
                if (s.getEmbeddedMedia() != null) {
                    validateModel(s.getEmbeddedMedia());
                    count++;
                }
            }

            if (count == 0) {
                // Did not perform any validation, so the test was essentially skipped.
                throw new SkipException("No EmbeddedMedia models were found on the front page");
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

            Listing<Comment> comments = submission.getComments();
            // This is one of the most upvoted links in reddit history, there's bound to be more than one comment
            assertFalse(comments.isEmpty());

            validateModels(comments);

            RedditResponse response = reddit.execute(RestRequest.Builder.from("GET", new URL(submission.getShortURL()))
                    .expected(MediaTypes.HTML.type())
                    .build());
            assertTrue(JrawUtils.typeComparison(response.getType(), MediaTypes.HTML.type()));
        } catch (NetworkException | MalformedURLException e) {
            handle(e);
        }
    }

    @Test
    public void testMoreChildren() {
        try {
            ThingCache.instance().setEnabled(true);
            Submission submission = reddit.getSubmission("92dd8");
            More more = submission.getComments().getMoreChildren();

            // Top-comments in the "more" node at the end of the thread "id" should be the fullname of the submission
            List<Thing> comments = reddit.getMoreThings(submission, CommentSort.TOP, more);

            for (Thing t : comments) {
                validateModel(t);
            }

            //Test loading more comment replies from comment:
            //http://www.reddit.com/r/pics/comments/92dd8/test_post_please_ignore/c0b715s
            Comment comment = (Comment) ThingCache.instance().getThing("t1_c0b715s");
            Comment[] loadedComments = comment.getReplies().loadMoreChildren(reddit, submission, comment, CommentSort.TOP);

            //Check if the expected parent is really the parent
            for (Comment c : loadedComments) {
                Comment parentComment = (Comment) ThingCache.instance().getThing(c.getParentId());
                boolean isCChildOfParent = false;
                for (Comment child : parentComment.getReplies()) {
                    if (child == c) {
                        isCChildOfParent = true;
                        break;
                    }
                }
                assertTrue(isCChildOfParent);
            }
        } catch (NetworkException | ApiException | IllegalArgumentException e) {
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
            reddit.getStylesheet(null);
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
    public void testLiveThread() {
        try {
            LiveThread t = reddit.getLiveThread("ts4r8m1g99ys");
            validateModel(t);
        } catch (NetworkException e) {
            handle(e);
        }
    }
}
