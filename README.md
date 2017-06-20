<img src="https://raw.githubusercontent.com/thatJavaNerd/JRAW/kotlin/art/header.png" alt="The Java Reddit API Wrapper" />

[![travis-ci build status](https://img.shields.io/travis/thatJavaNerd/JRAW.svg)](https://travis-ci.org/thatJavaNerd/JRAW)
[![Latest release](https://img.shields.io/github/release/thatJavaNerd/JRAW.svg)](https://bintray.com/thatjavanerd/maven/JRAW/_latestVersion)
[![Kotlin 1.1.2-5](https://img.shields.io/badge/Kotlin-1.1.2--5-blue.svg)](http://kotlinlang.org)
[![API coverage](https://img.shields.io/badge/API_coverage-6%25-9C27B0.svg)](https://github.com/thatJavaNerd/JRAW/blob/kotlin/ENDPOINTS.md)
[![Codecov branch](https://img.shields.io/codecov/c/github/thatJavaNerd/JRAW/kotlin.svg)](https://codecov.io/gh/thatJavaNerd/JRAW/branch/kotlin)

This branch is a rewrite of the library in [Kotlin](https://kotlinlang.org/). Please note that this branch is not even close to being production ready! There are still tons of missing features from JRAW v0.9.0.

```kotlin
// Assuming we have a 'script' reddit app
val oauthCreds = Credentials.script(username, password, clientId, clientSecret)

// Create a unique User-Agent for our bot
val userAgent = UserAgent("desktop", "my.cool.bot", "1.0.0", "myRedditUsername")

// Create our RedditClient
val reddit = OAuthHelper.script(oauthCreds, OkHttpAdapter(userAgent))

// GET https://oauth.reddit.com/some/endpoint
val foo = reddit.request { it.path("/some/endpoint") }.deserialize<Foo>()

// Iterate through posts
val pics: Paginator<Submission> = reddit.subreddit("pics").posts()
    .sorting(Sorting.TOP)
    .timePeriod(TimePeriod.ALL)
    .limit(100)
    .build()
    
pics.next().forEach(::println)
```

## Proposed APIs

This section is a draft and is very likely to change as development continues

```kotlin
// Users
val davinci = reddit.user("Shitty_Watercolour")
val about = davinci.info()
davinci.message(...) // send a PM

// Submitting/commenting
val submission = reddit.me().submit("pics", ...)
val comment = submission.comment(...)
val subcomment = comment.comment(...)
```

## Contributing

To get started you'll need to create two [reddit OAuth2 apps](https://www.reddit.com/prefs/apps), one script and one installed.

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
