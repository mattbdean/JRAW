package net.dean.jraw.endpoints;

import net.dean.jraw.Endpoint;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class is responsible for parsing {@value #ALL_ENDPOINTS_FILE_NAME} into a Endpoint objects
 */
public class EndpointAnalyzer {
    private static final String KEY_MD5 = "md5sum";
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

    private static String getMd5Sum(File f) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(f));
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
        String jsonHash = null;

        if (args.length != 3) {
            throw new IllegalArgumentException("Must have three arguments (java=<file>, md=<file>, and md5sum=<file>");
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
                case KEY_MD5:
                    jsonHash = kvPair[1];
                    break;
                default:
                    throw new IllegalArgumentException("No generator for \"" + kvPair[0] + "\"");
            }
        }

        if (java == null || md == null || jsonHash == null) {
            System.err.println("Missing argument(s)");
            return;
        }

        EndpointAnalyzer endpointAnalyzer = new EndpointAnalyzer();

        boolean runMd = false;
        boolean runJava = false;

        String md5 = getMd5Sum(endpointAnalyzer.jsonEndpoints);

        File jsonHashFile = new File(jsonHash);
        String md5Sum = new String(Files.readAllBytes(Paths.get(jsonHashFile.toURI()))).trim();
        if (!md5Sum.equals(md5)) {
            runMd = true;
            runJava = true;

            // Update the md5 file
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(jsonHashFile.toURI()));
            bw.write(getMd5Sum(endpointAnalyzer.jsonEndpoints));
            bw.close();
        } else {
            // Enable specific generators if their files don't exist
            if (!new File(md).exists()) {
                runMd = true;
            }
            if (!new File(java).exists()) {
                runJava = true;
            }
        }


        if (runMd || runJava) {
            if (runMd) {
                new MarkdownGenerator(endpointAnalyzer.getEndpoints()).generate(new File(md));
            }
            if (runJava) {
                new JavaGenerator(endpointAnalyzer.getEndpoints()).generate(new File(java));
            }
        } else {
            System.out.println(String.format("%s has not been modified and both files (\"%s\" and \"%s\") exist; not running.",
                    endpointAnalyzer.jsonEndpoints.getName(), new File(md).getAbsolutePath(), new File(java).getAbsolutePath()));

        }
    }
}
