<img src="https://raw.githubusercontent.com/thatJavaNerd/JRAW/kotlin/art/header.png" alt="The Java Reddit API Wrapper" />

[![travis-ci build status](https://img.shields.io/travis/thatJavaNerd/JRAW.svg)](https://travis-ci.org/thatJavaNerd/JRAW)
[![Latest release](https://img.shields.io/github/release/thatJavaNerd/JRAW.svg)](https://bintray.com/thatjavanerd/maven/JRAW/_latestVersion)
[![Kotlin 1.1.3](https://img.shields.io/badge/Kotlin-1.1.3-blue.svg)](http://kotlinlang.org)
[![API coverage](https://img.shields.io/badge/API_coverage-24%25-9C27B0.svg)](https://github.com/thatJavaNerd/JRAW/blob/kotlin/ENDPOINTS.md)
[![Codecov branch](https://img.shields.io/codecov/c/github/thatJavaNerd/JRAW/kotlin.svg)](https://codecov.io/gh/thatJavaNerd/JRAW/branch/kotlin)

This branch is a rewrite of the library in [Kotlin](https://kotlinlang.org/). Please note that this branch is not even close to being production ready! There are still tons of missing features from JRAW v0.9.0.

```java
// Assuming we have a 'script' reddit app
Credentials oauthCreds = Credentials.script(username, password, clientId, clientSecret);

// Create a unique User-Agent for our bot
UserAgent userAgent = new UserAgent("desktop", "my.cool.bot", "1.0.0", "myRedditUsername");

// Create our RedditClient
RedditClient reddit = OAuthHelper.script(oauthCreds, OkHttpAdapter(userAgent));

// Iterate through posts
Paginator<Submission> pics = reddit.subreddit("pics").posts()
    .sorting(Sorting.TOP)
    .timePeriod(TimePeriod.ALL)
    .limit(100)
    .build();
    
for (Submission s : pics.next())
    System.out.println(s);

// Get user preferences
Map<String, Object> prefs = reddit.me().prefs();
boolean showNsfw = (Boolean) prefs.get("over_18")

// Update preferences
Map<String, Object> newPrefs = new HashMap<>();
newPrefs.put("over_18", true);
reddit.me().patchPrefs(newPrefs);

// Get a user's info
Account user = reddit.user("_vargas_").about();
System.out.println(user.linkKarma);

// Submit a post
reddit.subreddit("pics").submit(SubmissionKind.LINK, "check out this cool website i found",
        "https://www.google.com", false);
```

## Proposed APIs

This section is a draft and is very likely to change as development continues

```java
// Users
UserReference davinci = reddit.user("Shitty_Watercolour")
davinci.message(...) // send a PM

// Submitting/commenting
val submission = reddit.me().submit("pics", ...)
val comment = submission.comment(...)
val subcomment = comment.comment(...)
```

## Contributing

To get started you'll need to create two [reddit OAuth2 apps](https://www.reddit.com/prefs/apps), one script and one installed.

To have this done automatically for you, run this command:

```sh
$ ./gradlew :meta:credentials --no-daemon --console plain
```

`lib/src/test/resources/credentials.json`:

```json
{
    "script": {
        "username": "...",
        "password": "...",
        "clientId": "...",
        "clientSecret": "..."
    },
    "app": {
        "clientId": "...",
        "redirectUrl": "..."
    }
}
```

Then you can go ahead and run the tests

```sh
$ ./gradlew test
```

Tests are written with [Spek](http://spekframework.org/) and assertions are done with [Expekt](https://github.com/winterbe/expekt).

## Project structure

The [`:lib`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib) subproject contains the actual library source code, which also includes some generated sources (see `lib/src/gen`)

[`:meta`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/meta) contains some utilities for doing metadata-based work. It:

 - Generates the [`Endpoint`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/gen/java/net/dean/jraw/Endpoint.java) enum
 - Generates [`ENDPOINTS.md`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/ENDPOINTS.md)
 - Updates the API coverage badge in the README
