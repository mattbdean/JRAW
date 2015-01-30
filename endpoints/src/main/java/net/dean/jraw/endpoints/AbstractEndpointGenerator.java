package net.dean.jraw.endpoints;

import javassist.ClassPool;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.dean.jraw.Endpoint;
import net.dean.jraw.JrawUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * This class provides a template for a class that generates a file based off of {@link Endpoint} objects.
 */
public abstract class AbstractEndpointGenerator {
    protected final List<Endpoint> endpoints;
    protected final boolean overwriteFile;
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    private static final int LINE_NUM_OFFSET = -1;

    /**
     * Instantiates a new AbstractEndpointGenerator
     * @param endpoints A list of endpoints
     * @param overwriteFile If true, then this generator is not re-creating the file, but instead updating it. The
     *                      BufferedWriter in {@link #_generate(File, IndentAwareFileWriter)} will be null.
     */
    public AbstractEndpointGenerator(List<Endpoint> endpoints, boolean overwriteFile) {
        this.endpoints = endpoints;
        this.overwriteFile = overwriteFile;
    }

    /**
     * Generates a file based on the given endpoints
     *
     * @param dest The file to write to
     */
    public final void generate(File dest) {
        System.out.println(String.format("Using %s to %s %s", getClass().getSimpleName(),
                overwriteFile ? "write to" : "update", dest.getAbsolutePath()));

        IndentAwareFileWriter writer = null;
        try {
            if (overwriteFile) {
                writer = new IndentAwareFileWriter(dest, 4);
            }
            if (!dest.exists()) {
                if (!dest.mkdirs()) {
                    throw new IOException("Could not make directories for " + dest.getAbsolutePath());
                }
            }
            if (!dest.isFile()) {
                throw new IllegalArgumentException("The destination must be a file");
            }
            _generate(dest, writer);
        } catch (IOException e) {
            JrawUtils.logger().error("Could not write the destination file: " + dest.getAbsolutePath(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    JrawUtils.logger().error("Could not close the IndentAwareFileWriter", e);
                }
            }
        }
    }

    /**
     * Looks through a list of Endpoint objects and puts them in a map in which the key is the OAuth2 scope and the
     * value is a list of Endpoints that have that scope
     *
     * @param endpoints A list of endpoints
     * @return A map of endpoints
     */
    protected NavigableMap<String, List<Endpoint>> sortEndpoints(List<Endpoint> endpoints) {
        TreeMap<String, List<Endpoint>> sorted = new TreeMap<>();

        for (Endpoint e : endpoints) {
            if (!sorted.containsKey(e.getScope())) {
                sorted.put(e.getScope(), new ArrayList<Endpoint>());
            }

            sorted.get(e.getScope()).add(e);
        }

        return sorted;
    }

    /**
     * Gets a link to Reddit's official API documentation for a specific endpoint
     *
     * @param endpoint The endpoint to look up
     * @return A URL pointing to the given endpoint
     */
    protected String getRedditDocUrl(Endpoint endpoint) {
        String ref = JrawUtils.urlEncode(endpoint.getVerb() + endpoint.getUri().replace('/', '_'));

        String base = "https://www.reddit.com/dev/api";
        if (!endpoint.getScope().equals("(not available through oauth)")) {
            base += "/oauth";
        }
        base += "#";

        return base + ref;
    }

    /**
     * Gets a URL linking to a given method's line number on GitHub.
     *
     * @param m The method to use
     * @return A URL
     */
    protected String getSourceUrl(Method m) {
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

    protected String getJavadocUrl(Endpoint endpoint) {
        StringBuilder base = new StringBuilder("https://thatjavanerd.github.io/JRAW/docs/git/latest");

        Method m = endpoint.getMethod();
        // "/net/dean/jraw/ClassName.html#"
        base.append("/")
                .append(m.getDeclaringClass().getName().replace('.', '/'))
                .append(".html")
                .append('#');
        StringBuilder ref = new StringBuilder();
        // "myMethod"
        ref.append(m.getName());

        // Begin parameter types
        ref.append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        int counter = 0;
        for (Class<?> parameterType : parameterTypes) {
            ref.append(parameterType.getName());
            if (counter != parameterTypes.length - 1) {
                // Parameters are separated with a hyphen
                ref.append(", ");
            }

            counter++;
        }

        // End parameter types
        ref.append(')');
        return base + ref.toString();
    }

    protected int getImplementedEndpointsCount() {
        int counter = 0;
        for (Endpoint e : endpoints) {
            if (e.isImplemented()) {
                counter++;
            }
        }

        return counter;
    }

    protected int getTotalEndpoints() {
        return endpoints.size();
    }

    /**
     * Called by {@link #generate(java.io.File)}. This method does the actual generation of files.
     * @param dest The file to write to
     * @param writer An {@link IndentAwareFileWriter} that writes to dest. Will be null if the value of overwriteFile
     *               in the passed to the constructor was true.
     * @throws IOException If there was a problem writing to the file
     */
    protected abstract void _generate(File dest, IndentAwareFileWriter writer) throws IOException;
}
