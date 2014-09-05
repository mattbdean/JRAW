#Java Reddit API Wrapper [![travis-ci build status](https://travis-ci.org/thatJavaNerd/JRAW.svg?branch=master)](https://travis-ci.org/thatJavaNerd/JRAW)

>JRAW is currently in an experimental stage, and therefore subject to API changes. Application-breaking changes could occur at any release before 1.0.

JRAW was built off these principles:

1. Provide a solid foundation upon which to send HTTP requests
2. Make using the Reddit API feel as comforatble as possible for Java developers

##Building

JRAW uses Gradle as its build system. If you're coming from a Maven background, you can read [this StackOverflow question](http://stackoverflow.com/q/7719495/1275092) to help you get started.

`./gradlew release` will generate four Jar files in `build/releases/`: a normal jar with just the library, a "fat" jar with all of JRAW's runtime dependencies, a Javadoc jar, and a sources jar.

`./gradlew test` will run the unit tests

##Models
####Hierarchy
[`JsonModel`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/JsonModel.java) is the superclass for all models in JRAW, including [`Thing`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/core/Thing.java). All [core models](https://github.com/thatJavaNerd/JRAW/tree/master/src/main/java/net/dean/jraw/models/core) defined by the [Reddit wiki on GitHub](https://github.com/reddit/reddit/wiki/JSON) extend `Thing`

An overview of the models would look like this:

![UML](https://i.imgur.com/151gWff.png)

####Data Retrieval

The workings behind getter methods of models are not the same as most Java objects. All models are instantiated with a [Jackson](http://jackson.codehaus.org/) JsonNode. Each getter method retrieves a value from the "data" node (*see [here](http://www.reddit.com/user/way_fairer/about.json) for an example*) by using a key specific to that method.

##API Endpoints
See [`ENDPOINTS.md`](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md) for a list of endpoints that need to implemented and ones that have already been implemented.

####Updating Endpoints
The subproject [`endpoints`](https://github.com/thatJavaNerd/JRAW/tree/master/endpoints) uses annotations and the Reflections library to find methods that implement different API endpoints and then compile them into `ENDPOINTS.md`. Running `./gradlew endpoints:update` will run the [`EndpointAnalyzer`](https://github.com/thatJavaNerd/JRAW/blob/master/endpoints/src/main/java/net/dean/jraw/endpoints/EndpointAnalyzer.java) class, which will in turn generate the endpoints markdown file.

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
6. Update [`ENDPOINTS.md`](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md) by running `./gradlew endpoints:update`
6. Send the pull request
