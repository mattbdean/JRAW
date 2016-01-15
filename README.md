#Java Reddit API Wrapper

[![travis-ci build status](https://img.shields.io/travis/thatJavaNerd/JRAW.svg)](https://travis-ci.org/thatJavaNerd/JRAW) [![Coverage Status](https://img.shields.io/coveralls/thatJavaNerd/JRAW.svg)](https://coveralls.io/r/thatJavaNerd/JRAW) [![reddit API coverage](https://img.shields.io/badge/api--coverage-63%-blue.svg)](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md) [![Latest release](https://img.shields.io/github/release/thatJavaNerd/JRAW.svg)](https://bintray.com/thatjavanerd/maven/JRAW/_latestVersion)

>JRAW is currently in an experimental stage, and therefore subject to breaking API changes that could occur at any time.

>*Quick Note*: Due to the fact that the unit tests rely on a solid internet connection and that the reddit API is working properly, a failing build *does not* necessarily mean that the library is currently unstable. For example, [this build](https://travis-ci.org/thatJavaNerd/JRAW/builds/69069754) failed because reddit was having issues with SSL at the time. JRAW tries its best to recover from server errors, but it can only do so much.

##Notable Features
 - OAuth2 support
 - Full multireddit support
 - All common actions (login, vote, submit, comment, messages, etc.)
 - Java 7 compatible
 - Simple HTTP framework capable of wrapping most any HTTP library
 - Dynamic ratelimit adjustment

See [Quickstart](https://github.com/thatJavaNerd/JRAW/wiki/Quickstart) to get you up and running. Javadoc can be found [here](https://thatjavanerd.github.io/JRAW#javadoc)

##Building

JRAW uses Gradle as its build system. If you come from a Maven background, see Gradle's [user guide](https://gradle.org/docs/current/userguide/tutorial_using_tasks.html) to get you started.

`gradle release` will generate four Jar files in `build/releases`: a normal jar with just the library, a "fat" jar with all of JRAW's runtime dependencies, a Javadoc jar, and a sources jar. See [here](https://github.com/thatJavaNerd/JRAW/releases/tag/v0.2.0) for an example.

`gradle test` will run the unit tests

##Contributing

Before contributing, it is recommended that you have a decent knowledge of how the reddit API works.

Some references:
 - [reddit/reddit's 'API' wiki page](https://github.com/reddit/reddit/wiki/API): Quick overview of the API and its rules
 - [reddit/reddit's 'JSON' wiki page](https://github.com/reddit/reddit/wiki/JSON): Shows the data structure of the objects returned by the API
 - And of course, don't forget the [official reddit API documentation](https://www.reddit.com/dev/api/oauth)

####Want to contribute? Follow these steps:

1. Fork the repository
2. Put your testing user's credentials in `src/test/resources/credentials.json`. It should be in [this format](https://gist.github.com/thatJavaNerd/e393a7af4c3a8c564833). If you don't have a testing user, see below.
3. Add your code. Implement an endpoint, make the code prettier, or even just fix up some whitespace or documentation.
4. Write TestNG tests covering your changes
5. Test your code by executing `gradle test`
6. Update `ENDPOINTS.md` and `Endpoints.java` by running `gradle endpoints:update` (see [the wiki](https://github.com/thatJavaNerd/JRAW/wiki/Endpoints))
7. Send the pull request

####Creating a user for unit testing

Here's how to create a testing user:

1. Register a new user
2. [Create an OAuth2 app](https://www.reddit.com/prefs/apps)
3. Record the username, password, client ID, and client secret in `src/test/resources/credentials.json`
4. [Create a subreddit](https://www.reddit.com/subreddits/create)
5. [Create a multireddit](http://www.redditblog.com/2013/06/browse-future-of-reddit-re-introducing.html)
6. [Submit a self post to /r/jraw_testing2](https://www.reddit.com/r/jraw_testing2/submit?selftext=true)
