#Java Reddit API Wrapper [![travis-ci build status](https://travis-ci.org/thatJavaNerd/JRAW.svg?branch=master)](https://travis-ci.org/thatJavaNerd/JRAW)

>JRAW is currently in an experimental stage, and therefore subject to API changes. You are, however, able to use this library in your own projects.

JRAW was built off of two main principles:

1. Provide a solid foundation upon which to send HTTP requests
2. Make using the Reddit API in Java feel as natural as possible

##Building

JRAW uses Gradle as its build system. If you're coming from a Maven background, you can read [Gradle for Maven 2 Users](http://wiki.gradle.org/display/GRADLE/Gradle+for+Maven+2+users) to help you get started

To run the unit tests, use `./gradlew test`

##Models
####Hierarchy
[`RedditObject`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/RedditObject.java) is the superclass for all major models in JRAW, including [`Thing`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/RedditObject.java). All [core models](https://github.com/thatJavaNerd/JRAW/tree/master/src/main/java/net/dean/jraw/models/core) defined by the [Reddit wiki on GitHub](https://github.com/reddit/reddit/wiki/JSON) extend [`Thing`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/Thing.java)

A UML diagram of the models look like this:

![UML](https://i.imgur.com/151gWff.png)

####Data Retrieval

The workings behind getter methods of models are not the same as most Java objects. All models are instantiated with a [Jackson](http://jackson.codehaus.org/) JsonNode. Each getter method retrieves a value from the "data" node (*see [here](http://www.reddit.com/api/me.json) for an example*) by using a key specific to that method.

##API Endpoints
See [`ENDPOINTS.md`](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md) for a list of endpoints that need to implemented and ones that have already been implemented.

####Updating Endpoints
The package [`net.dean.jraw.endpointgen`](https://github.com/thatJavaNerd/JRAW/tree/master/src/main/java/net/dean/jraw/endpointgen) is actually a mini-application that uses annotations and the Reflections library to find methods that implement different API endpoints and then compile them into `ENDPOINTS.md`. Running `./gradlew updateEndpoints` will run the `EndpointAnalyzer` class, which will in turn generate the endpoints file.

##Contributing
Want to contribute? Fantastic! Follow these steps:

1. Fork the repository
2. Put your testing user's credentials in `/src/test/java/resources/credentials.txt`. The first line should be the username, and the second should be its password.
3. Add your code. Implement an API endpoint, make the code prettier, or even just fix up some whitespace or documentation.
4. Add TestNG tests that implement your code
5. Test your code by executing `./gradlew test`
6. Update [`ENDPOINTS.md`](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md) by running `./gradlew updateEndpoints`
6. Send a pull request!
