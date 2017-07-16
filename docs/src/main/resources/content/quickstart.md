# Quickstart

## Add the Dependency

JRAW is hosted on Bintray's [jCenter](http://jcenter.bintray.com/net/dean/jraw/JRAW/).

**Gradle**:

```groovy
repositories {
    jcenter()
}

dependencies {
    compile "net.dean.jraw:JRAW:$jrawVersion"
}
```

**Maven**:

Add jCenter (`https://jcenter.bintray.com`) to your repositories and then add the dependency to the `pom.xml`:

```xml|escapeHtml
<dependency>
    <groupId>net.dean.jraw</groupId>
    <artifactId>JRAW</artifactId>
    <version>${jraw.version}</version>
</dependency>
```


## Choosing a User-Agent
The first step in using the Reddit API effectively is making sure you're sending a descriptive User-Agent header. This allows Reddit to block buggy versions of your app while not affecting others. An effective User-Agent consists of four parts:

1. The target platform
2. A unique ID (usually your package)
3. A version
4. A reddit username.

A `UserAgent` for a script named "Awesome Script" could look like this:

```@Quickstart.userAgent
_
```

## Authentication

Now we can create a [[@RedditClient]]

```java
RedditClient redditClient = new RedditClient(myUserAgent);
```

Because reddit uses OAuth2, this RedditClient will need to be authenticated before it can do anything useful. Fortunately, `RedditClient` comes with a helper class: [[@OAuthHelper]]. This guide will assume you picked a script app, since those are the easiest to authenticate. If you need to use a web or installed app, see the [OAuth2 page](https://github.com/thatJavaNerd/JRAW/wiki/OAuth2).

The first thing we need to do is get our credentials in order. JRAW comes with a [[@Credentials]] class to help us organize everything.

```java|escapeHtml
Credentials credentials = Credentials.script("<username>", "<password>", "<clientId>", "<clientSecret>");
```

To use this data, we need to use the RedditClient's OAuthHelper. Since the Credentials we created was for a script, we can make use of OAuthHelper's `eashAuth` method.

```java
OAuthData authData = redditClient.getOAuthHelper().easyAuth(credentials);
```

Finally, we can notify the RedditClient that we have been authorized.

```java
redditClient.authenticate(authData);
```

Now we are fully authorized and are able to make requests to the API successfully! To test it out, try `redditClient.me()`.

Note that you will need to renew your access token after one hour. To do this, see the [OAuth2 page](https://github.com/thatJavaNerd/JRAW/wiki/OAuth2).
