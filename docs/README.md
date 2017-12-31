# JRAW Documentation

This is a meta-project dedicated to generating Markdown files to be hosted on [GitBook](https://mattbdean.gitbooks.io/jraw).

The integration tests require [gitbook-cli](https://github.com/GitbookIO/gitbook-cli) to be installed globally.

```
$ npm install --global gitbook-cli
```

## TL;DR

```
$ ./gradlew :docs:buildSite
$ cd docs/build/docs && gitbook serve
```

## How it works

The [`net.dean.jraw.docs.samples`](https://github.com/thatJavaNerd/JRAW/tree/master/docs/src/main/java/net/dean/jraw/docs/samples) package contains classes with `@CodeSample` methods to be used in documentation. For example:

```java
final class Example {
    
    @CodeSample
    void showSomething() {
        String x = "foo";
        System.out.println(x);
    }
    
    @CodeSample
    void doSomething() {
        int y = 10;
        int x = 4;
        int z = x * y;
    }
    
    void ignored() {
        // This doesn't have the @CodeSample annotation so it gets ignored
    }
}
```

Each .java file in the package gets parsed into its abstract syntax tree at runtime so we can have access to the name and the content of each method. In the example above, we will have access to two code samples with the names `Example.showSomething` and `Example.doSomething`

Each markdown file in `src/main/resources/content` is considered to "pre-compiled" files that have access to a special syntax for referencing JRAW classes and code samples.

Include a code sample like this:

    {{ @Example.showSomething }}

This gets compiled to

<pre lang="no-highlight"></code>
```java
String x = "foo";
System.out.println(x);
```
</code></pre>

We can also include a link to a JRAW class like this:

```
Lorem ipsum dolor [[@RedditClient]] sit amet
```

This gets compiled to

```
Lorem ipsum dolor [RedditClient](https://host.com/path/to/javadoc/net/dean/jraw/RedditClient.html) sit amet
```

Note that when specified, a code sample is the only thing allowed on its line. There may be several documentation links per line. For example, this is fine:

```
{{ @Example.showSomething }}
{{ @Example.doSomething }}

Lorem ipsum [[@RedditClient]] dolor [[@Paginator]] sit amet.
```

but this is not:

```
{{ @Exmaple.showSomething }} {{ @Example.doSomething }}
```

> Quick note: when referencing classes with the `[[@Foo]]` syntax, enums have to be referenced by their fully qualified class names, e.g. `[[@com.example.Foo]]`. This is due to a [quirk in the Reflections library](https://stackoverflow.com/a/35588452).

> Another quick note: when writing code samples, keep lines less than 100 characters so readers don't have to scroll horizontally through code blocks.

## Adding a new chapter

All chapters are arranged in [`toc.json`](https://github.com/mattbdean/JRAW/blob/master/docs/src/main/resources/content/toc.json). This file specifies the table of contents for our book in JSON format. This is essentially an array of [`Chapter`](https://github.com/mattbdean/JRAW/blob/master/docs/src/main/java/net/dean/jraw/docs/Chapter.java)s.

Say our `toc.json` looks like this:

```json
[
  { "file": "foo" },
  { "file": "bar", "title": "Something else" }
]
```

- `file` is the basename of the markdown file relative to the content directory (which is [here](https://github.com/mattbdean/JRAW/tree/master/docs/src/main/resources/content)).
- `title` is the text displayed to the user in the sidebar. If it isn't specified, the capitalized version of `file` is used.

To build the book, we compile `foo.md` and `bar.md` and copy them to the output destination. We also create a `SUMMARY.md` file that GitBooks uses to generate the sidebar. For this specific configuration, the built summary file looks like this:

<pre>
# Summary

* [Foo](foo.md)
* [Something else](bar.md)
</pre>

## Why not include code samples directly?

As the documentation grows and the library evolves, it will be difficult to manually check for syntax errors. This method forces us to keep all of our code samples up-to-date and syntactically valid. A code sample with a compile-time error will fail the CI build.

## Pushing to GitBook

Create `gradle.properties` in the project root:

```properties
gitbookUsername=<gitbook username>
gitbookPassword=<gitbook access token>
```

Then run

```
$ ./gradlew :docs:pushSite
```

## Developer tips

Continuous Gradle builds, using the watch flag when running GitBook, and a LiveReload plugin for your browser can make development a breeze.

In the first terminal:

```sh
$ ./gradlew -t :docs:buildSite
```

And in the second:

```sh
$ cd docs/build/docs && gitbook install && gitbook serve --watch
```

Anytime you make a change to a relevant file, the website will be reloaded in your browser.

If you happen to have [concurrently](https://www.npmjs.com/package/concurrently) installed globally, you can use this command to use a single terminal instead:

```sh
$ concurrently -r -k "./gradlew -t :docs:buildSite" "cd docs/build/docs && gitbook install && gitbook serve --watch"
```
