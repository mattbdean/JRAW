package net.dean.jraw.endpoints;

import com.google.common.base.Joiner;
import net.dean.jraw.Endpoint;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for the compilation of
 * <a href="https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md">ENDPOINITS.md</a>, which is a collection of
 * implemented and unimplemented Reddit API endpoints.
 */
public class MarkdownGenerator extends AbstractEndpointGenerator {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    /**
     * Instantiates a new AbstractEndpointGenerator
     *
     * @param endpoints A map of endpoints where the key is the category and the value is a list of endpoints in that category
     */
    public MarkdownGenerator(List<Endpoint> endpoints) {
        super(endpoints, true);
    }

    @Override
    protected void _generate(File dest, IndentAwareFileWriter bw) throws IOException {
        // http://stackoverflow.com/a/4829998/1275092
        bw.write(String.format("<!--- Generated %s. Use `gradle endpoints:update` to update. DO NOT MODIFY DIRECTLY -->%n",
                dateFormat.format(new Date())));

        // Main header
        bw.writeLine("#Endpoints\n");
        bw.writeLine("This file contains a list of all the endpoints (regardless of if they have been implemented) that " +
                "can be found at the [official reddit API docs](https://www.reddit.com/dev/api/oauth). To update this file, " +
                "run `gradle endpoints:update`.\n");

        // Summary
        bw.writeLine(String.format("So far **%s** endpoints (out of %s total) have been implemented.",
                getImplementedEndpointsCount(), getTotalEndpoints()));

        for (Map.Entry<String, List<Endpoint>> category : sortEndpoints(endpoints).entrySet()) {
            String catName = category.getKey();
            List<Endpoint> endpointList = category.getValue();

            // Category header
            bw.writeLine("\n## " + catName);
            // Start table
            bw.writeLine("|Method|Endpoint|Implemention|");
            bw.writeLine("|:----:|--------|------------|");

            for (Endpoint e : endpointList) {
                StringBuilder sb = new StringBuilder();

                String implString = "None";
                if (e.isImplemented()) {
                    implString = String.format("[`%s`](%s)",
                            getStringRepresentation(e.getMethod()),
                            getJavadocUrl(e));
                }

                // ex: `|GET`|[`/api/me.json`](https://www.reddit.com/dev/api#GET_api_me.json)|[`RedditClient.me()`](url and line #)|
                // or: `|POST`|[`/api/clear_sessions`](https://www.reddit.com/dev/api#POST_api_clear_sessions)|No|

                sb.append("|`").append(e.getVerb()).append("`|")
                        .append("[`").append(e.getUri()).append("`](").append(getRedditDocUrl(e)).append(")|")
                        .append(implString).append('|');

                bw.writeLine(sb.toString());
            }
        }
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
        return m.getDeclaringClass().getSimpleName() + "." + m.getName() + "(" + formatMethodParameters(m) + ")";
    }
    
    private String formatMethodParameters(Method m) {
        Class<?>[] params = m.getParameterTypes();
        if (params.length == 0) {
            return "";
        }
        String[] parameterClasses = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            parameterClasses[i] = params[i].getSimpleName();
        }

        return Joiner.on(", ").join(parameterClasses);
    }

}
