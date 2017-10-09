# Quickstart

## Add the Dependency

JRAW is hosted on Bintray's [jCenter](http://jcenter.bintray.com/net/dean/jraw/JRAW/).

[![Latest release](https://img.shields.io/github/release/mattbdean/JRAW.svg)](https://bintray.com/thatjavanerd/maven/JRAW/_latestVersion)

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

```xml
<dependency>
    <groupId>net.dean.jraw</groupId>
    <artifactId>JRAW</artifactId>
    <version>${jraw.version}</version>
</dependency>
```

## Choose a User-Agent

The first step in using the reddit API effectively is making sure you're sending a descriptive User-Agent header. This allows reddit to block buggy versions of your app while not affecting others. An effective User-Agent consists of four parts:

1. The target platform
2. A unique ID (usually your package)
3. A version
4. A reddit username.

A [[@UserAgent]] for a super useful bot could look like this:

{{ Quickstart.userAgent }}

## Create a reddit OAuth2 app

reddit uses OAuth2 to authenticate 3rd party apps. The first thing you'll need to do is to register your app [here](https://www.reddit.com/prefs/apps). For the sake of simplicity, let's create a script app.

![client ID and client secret](https://i.imgur.com/ILMeklr.png)

You'll need the client ID and client secret later.

## Authenticate

Let's tell JRAW to authenticate our client:

{{ Quickstart.authenticate }}

Here `<username>` and `<password>` must be for the account that created the script.

See the [OAuth2 page](oauth2.md) page for more.
