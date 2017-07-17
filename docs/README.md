# JRAW Documentation

This is a meta-project dedicated to generating the static site hosted on the [GitHub pages](https://thatjavanerd.github.io/JRAW). This site will be available with the release of JRAW [v1.0.0](https://github.com/thatJavaNerd/JRAW/milestone/3).

## TL;DR

Run `./gradlew :docs:buildSite` and then see the `build/docs` subdirectory.

## How it works

The [`net.dean.jraw.docs.samples`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/docs/src/main/java/net/dean/jraw/docs/samples) package contains classes with `@CodeSample` methods to be used in documentation. For example:

```java
public class Example {
    
    @CodeSample
    private static void showSomething() {
        String x = "foo";
        System.out.println(x);
    }
    
    @CodeSample
    private static void doSomething() {
        int y = 10;
        int x = 4;
        int z = x * y;
    }
}
```

Each .java file in the package gets parsed into its abstract syntax tree at runtime so we can have access to the name and the content of each method. In the example above, we will have access to two code samples with the names `Example.showSomething` and `Example.doSomething`

Each markdown file in `src/main/resources/content` will be rendered to an HTML file. These markdown files have access to the code samples using a special syntax:

<pre lang="no-highlight"></code>
```@Example.showSomething
_
```
</code></pre>

This is equivalent to writing

<pre lang="no-highlight"></code>
```java
String x = "foo";
System.out.println(x);
```
</code></pre>

If there is no content in the code block it won't be rendered. The standard here is to include a single underscore. Note that it won't show up in the final HTML.

We can also include a link to a JRAW class using this syntax:

```
[[@RedditClient]]
```

This is essentially equivalent to writing

```
[RedditClient](http://host.com/path/to/javadoc/net/dean/jraw/RedditClient.html)
```

If your code contains `<` or `>`, make sure to specify that you want those escaped. This only works when not referencing a code sample.

<pre lang="no-highlight"></code>
```xml|escapeHtml
&lt;this&gt;will render properly&lt;/this&gt;
```
</code></pre>


## Why not include code samples directly?

As the documentation grows and the library evolves, it will be difficult to manually check for syntax errors. This method forces us to keep all of our code samples up-to-date and syntactically valid.
