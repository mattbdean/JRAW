#Java Reddit API Wrapper [![travis-ci build status](https://travis-ci.org/thatJavaNerd/JRAW.svg?branch=master)](https://travis-ci.org/thatJavaNerd/JRAW)

>JRAW is currently in an experimental stage, and therefore subject to application-breaking API changes that could occur at any time before v1.0.

>JRAW requires language features that are only available in Java 8 and above. Please adjust your project accordingly.

##Features
 - Mini-framework that wraps Apache's HttpComponents
 - (Optional) request management to prevent sending over 30 requests per minute
 - Ability to iterate through posts on the front page or specific subreddits, with support for limits, sortings (hot, new, top, etc.) and time periods when using 'top' (day, week, all, etc.)
 - Captcha support
 - Get posts, users, and subreddits by ID (or name, in case of a user)
 - Basic wiki access
 - Get random posts
 - Get the submit text of a subreddit
 - Searching subreddits
 - Trending subreddits
 - Comment on/vote on/submit posts
 - Hide/unhide and save/unsave posts
 - Delete posts and comments
 - Reply to a post or comment
 - Get user's multi reddits
 - Adding/removing developers from [Reddit apps](https://ssl.reddit.com/prefs/apps/)
 - Iterate through new/popular subreddits
 - Get your subscribed subreddits
 - Iterate through your posts/comments/saved/hidden posts and comments, etc.
 - Lots of smaller features not mentioned here
 - Get a subreddit's stylesheet
 - Search for subreddits (by topic and by name)


##Design Philosiphy
JRAW was built off the principle that as few classes as possible should be able to access the internet. Therefore, only [`RestClient`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/http/RestClient.java) and its subclasses (most notably [`RedditClient`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java)) can send HTTP requests. In order for other classes to send HTTP requests, they must acquire a `RestClient`, such as what [`Paginator`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/Paginator.java) does.

All [models](https://github.com/thatJavaNerd/JRAW/tree/master/src/main/java/net/dean/jraw/models) are instantiated with a Jackson `JsonNode` parsed from responses from the Reddit API.

##Getting Started
####Adding the Dependency

JRAW is hosted on Bintray's [jCenter](https://bintray.com/thatjavanerd/maven/JRAW/view).

**Gradle**:
```groovy
repositories {
    jcenter()
}

dependencies {
    compile(group: 'net.dean.jraw', name: 'JRAW', version: '0.3.0')
}
```

**Maven**:

Add jCenter to your repositories (see [here](https://bintray.com/bintray/jcenter) and press "Set me up!" on the right hand side) and then add the repository:

```xml
<dependency>
    <groupId>net.dean.jraw</groupId>
    <artifactId>JRAW</artifactId>
    <version>0.3.0</version>
</dependency>
```

####Using the Library

Get yourself a `RedditClient`

```java
RedditClient reddit = new RedditClient(MY_USER_AGENT);
```

Login (if necessary)

```java
LoggedInAccount me = reddit.login(MY_USERNAME, MY_PASSWORD);
```

Start using the library! Some examples:

```java
// Iterate through the front page
SubredditPaginator frontPage = new SubredditPaginator(reddit); // Second parameter could be a subreddit
while (frontPage.hasNext()) {
    Listing<Submission> submissions = frontPage.next();

    for (Submission submission : submissions.getChildren()) {
        System.out.println(submission.getTitle());
    }
}

// Post a link
URL url = // ...
me.submitContent(new LoggedInAccount.SubmissionBuilder(url, SUBREDDIT, TITLE));

// Post a self-post
String content = // ...
me.submitContent(new LoggedInAccount.SubmissionBuilder(content, SUBREDDIT, TITLE));

// Do stuff with a submission
Submission submission = reddit.getSubmission("28d6vv"); // http://redd.it/28d6vv
me.vote(submission, VoteDirection.UPVOTE);
me.setSaved(submission, true);
me.setHidden(submission, true);
```

For even more examples, see the [unit tests](https://github.com/thatJavaNerd/JRAW/tree/master/src/test/java/net/dean/jraw/test).

Javadoc can be found [here](https://thatjavanerd.github.io/JRAW/docs/0.3.0/)

##Models
####Hierarchy
[`JsonModel`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/JsonModel.java) is the superclass for all models in JRAW, including [`Thing`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/core/Thing.java). All [core models](https://github.com/thatJavaNerd/JRAW/tree/master/src/main/java/net/dean/jraw/models/core) defined by the [Reddit wiki on GitHub](https://github.com/reddit/reddit/wiki/JSON) extend `Thing` (except for `Listing`, which extends `Thing`'s superclass, `RedditObject`

An overview of the models looks like this:

![UML](https://i.imgur.com/151gWff.png)

####Data Retrieval

The workings behind getter methods of models are not the same as most Java objects. All models are instantiated with a [Jackson](http://jackson.codehaus.org/) JsonNode. Each getter method retrieves a value from the "data" node (*see [here](http://www.reddit.com/user/way_fairer/about.json) for an example*) by using a key specific to that method.

##API Endpoints
See [`ENDPOINTS.md`](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md) for full list of Reddit's API endpoints.

####Updating Endpoints
The subproject [`endpoints`](https://github.com/thatJavaNerd/JRAW/tree/master/endpoints) uses annotations and the Reflections library to find methods that implement different API endpoints and then compile them into `ENDPOINTS.md`. Running `./gradlew endpoints:update` will run the [`EndpointAnalyzer`](https://github.com/thatJavaNerd/JRAW/blob/master/endpoints/src/main/java/net/dean/jraw/endpoints/EndpointAnalyzer.java) class, which will in turn generate the endpoints markdown file.

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
1. Create a multireddit containing at least one subreddit
2. Have at least 10 link karma (otherwise you will have to use captchas)
3. Submit at least one post (how about on [/r/jraw_testing2](http://www.reddit.com/r/jraw_testing2)?)
