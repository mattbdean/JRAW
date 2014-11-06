package net.dean.jraw.endpoints;

import net.dean.jraw.Endpoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

/**
 * This class generates an enum called Endpoints.java.
 */
public class JavaGenerator extends AbstractEndpointGenerator {
    private static final String INDENT = "    ";
    private static final String COMMENT_WARNING =
            "/* This class is updated by running ./gradlew endpoints:update. Do not modify directly */";
    private static final String JAVADOC_CLASS =
            "/** This class is an automatically generated enumeration of Reddit's API endpoints */";
    private static final String JAVADOC_ENUM =
            "/** Represents the endpoint \"<a href=\"%s\">{@code %s}</a>\" included in the \"%s\" scope */";
    private static final String JAVADOC_GET_ENDPOINT = "Gets the Endpoint object associated with this enumeration";
    private static final String JAVADOC_GET_ENDPOINT_RETURN = "The Endpoint object";
    private static final Map<String, String> PREFIX_SUBSTITUTIONS;
    private static final Map<String, String> POSTFIX_SUBSTITUTIONS;

    /**
     * Instantiates a new JavaGenerator
     *
     * @param endpoints A map of endpoints where the key is the category and the value is a list of endpoints in that category
     */
    public JavaGenerator(List<Endpoint> endpoints) {
        super(endpoints, true);
    }


    @Override
    protected void _generate(File dest, IndentAwareFileWriter writer) throws IOException {
        writer.write("package net.dean.jraw;");
        writer.newline();

        writer.write(COMMENT_WARNING);
        writer.write(JAVADOC_CLASS);
        writer.write("@SuppressWarnings(\"unused\")");
        writer.write("public enum Endpoints {");
        writer.incIndent();

        List<String> duplicateUris = findDuplicateUris(endpoints);

        int catCounter = 0;

        NavigableMap<String, List<Endpoint>> sorted = sortEndpoints(endpoints);

        for (Map.Entry<String, List<Endpoint>> entry : sorted.entrySet()) {

            writer.newline();
            writer.write("///////// " + entry.getKey() + " /////////");

            int endpointCounter = 0;
            for (Endpoint endpoint : entry.getValue()) {
                writer.write(String.format(JAVADOC_ENUM, getRedditDocUrl(endpoint), endpoint.getRequestDescriptor(), endpoint.getScope().replace("&", "&amp;")));
                writer.write(String.format("%s(\"%s\")%s", generateEnumName(endpoint, duplicateUris.contains(endpoint.getUri())), endpoint.getRequestDescriptor(),
                        catCounter == sorted.size() - 1 && endpointCounter == entry.getValue().size() - 1 ? ";" : ","));

                endpointCounter++;
            }

            catCounter++;
        }

        writer.newline();
        writer.write("private final net.dean.jraw.Endpoint endpoint;");
        writer.newline();
        writer.write("private Endpoints(String requestDescriptor) {");
        writer.incIndent();
        writer.write("this.endpoint = new Endpoint(requestDescriptor);");
        writer.decIndent();
        writer.write("}");
        writer.newline();
        writer.write("/**");
        writer.write("  * " + JAVADOC_GET_ENDPOINT);
        writer.write("  * @return " + JAVADOC_GET_ENDPOINT_RETURN);
        writer.write("  */");
        writer.write("public final net.dean.jraw.Endpoint getEndpoint() {");
        writer.incIndent();
        writer.write("return endpoint;");
        writer.decIndent();
        writer.write("}");

        writer.newline();

        writer.write("@Override");
        writer.write("public java.lang.String toString() {");
        writer.incIndent();
        writer.write("return endpoint.toString();");
        writer.decIndent();
        writer.write("}");
        writer.decIndent();
        writer.write("}");
    }

    private String generateEnumName(Endpoint ep, boolean isDuplicate) {
        String enumName = ep.getUri();

        // Replace prefixes
        for (Map.Entry<String, String> substitutionEntry : PREFIX_SUBSTITUTIONS.entrySet()) {
            String key = substitutionEntry.getKey();
            String val = substitutionEntry.getValue();
            if (enumName.startsWith(key)) {
                enumName = enumName.replaceFirst(key, val);
            }
        }

        // Replace postfixes
        // TODO: Could be done more accurately with substring()
        for (Map.Entry<String, String> substitutionEntry : POSTFIX_SUBSTITUTIONS.entrySet()) {
            String key = substitutionEntry.getKey();
            String val = substitutionEntry.getValue();
            if (enumName.endsWith(key)) {
                enumName = enumName.replace(key, val);
            }
        }

        enumName = enumName.toUpperCase()
                .replace("/", "_")
                .replace("{", "")
                .replace("}", "");

        if (isDuplicate) {
            enumName += "_" + ep.getVerb();
        }

        return enumName;
    }

    private List<String> findDuplicateUris(List<Endpoint> endpoints) {
        List<String> dupes = new ArrayList<>();

        List<String> uris = new ArrayList<>();
        for (Endpoint e : endpoints) {
            uris.add(e.getUri());
        }
        dupes.addAll(findDuplicates(uris));

        return dupes;
    }

    private <T> List<T> findDuplicates(List<T> list) {
        Set<T> duplicates = new LinkedHashSet<>();
        Set<T> uniques = new HashSet<>();

        for (T obj : list) {
            if (!uniques.add(obj)) {
                duplicates.add(obj);
            }
        }

        List<T> returnList = new ArrayList<>(duplicates.size());

        returnList.addAll(duplicates);

        return returnList;
    }

    private void write(BufferedWriter bw, int indents, String msg) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indents; i++) {
            sb.append(INDENT);
        }
        sb.append(msg);
        sb.append('\n');

        bw.write(sb.toString());
    }

    static {
        PREFIX_SUBSTITUTIONS = new LinkedHashMap<>();
        // Put in order of priority
        PREFIX_SUBSTITUTIONS.put("/api/v1", "OAUTH");
        PREFIX_SUBSTITUTIONS.put("/api/", "");
        PREFIX_SUBSTITUTIONS.put("/r/", "");
        PREFIX_SUBSTITUTIONS.put("/", "");

        POSTFIX_SUBSTITUTIONS = new LinkedHashMap<>();
        POSTFIX_SUBSTITUTIONS.put(".json", "");
    }
}
