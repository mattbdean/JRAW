# The Basics

JRAW was written with one thing in mind: simplicity. The programmer should be able to describe what they want to do quickly and intuitively without having to worry about sending HTTP requests and parsing JSON. If you'd like to know exactly how this is done, check out the [internals page](internals.md).

The fluent API is built on References. A [[@Reference]] is a cheap to create, immutable, abstract pointer to a reddit resource that may or may not exist. All References are created from RedditClients or other References.

The [[@RedditClient]] class handles HTTP requests, websocket connections, and error detection in parsed JSON. It also serves as the root of all Reference chains like this one:

{{ Basics.referenceChain }}

RedditClient is also the only class that can send requests to the API (excluding some OAuth2 stuff). Classes that need to send an HTTP request or open up a WebSocket connection do so using RedditClient.

The API is designed to mirror the actions a normal user might take to do the same job on the reddit website. Take for example, updating user flair on a subreddit. To do this, the user would navigate to the subreddit, browse through available flair, and update their flair with a new option.

{{ Basics.updateFlair }}

## Error Handling

Methods annotated with the [[@EndpointImplementation]] annotation have the ability to throw either a [[@net.dean.jraw.http.NetworkException]] or an [[@net.dean.jraw.ApiException]]. ApiExceptions are thrown when an error is detected in the JSON response. A NetworkException is thrown when an HTTP request returns a non-success status code, e.g. 404.
