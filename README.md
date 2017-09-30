<img src="https://raw.githubusercontent.com/mattbdean/JRAW/master/art/header.png" alt="The Java Reddit API Wrapper" />

[![travis-ci build status](https://img.shields.io/travis/mattbdean/JRAW.svg)](https://travis-ci.org/mattbdean/JRAW)
[![Latest release](https://img.shields.io/github/release/mattbdean/JRAW.svg)](https://bintray.com/thatjavanerd/maven/JRAW/_latestVersion)
[![Kotlin 1.1.51](https://img.shields.io/badge/Kotlin-1.1.51-blue.svg)](http://kotlinlang.org)
[![API coverage](https://img.shields.io/badge/API_coverage-40%25-9C27B0.svg)](https://github.com/thatJavaNerd/JRAW/blob/kotlin/ENDPOINTS.md)
[![Codecov branch](https://img.shields.io/codecov/c/github/mattbdean/JRAW.svg)](https://codecov.io/gh/mattbdean/JRAW)

> JRAW is currently being rewritten in [Kotlin](https://kotlinlang.org/) for v1.0.0 (see [#187](https://github.com/mattbdean/JRAW/issues/187)). If you'd like to try it out before the official release, please use [Jitpack](https://jitpack.io/#mattbdean/JRAW/master-SNAPSHOT).

```java
// Assuming we have a 'script' reddit app
Credentials oauthCreds = Credentials.script(username, password, clientId, clientSecret);

// Create a unique User-Agent for our bot
UserAgent userAgent = new UserAgent("desktop", "my.cool.bot", "1.0.0", "myRedditUsername");

// Authenticate our client
RedditClient reddit = OAuthHelper.automatic(oauthCreds, new OkHttpNetworkAdapter(userAgent));

// Get info about the user
Account me = reddit.me().about();
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



