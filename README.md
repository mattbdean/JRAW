#Java Reddit API Wrapper

[![travis-ci build status](https://travis-ci.org/thatJavaNerd/JRAW.svg?branch=master)](https://travis-ci.org/thatJavaNerd/JRAW) [![Coverage Status](https://img.shields.io/coveralls/thatJavaNerd/JRAW.svg)](https://coveralls.io/r/thatJavaNerd/JRAW) [![Reddit API coverage](https://img.shields.io/badge/api--coverage-44.62%-blue.svg)](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md)

>JRAW is currently in an experimental stage, and therefore subject to application-breaking API changes that could occur at any time before v1.0.

>JRAW requires language features that are only available in Java 8 and above. Please adjust your project accordingly. Voice your opinion on the matter at [issue #12](https://github.com/thatJavaNerd/JRAW/issues/12).

##Features
 - Mini-framework that wraps Square's OkHttp
 - (Optional) request management to prevent sending over 30 requests per minute
 - Full multireddit support
 - Full captcha support
 - Basic wiki access
 - Ability to iterate through posts on the front page or specific subreddits, with support for limits, sortings (hot, new, top, etc.) and time periods when using 'top' (day, week, all, etc.)
 - Get random posts and subreddits
 - Get trending subreddits
 - Get the submit text of a subreddit
 - Searching subreddits
 - Comment on/vote on/submit posts
 - Hide/unhide and save/unsave posts
 - Delete posts and comments
 - Reply to a post or comment
 - Adding/removing developers from [Reddit apps](https://ssl.reddit.com/prefs/apps/)
 - Iterate through new/popular subreddits
 - Get your subscribed subreddits
 - Iterate through your posts/comments/saved/hidden posts and comments, etc.
 - Lots of smaller features not mentioned here
 - Get a subreddit's stylesheet
 - Search for subreddits (by topic and by name)

##Getting Started
####Adding the Dependency

JRAW is hosted on Bintray's [jCenter](https://bintray.com/thatjavanerd/maven/JRAW/view).

**Gradle**:
```groovy
repositories {
    jcenter()
}

dependencies {
    compile(group: 'net.dean.jraw', name: 'JRAW', version: '0.4.0')
}
```

**Maven**:

Add jCenter to your repositories (see [here](https://bintray.com/bintray/jcenter) and press "Set me up!" on the right hand side) and then add the repository:

```xml
<dependency>
    <groupId>net.dean.jraw</groupId>
    <artifactId>JRAW</artifactId>
    <version>0.4.0</version>
</dependency>
```

####Using the Library
See [the wiki](https://github.com/thatJavaNerd/JRAW/wiki/Home) to get you up and running

Javadoc can be found [here](https://thatjavanerd.github.io/JRAW/)

##Building

JRAW uses Gradle as its build system. If you're coming from a Maven background, you can read [this StackOverflow question](http://stackoverflow.com/q/7719495/1275092) to help you get started.

`./gradlew release` will generate four Jar files in `build/releases/`: a normal jar with just the library, a "fat" jar with all of JRAW's runtime dependencies, a Javadoc jar, and a sources jar. See [here](https://github.com/thatJavaNerd/JRAW/releases/tag/v0.2.0) for an example.

`./gradlew test` will run the unit tests

##Contributing

Before contributing, it is recommended that you have a decent knowledge of how the Reddit API works.

Some references:
 - [reddit/reddit's 'API' wiki page](https://github.com/reddit/reddit/wiki/API): Quick overview of the API and its rules
 - [reddit/reddit's 'JSON' wiki page](https://github.com/reddit/reddit/wiki/JSON): Shows the data structure of the objects returned by the API
 - And of course, don't forget the [official Reddit API documentation](https://www.reddit.com/dev/api)

Want to contribute? Follow these steps:

1. Fork the repository
2. Put your testing user's credentials in `/src/test/java/resources/credentials.txt`. The first line should be the username, and the second should be its password.
3. Add your code. Implement an API endpoint, make the code prettier, or even just fix up some whitespace or documentation.
4. Write some TestNG unit tests relevant to your changes
5. Test your code by executing `./gradlew test`
6. Update `ENDPOINTS.md` and `Endpoints.java` by running `./gradlew endpoints:update`
6. Send the pull request

####Creating a user for unit testing
1. Create a multireddit whose name is *not* "jraw_testing", containing at least one subreddit
2. Be a moderator of at least one subreddit. See [here](https://www.reddit.com/subreddits/create) to create one.
3. Submit at least one post (how about on [/r/jraw_testing2](http://www.reddit.com/r/jraw_testing2)?)


