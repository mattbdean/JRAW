package net.dean.jraw.endpoints;

import net.dean.jraw.Endpoint;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for parsing {@value #ALL_ENDPOINTS_FILE_NAME} into Endpoint objects
 */
public class EndpointAnalyzer {
    /**
     * The name of the file that contains a JSON mapping of all the endpoints and their
     * respective categories.
     */
    protected static final String ALL_ENDPOINTS_FILE_NAME = "endpoints.json";
    private static final String KEY_GEN_MARKDOWN = "md";
    private static final String KEY_GEN_JAVA = "java";
    private static final String KEY_UPD_README = "readme";

    /** The file that contains a JSON mapping of all the endpoints and their respective categories */
    private File jsonEndpoints;
    /** The ObjectMapper used to read {@value #ALL_ENDPOINTS_FILE_NAME} file */
    private ObjectMapper mapper;
    private List<Endpoint> endpoints;

    /**
     * Instantiates a new EndpointAnalysis
     */
    private EndpointAnalyzer() {
        this.mapper = new ObjectMapper();
        try {
            this.jsonEndpoints = new File(EndpointAnalyzer.class.getResource("/" + ALL_ENDPOINTS_FILE_NAME).toURI());
        } catch (URISyntaxException e) {
            JrawUtils.logger().error("Could not find endpoints.json", e);
        }

        List<Endpoint> endpoints = findEndpoints();
        // Sort by if it was implemented, then by URI, then by HTTP method
        Collections.sort(endpoints);

        this.endpoints = endpoints;
    }

    /**
     * Reads {@link #jsonEndpoints} and parses Endpoint objects from it
     *
     * @return A list of all the endpoints noted in the file
     */
    private List<Endpoint> findEndpoints() {
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

            for (final Method m : methods) {
                EndpointImplementation endpointImpl = m.getAnnotation(EndpointImplementation.class);
                for (final Endpoints e : endpointImpl.value()) {

                    for (Endpoint endpoint : endpoints) {
                        if (endpoint.getRequestDescriptor().equals(e.getEndpoint().getRequestDescriptor())) {
                            endpoint.implement(m);
                            break;
                        }
                    }
                }
            }

            return endpoints;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    /**
     * This is the main method of the endpoints subproject.
     * @param args A String array consisting of three elements. It must contain {@code java=<file>} to specify a target
     *             for {@link JavaGenerator}, {@code md=<file>} for {@link MarkdownGenerator}, and {@code md5sum=<file>}
     *             to store the MD5 of "endpoints.json".
     * @throws IOException If there was a problem reading/writing to files
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Started " + EndpointAnalyzer.class.getSimpleName() + " using arguments " + Arrays.toString(args));
        String md = null;
        String java = null;
        String readme = null;

        if (args.length != 3) {
            throw new IllegalArgumentException("Must have three arguments (java=<file>, md=<file>, and readme=<file>");
        }

        for (String arg : args) {
            if (!arg.contains("=")) {
                throw new IllegalArgumentException("Not a key/value argument: " + arg);
            }

            String[] kvPair = arg.trim().split("=");
            if (kvPair.length != 2) {
                throw new IllegalArgumentException("More than one assignment: " + arg);
            }

            for (int i = 0; i < kvPair.length; i++) {
                kvPair[i] = kvPair[i].trim();
            }

            switch(kvPair[0].toLowerCase()) {
                case KEY_GEN_MARKDOWN:
                    md = kvPair[1];
                    break;
                case KEY_GEN_JAVA:
                    java = kvPair[1];
                    break;
                case KEY_UPD_README:
                    readme = kvPair[1];
                    break;
                default:
                    throw new IllegalArgumentException("No generator for \"" + kvPair[0] + "\"");
            }
        }

        if (java == null || md == null || readme == null) {
            System.err.println("Missing argument(s)");
            return;
        }

        EndpointAnalyzer endpointAnalyzer = new EndpointAnalyzer();

        new MarkdownGenerator(endpointAnalyzer.getEndpoints()).generate(new File(md));
        new JavaGenerator(endpointAnalyzer.getEndpoints()).generate(new File(java));
        new ReadmeUpdater(endpointAnalyzer.getEndpoints()).generate(new File(readme));
    }
}
