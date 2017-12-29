<img src="https://raw.githubusercontent.com/mattbdean/JRAW/master/art/header.png" alt="The Java Reddit API Wrapper" />

[![travis-ci build status](https://img.shields.io/travis/mattbdean/JRAW.svg)](https://travis-ci.org/mattbdean/JRAW)
[![Latest release](https://img.shields.io/github/release/mattbdean/JRAW.svg)](https://bintray.com/thatjavanerd/maven/JRAW/_latestVersion)
[![Kotlin 1.2.0](https://img.shields.io/badge/Kotlin-1.2.0-blue.svg)](http://kotlinlang.org)
[![API coverage](https://img.shields.io/badge/API_coverage-39%25-9C27B0.svg)](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md)
[![Codecov branch](https://img.shields.io/codecov/c/github/mattbdean/JRAW.svg)](https://codecov.io/gh/mattbdean/JRAW)

> JRAW is currently being rewritten in [Kotlin](https://kotlinlang.org/) for v1.0.0 (see [#187](https://github.com/mattbdean/JRAW/issues/187)). If you'd like to try it out before the official release, please use [Jitpack](https://jitpack.io/#mattbdean/JRAW/master-SNAPSHOT).

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.mattbdean:JRAW:master-SNAPSHOT'
}
```

## Documentation

The full documentation is available [on GitBooks](https://mattbdean.gitbooks.io/jraw), but here's a sneak peek:

```java
// Assuming we have a 'script' reddit app
Credentials oauthCreds = Credentials.script(username, password, clientId, clientSecret);

// Create a unique User-Agent for our bot
UserAgent userAgent = new UserAgent("bot", "my.cool.bot", "1.0.0", "myRedditUsername");

// Authenticate our client
RedditClient reddit = OAuthHelper.automatic(oauthCreds, new OkHttpNetworkAdapter(userAgent));

// Get info about the user
Account me = reddit.me().about();
```

### Javadoc

JRAW uses JitPack to host its Javadoc.

```
https://jitpack.io/com/github/mattbdean/JRAW/VERSION/javadoc/index.html
```

`VERSION` can be a specific commit hash (like [`d6843bf`](https://jitpack.io/com/github/mattbdean/JRAW/d6843bf/javadoc/index.html)), a tag (like [`v0.9.0`](https://jitpack.io/com/github/mattbdean/JRAW/v0.9.0/javadoc/index.html)), or the HEAD of a branch (like [`master-SNAPSHOT`](https://jitpack.io/com/github/mattbdean/JRAW/master-SNAPSHOT/javadoc/index.html)).

JitPack produces Javadoc only when necessary, so the first time someone accesses the Javadoc for a specific build it may take a little bit.

## Android

JRAW doesn't target Android specifically, but there is an [extension library](https://github.com/mattbdean/JRAW-Android) that solves some quality of life issues. Also be sure to check out the [example app](https://github.com/mattbdean/JRAW-Android/tree/master/example-app) that shows how to get users logged in.

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

In order to get the integration tests of the `docs` module to pass, you'll need [gitbook-cli](https://github.com/GitbookIO/gitbook-cli) installed globally. You shouldn't have to worry about this, as most of the contributions are likely to be towards the core library and not its accessory modules.

