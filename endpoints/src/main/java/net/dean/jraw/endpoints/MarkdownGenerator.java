package net.dean.jraw.endpoints;

import net.dean.jraw.Endpoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * This class is responsible for the compilation of <a href="https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md">ENDPOINITS.md</a>,
 * which is a collection of implemented and unimplemented Reddit API endpoints.<br>
 *
 * Here is a basic outline of how the class works:
 * <ol>
 *     <li>Read all the endpoints from endpoints.json
 *     <li>Determine if the endpoints are implemented
 *     <li>Find the corresponding methods for implemented methods
 *     <li>Output as table
 * </ol>
 */
public class MarkdownGenerator extends AbstractEndpointGenerator {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    /**
     * Instantiates a new AbstractEndpointGenerator
     *
     * @param endpoints A map of endpoints where the key is the category and the value is a list of endpoints in that category
     */
    public MarkdownGenerator(NavigableMap<String, List<Endpoint>> endpoints) {
        super(endpoints);
    }

    @Override
    protected void _generate(File dest, BufferedWriter bw) throws IOException {
        // http://stackoverflow.com/a/4829998/1275092
        bw.write(String.format("<!--- Generated %s. Use ./gradlew endpoints:update to update. DO NOT MODIFY DIRECTLY -->\n",
                DATE_FORMAT.format(new Date())));

        // Main header
        bw.write("#Endpoints\n\n");
        bw.write("This file contains a list of all the endpoints (regardless of if they have been implemented) that " +
                "can be found at the [official Reddit API docs](https://www.reddit.com/dev/api). To update this file, " +
                "run `./gradlew endpoints:update`.\n\n");

        // Summary
        bw.write(String.format("So far **%s** endpoints (out of %s total) have been implemented.\n",
                getImplementedEndpointsCount(), getTotalEndpoints()));

        for (Map.Entry<String, List<Endpoint>> category : endpoints.entrySet()) {
            String catName = category.getKey();
            List<Endpoint> endpointList = category.getValue();

            // Category header
            bw.write("\n##" + catName + "\n");
            // Start table
            bw.write("Method|Endpoint|Implemented?\n");
            bw.write(":----:|--------|------------\n");

            for (Endpoint e : endpointList) {
                StringBuilder sb = new StringBuilder();

                String implString = "No";
                if (e.isImplemented()) {
                    implString = String.format("[`%s`](%s)",
                            getStringRepresentation(e.getMethod()),
                            getJavadocUrl(e));
                }

                // ex: `GET`|[`/api/me.json`](https://www.reddit.com/dev/api#GET_api_me.json)|[`RedditClient.me()`](url and line #)
                // or: `POST`|[`/api/clear_sessions`](https://www.reddit.com/dev/api#POST_api_clear_sessions)|No

                sb.append('`').append(e.getVerb()).append("`|")
                        .append("[`").append(e.getUri()).append("`](").append(getRedditDocUrl(e)).append(")|")
                        .append(implString).append('\n');

                bw.write(sb.toString());
            }
        }
    }

    private int getTotalEndpoints() {
        int counter = 0;

        for (Map.Entry<String, List<Endpoint>> endpoints : this.endpoints.entrySet()) {
            counter += endpoints.getValue().size();
        }

        return counter;
    }

    private int getImplementedEndpointsCount() {
        int counter = 0;
        for (Map.Entry<String, List<Endpoint>> endpoints : this.endpoints.entrySet()) {
            for (Endpoint e : endpoints.getValue()) {
                if (e.isImplemented()) {
                    counter++;
                }
            }
        }

        return counter;
    }

    /**
     * Formats a method to be as succint as possible. The basic format is "{@code {class name}.{method name}()}". For example,
     * a method declared as "{@code public void com.foo.bar.MyClass.myMethod(String, String, int) throws IllegalArgumentException}"
     * would result in "{@code MyClass.myMethod()}"
     *
     * @param m The method to simplify
     * @return A condensed version of the given method
     */
    private String getStringRepresentation(Method m) {
        return m.getDeclaringClass().getSimpleName() + "." + m.getName() + "()";
    }

}
