<img src="https://raw.githubusercontent.com/mattbdean/JRAW/master/art/header.png" alt="The Java Reddit API Wrapper" />

[![travis-ci build status](https://img.shields.io/travis/mattbdean/JRAW.svg)](https://travis-ci.org/mattbdean/JRAW)
[![Latest release](https://img.shields.io/github/release/mattbdean/JRAW.svg)](https://bintray.com/thatjavanerd/maven/JRAW/_latestVersion)
[![Kotlin 1.2.10](https://img.shields.io/badge/Kotlin-1.2.10-blue.svg)](http://kotlinlang.org)
[![API coverage](https://img.shields.io/badge/API_coverage-44%25-9C27B0.svg)](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md)
[![Codecov branch](https://img.shields.io/codecov/c/github/mattbdean/JRAW.svg)](https://codecov.io/gh/mattbdean/JRAW)

```groovy
repositories {
    jcenter()
}
dependencies {
    implementation "net.dean.jraw:JRAW:$jrawVersion"
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

`VERSION` can be a specific commit hash (like [`d6843bf`](https://jitpack.io/com/github/mattbdean/JRAW/d6843bf/javadoc/index.html)), a tag (like [`v1.0.0`](https://jitpack.io/com/github/mattbdean/JRAW/v1.0.0/javadoc/index.html)), or the HEAD of a branch (like [`master-SNAPSHOT`](https://jitpack.io/com/github/mattbdean/JRAW/master-SNAPSHOT/javadoc/index.html)).

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

## Releasing

Define these variables in `gradle.properties`:

```properties
# Go to gitbook.com -> Account Settings -> Applications/Tokens to get an API key
gitbookUsername=<gitbook username>
gitbookPassword=<gitbook API key or password>

# Go to bintray.com -> Edit Profile -> API Key to get your account's API key
bintrayUser=<bintray username>
bintrayKey=<bintray API key>

# If this property doesn't match the target release, all release-related tasks
# will be disabled
authorizeRelease=<version to release>
```

Update the version in the root [build.gradle](https://github.com/mattbdean/JRAW/blob/master/build.gradle) and then run the `:lib:release` task to perform a release.

```
$ ./gradlew release --no-daemon --console plain
```

This task will:

 1. Clean everything and run `:lib`'s tests
 2. Run `:meta:update` (see [here](https://github.com/mattbdean/JRAW/tree/master/meta) for what this does)
 3. Creates a commit for the version. This commit must be pushed manually later.
 4. Updates the GitBook site and creates a new tag in the Git repo.
 5. Uploads artifacts (sources, Javadoc, and compiled) to [Bintray](https://bintray.com/thatjavanerd/maven/JRAW)

After running the task:

 1. Push the newly-created commit
 2. Create a [GitHub release](https://github.com/mattbdean/JRAW/releases/new) targeting that commit. Attach all jars generated in `lib/build/libs`.
 3. Publish the uploaded jars on [Bintray](https://bintray.com/thatjavanerd/maven/JRAW)
