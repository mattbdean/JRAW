#Java Reddit API Wrapper

[![travis-ci build status](https://img.shields.io/travis/thatJavaNerd/JRAW.svg)](https://travis-ci.org/thatJavaNerd/JRAW) [![Coverage Status](https://img.shields.io/coveralls/thatJavaNerd/JRAW.svg)](https://coveralls.io/r/thatJavaNerd/JRAW) [![Reddit API coverage](https://img.shields.io/badge/api--coverage-61.2%-blue.svg)](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md)

>JRAW is currently in an experimental stage, and therefore subject to application-breaking API changes that could occur at any time before v1.0.

##Notable Features
 - Java 7 compatible
 - Mini HTTP framework that wraps OkHttp
 - Basic site actions (login, vote, submit, comment, read inbox, etc.)
 - Full multireddit support
 - Full captcha support
 - Get trending subreddits
 - Get random posts and subreddits
 - Incubating OAuth2 usage

##Getting Started
####Adding the Dependency

JRAW is hosted on Bintray's [jCenter](https://bintray.com/thatjavanerd/maven/JRAW/view).

**Gradle**:
```groovy
repositories {
    jcenter()
}

dependencies {
    compile(group: 'net.dean.jraw', name: 'JRAW', version: '0.6.0')
}
```

**Maven**:

Add jCenter to your repositories (see [here](https://bintray.com/bintray/jcenter) and press "Set me up!" on the right hand side) and then add the repository:

```xml
<dependency>
    <groupId>net.dean.jraw</groupId>
    <artifactId>JRAW</artifactId>
    <version>0.6.0</version>
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

####Want to contribute? Follow these steps:

1. Fork the repository
2. Put your testing user's credentials in `/src/test/java/resources/credentials.json`. It should be in [this format](https://gist.github.com/thatJavaNerd/e393a7af4c3a8c564833)
3. Add your code. Implement an endpoint, make the code prettier, or even just fix up some whitespace or documentation.
4. Write TestNG tests covering your changes
5. Test your code by executing `./gradlew test`
6. Update `ENDPOINTS.md` and `Endpoints.java` by running `./gradlew endpoints:update`
7. Send the pull request

####Creating a user for unit testing

The [`:testingUser`](https://github.com/thatJavaNerd/JRAW/tree/master/testingUser) subproject is a script to help you get set up with JRAW. To run it, execute `TERM=dumb ./gradlew testingUser:run`. This script will:

1. Register a new user
2. Create an OAuth2 app
3. Record the username, password, client ID, and client secret in `credentials.json`
4. Create a subreddit
5. Create a multireddit
6. Submit a self post to /r/jraw_testing2
