# Ratelimiting

reddit allows up to 60 requests per minute to its OAuth2 API before it starts denying requests. By defualt, [[@RedditClient]] handles this by allowing generally one request per second, and "bursting" up to five at a time. This means that if more than five seconds go by with no requests, and the user tries to send ten reuqests at once, it will send the first five as fast as possible and the remaining five at a rate of one per second. This is a fairly conservative burst limit that generally never becomes an issue.

If this doesn't satisfy you or you want to tweak the burst limit, you can create your own [[@RateLimiter]] class or use the provided [[@LeakyBucketRateLimiter]] implementation.

{{ Ratelimiting.newRateLimiter }}
