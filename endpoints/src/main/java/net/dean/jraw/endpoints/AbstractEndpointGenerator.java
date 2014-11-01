package net.dean.jraw.endpoints;

import javassist.ClassPool;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.dean.jraw.Endpoint;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.Version;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * This class provides a template for a class that generates a file based off of {@link Endpoint} objects.
 */
public abstract class AbstractEndpointGenerator {
    protected final NavigableMap<String, List<Endpoint>> endpoints;
    protected final boolean overwriteFile;
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    private static final int LINE_NUM_OFFSET = -1;

    /**
     * Instantiates a new AbstractEndpointGenerator
     * @param endpoints A map of endpoints where the key is the category and the value is a list of endpoints in that category
     * @param overwriteFile If true, then this generator is not re-creating the file, but instead updating it. The
     *                      BufferedWriter in {@link #_generate(File, BufferedWriter)} will be null.
     */
    public AbstractEndpointGenerator(NavigableMap<String, List<Endpoint>> endpoints, boolean overwriteFile) {
        this.endpoints = Collections.unmodifiableNavigableMap(endpoints);
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

        BufferedWriter bw = null;
        try {
            if (overwriteFile) {
                bw = Files.newBufferedWriter(Paths.get(dest.toURI()));
            }
            if (!dest.exists()) {
                if (!dest.mkdirs()) {
                    throw new IOException("Could not make directories for " + dest.getAbsolutePath());
                }
            }
            if (!dest.isFile()) {
                throw new IllegalArgumentException("The destination must be a file");
            }
            _generate(dest, bw);
        } catch (IOException e) {
            JrawUtils.logger().error("Could not write the destination file: " + dest.getAbsolutePath(), e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    JrawUtils.logger().error("Could not close the BufferedWriter", e);
                }
            }
        }
    }

    /**
     * Gets a link to Reddit's official API documentation for a specific endpoint
     *
     * @param endpoint The endpoint to look up
     * @return A URL pointing to the given endpoint
     */
    protected String getRedditDocUrl(Endpoint endpoint) {
        String base = endpoint.getVerb() + endpoint.getUri().replace('/', '_');

        try {
            base = URLEncoder.encode(base, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            JrawUtils.logger().error("Could not URL-encode " + base, e);
            // Just leave filePath alone
        }
        return "https://www.reddit.com/dev/api#" + base;
    }

    /**
     * Gets a URL linking to a given method on GitHub.
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
        StringBuilder base = new StringBuilder("https://thatjavanerd.github.io/JRAW/docs");
        Method m = endpoint.getMethod();
        // "/0.4.0"
        base.append("/").append(Version.get().formatted());
        // "/net/dean/jraw/ClassName.html"
        base.append("/").append(m.getDeclaringClass().getName().replace('.', '/'));
        // "#myMethod"
        base.append('#').append(m.getName().replace('(', '-').replace(')', '-'));

        // Begin parameter types
        base.append('-');
        Class<?>[] parameterTypes = m.getParameterTypes();
        int counter = 0;
        for (Class<?> parameterType : parameterTypes) {
            base.append(parameterType.getName());
            if (counter != parameterTypes.length - 1) {
                // Parameters are separated with a hyphen
                base.append('-');
            }

            counter++;
        }

        // End parameter types
        base.append('-');
        return base.toString();
    }

    protected int getImplementedEndpointsCount() {
        int counter = 0;
        for (Map.Entry<String, List<Endpoint>> endpointsList : endpoints.entrySet()) {
            for (Endpoint e : endpointsList.getValue()) {
                if (e.isImplemented()) {
                    counter++;
                }
            }
        }

        return counter;
    }

    protected int getTotalEndpoints() {
        int counter = 0;

        for (Map.Entry<String, List<Endpoint>> endpointsList : endpoints.entrySet()) {
            counter += endpointsList.getValue().size();
        }

        return counter;
    }

    /**
     * Called by {@link #generate(java.io.File)}. This method does the actual generation of files.
     * @param dest The file to write to
     * @param bw A {@link BufferedWriter} that writes to dest. Will be null if the value of overwriteFile in the
     *           passed to the constructor was true.
     * @throws IOException If there was a problem writing to the file
     */
    protected abstract void _generate(File dest, BufferedWriter bw) throws IOException;
}
