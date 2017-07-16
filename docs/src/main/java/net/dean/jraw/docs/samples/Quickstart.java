package net.dean.jraw.docs.samples;

import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.http.UserAgent;

@SuppressWarnings("unused")
public class Quickstart {

    @CodeSample
    private static void userAgent() {
        UserAgent myUserAgent = new UserAgent("desktop", "net.dean.awesomescript", "v0.1", "thatJavaNerd");
    }
}
