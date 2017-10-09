package net.dean.jraw.docs.samples;

import net.dean.jraw.RedditClient;
import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.ratelimit.RateLimiter;

@SuppressWarnings("unused")
final class Ratelimiting {
    @CodeSample
    void newRateLimiter(RedditClient redditClient, RateLimiter someOtherRateLimiter) {
        redditClient.setRateLimiter(someOtherRateLimiter);
    }
}
