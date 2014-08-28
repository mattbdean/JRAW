package net.dean.jraw.endpoints;

import javassist.ClassPool;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.dean.jraw.EndpointImplementation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

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
public class EndpointAnalysis {

    private static final int LINE_NUM_OFFSET = -1;
    private static final String FILE_NAME = "ENDPOINTS.md";
    private static final String ALL_ENDPOINTS_FILE_NAME = "endpoints.json";
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();

    /** The file that represents {@value #FILE_NAME} */
    private File exportFile;
    /** The file that contains a JSON mapping of all the endpoints and their respective categories */
    private File jsonEndpoints;
    /** The ObjectMapper used to read {@value #ALL_ENDPOINTS_FILE_NAME} file */
    private ObjectMapper mapper;
    /** The DateFormat that will be used to generate the timestamp comment at the beginning of the file */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    /**
     * Instantiates a new EndpointAnalysis
     */
    public EndpointAnalysis() {
        this.mapper = new ObjectMapper();
        try {
            this.jsonEndpoints = new File(EndpointAnalysis.class.getResource("/" + ALL_ENDPOINTS_FILE_NAME).toURI());
            // Json file located in "JRAW/build/resources/main/" when run, go up 4 directories
            this.exportFile = jsonEndpoints;
            for (int i = 0; i < 5; i++) {
                exportFile = exportFile.getParentFile();
            }
            // "JRAW/ENDPOINTS.md"
            exportFile = new File(exportFile, FILE_NAME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a list of implemented and unimplemented endpoints and writes them to the {@link #exportFile}.
     */
    public void run() {
        List<Endpoint> endpoints = getEndpoints();
        // Sort by if it was implemented, then by URI, then by HTTP method
        Collections.sort(endpoints, (Endpoint endpoint, Endpoint endpoint2) -> {
            int implComp = Boolean.compare(endpoint.isImplemented(), endpoint2.isImplemented());
            if (implComp != 0) {
                return implComp;
            } else {
                int nameComp = endpoint.getUri().compareTo(endpoint2.getUri());
                if (nameComp != 0) {
                    return nameComp;
                } else {
                    return endpoint.getVerb().compareTo(endpoint2.getVerb());
                }
            }
        });

        TreeMap<String, List<Endpoint>> categorized = getEndpointsInCategories(endpoints);

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile), StandardCharsets.UTF_8))) {
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
                    getImplementedEndpointsCount(endpoints), endpoints.size()));

            for (Map.Entry<String, List<Endpoint>> category : categorized.entrySet()) {
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
                                getSourceUrl(e.getMethod()));
                    }

                    // ex: `GET`|[`/api/me.json`](https://www.reddit.com/dev/api#GET_api_me.json)|[`RedditClient.me()`](url and line #)
                    // or: `POST`|[`/api/clear_sessions`](https://www.reddit.com/dev/api#POST_api_clear_sessions)|No

                    sb.append('`').append(e.getVerb().name()).append("`|")
                            .append("[`").append(e.getUri()).append("`](").append(getRedditDocUrl(e)).append(")|")
                            .append(implString).append('\n');

                    bw.write(sb.toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getImplementedEndpointsCount(List<Endpoint> endpoints) {
        int counter = 0;
        for (Endpoint e : endpoints) {
            if (e.isImplemented()) {
                counter++;
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

    /**
     * Gets a URL linking to a given method on GitHub.
     *
     * @param m The method to use
     * @return A URL
     */
    private String getSourceUrl(Method m) {
        try {
            String base = "https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/%s.java#L%s";
            CtMethod method = CLASS_POOL.getMethod(m.getDeclaringClass().getName(), m.getName());

            String filePath = method.getDeclaringClass().getName().replace('.', '/');

            return String.format(base, filePath, method.getMethodInfo().getLineNumber(0) + LINE_NUM_OFFSET);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a link to Reddit's official API documentation for a specific endpoint
     *
     * @param endpoint The endpoint to look up
     * @return A URL pointing to the given endpoint
     */
    private String getRedditDocUrl(Endpoint endpoint) {
        String base = endpoint.getVerb().name() + endpoint.getUri().replace('/', '_');
        return "https://www.reddit.com/dev/api#" + base;
    }

    /**
     * Places a list of Endpoints into a map where the key is the category and the value is the list of endpoints that are
     * in that category
     *
     * @param endpoints The Endpoints to use
     * @return A map of the given endpoints
     */
    private TreeMap<String, List<Endpoint>> getEndpointsInCategories(List<Endpoint> endpoints) {
        TreeMap<String, List<Endpoint>> map = new TreeMap<>();
        for (Endpoint e : endpoints) {
            if (map.get(e.getCategory()) == null) {
                map.put(e.getCategory(), new ArrayList<>());
            }

            map.get(e.getCategory()).add(e);
        }

        return map;
    }

    /**
     * Reads {@link #jsonEndpoints} and parses Endpoint objects from it
     *
     * @return A list of all the endpoints noted in the file
     */
    private List<Endpoint> getEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();

        try {
            JsonNode rootNode = mapper.readTree(jsonEndpoints);

            for (Iterator<Map.Entry<String, JsonNode>> it = rootNode.getFields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();

                for (JsonNode endpoint : entry.getValue()) {
                    endpoints.add(new Endpoint(endpoint.asText(), entry.getKey()));
                }
            }

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
                .setScanners(new MethodAnnotationsScanner()));

            Set<Method> methods = reflections.getMethodsAnnotatedWith(EndpointImplementation.class);

            for (Method m : methods) {
                EndpointImplementation endpointImpl = m.getAnnotation(EndpointImplementation.class);
                for (String uri : endpointImpl.uris()) {
                    endpoints.stream().filter(endpoint -> endpoint.getHttpDescriptor().equals(uri)).forEach(endpoint -> {
                        endpoint.setMethod(m);
                        endpoint.setImplemented(true);
                    });
                }
            }

            return endpoints;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        new EndpointAnalysis().run();
    }
}
