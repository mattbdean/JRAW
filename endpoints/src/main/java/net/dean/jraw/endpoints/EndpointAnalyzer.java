package net.dean.jraw.endpoints;

import net.dean.jraw.Endpoint;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
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
import java.util.*;

/**
 * This class is responsible for parsing {@value #ALL_ENDPOINTS_FILE_NAME} into
 */
public class EndpointAnalyzer {
    private static final String ALL_ENDPOINTS_FILE_NAME = "endpoints.json";
    private static final String KEY_GEN_MARKDOWN = "md";
    private static final String KEY_GEN_JAVA = "java";

    /** The file that contains a JSON mapping of all the endpoints and their respective categories */
    private File jsonEndpoints;
    /** The ObjectMapper used to read {@value #ALL_ENDPOINTS_FILE_NAME} file */
    private ObjectMapper mapper;
    private TreeMap<String, List<Endpoint>> endpoints;

    /**
     * Instantiates a new EndpointAnalysis
     */
    public EndpointAnalyzer() {
        this.mapper = new ObjectMapper();
        try {
            this.jsonEndpoints = new File(EndpointAnalyzer.class.getResource("/" + ALL_ENDPOINTS_FILE_NAME).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        List<Endpoint> endpoints = findEndpoints();
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

        this.endpoints = categorizeEndpoints(endpoints);
    }

    /**
     * Places a list of Endpoints into a map where the key is the category and the value is the list of endpoints that are
     * in that category
     *
     * @param endpoints The Endpoints to use
     * @return A map of the given endpoints
     */
    private TreeMap<String, List<Endpoint>> categorizeEndpoints(List<Endpoint> endpoints) {
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

            for (Method m : methods) {
                EndpointImplementation endpointImpl = m.getAnnotation(EndpointImplementation.class);
                for (Endpoints e : endpointImpl.value()) {
                    endpoints.stream().filter(endpoint -> endpoint.getRequestDescriptor().equals(e.getEndpoint().getRequestDescriptor())).forEach(endpoint -> {
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

    public TreeMap<String, List<Endpoint>> getEndpoints() {
        return endpoints;
    }

    public static void main(String[] args) {
        System.out.println("Started " + EndpointAnalyzer.class.getSimpleName() + " using arguments " + Arrays.toString(args));
        String md = null;
        String java = null;

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
                default:
                    throw new IllegalArgumentException("No generator for \"" + kvPair[0] + "\"");
            }
        }

        if (md == null && java == null) {
            System.out.println("No generators given");
            return;
        }

        EndpointAnalyzer endpointAnalyzer = new EndpointAnalyzer();

        if (md != null) {
            new MarkdownGenerator(endpointAnalyzer.getEndpoints()).generate(new File(md));
        }
        if (java != null) {
            new JavaGenerator(endpointAnalyzer.getEndpoints()).generate(new File(java));
        }
    }
}
