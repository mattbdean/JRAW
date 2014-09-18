package net.dean.jraw.models;

import net.dean.jraw.JrawConfig;
import net.dean.jraw.JrawUtils;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides an abstract model for retrieving data from a JSON node, although not necessarily relating to the
 * Reddit API.
 */
public abstract class JsonModel {
    protected final JsonNode data;
    /** The maximum length of a result of a @JsonInteraction method in {@link #toString()} */
    private static final int MAX_STRING_LENGTH = 500;
    private static final String ELLIPSIS = "(...)";

    /**
     * Instantiates a new JsonModel
     *
     * @param dataNode The node to parse data from
     */
    public JsonModel(JsonNode dataNode) {
        this.data = dataNode;
    }

    /**
     * Retrieves a String value from the JSON node.
     *
     * @param name The key to look up in the JSON node.
     * @return An String in the JSON node
     */
    public String data(String name) {
        return data(name, String.class);
    }

    /**
     * Retrieves a value from the JSON node (provided in the constructor) of type T. When the class is Boolean, Double,
     * Integer, Long, Float, or String, then it returns one of those objects using {@code JsonNode.asX()}. If class is URI, or
     * URI, a new URL or URI is returned. If class is (java.util.)Date, then the JsonNode's value is assumed to be long.
     * The value is multiplied by 1000 (since the value is assumed to be in seconds) and then passed to {@link Date#Date(long)}.
     * Finally, if the class is RenderStringPair, then one will be returned based on the value of
     * {@link net.dean.jraw.JrawConfig#loadRenderStringPairHtml}
     *
     * @param name The key to look up in the JSON node.
     * @param type The wanted return value. Supported values are any class representing a primitive data type, such as
     *             {@link Integer} or {@link Boolean}.
     * @param <T> The desired return data type
     * @return An object of type T in the JSON node
     */
    @SuppressWarnings("unchecked")
    public <T> T data(String name, Class<T> type) {
        // Make sure the key is actually there
        if (!data.has(name)) {
            return null;
        }

        JsonNode node = data.get(name);

        if (node.isNull()) {
            return null;
        }

        T returnVal;

        // Try to return the desired value
        if (type.equals(Boolean.class))
            returnVal = (T) Boolean.valueOf(node.asBoolean());
        else if (type.equals(Double.class))
            returnVal = (T) Double.valueOf(node.asDouble());
        else if (type.equals(Integer.class))
            returnVal = (T) Integer.valueOf(node.asInt());
        else if (type.equals(Long.class))
            returnVal = (T) Long.valueOf(node.asLong());
        else if (type.equals(Float.class))
            returnVal = (T) Float.valueOf(node.asText());
        else if (type.equals(URL.class) || type.equals(URI.class)) {
            String href = node.asText();
            if (href != null) {
                if (type.equals(URL.class)) {
                    returnVal = (T) JrawUtils.newUrl(href);
                } else {
                    returnVal = (T) JrawUtils.newUri(href);
                }
            } else {
                returnVal = null;
            }
        } else if (type.equals(Date.class)) {
            long seconds = node.asLong();
            returnVal = (T) new Date(seconds * 1000);
        } else if (type.equals(RenderStringPair.class)) {
            String md = data(name);
            if (!JrawConfig.loadRenderStringPairHtml) {
                return (T) new RenderStringPair(md);
            }

            return (T) new RenderStringPair(md, data(name + "_html"));
        } else
            // Assume String
            returnVal = (T) String.valueOf(node.asText());

        return returnVal;
    }

    /**
     * This method gets the "data" JsonNode. In a normal request (let's say to <a href="http://www.reddit.com/r/pics/about.json">/r/pics' "about" API link</a>),
     * the Reddit API returns some JSON data. An example would look like this:
     * <pre>
     * {@code
     * {
     *     "kind": "t5",
     *     "data": {
     *         "submit_text_html": null,
     *         "user_is_banned": false,
     *         "id": "2qh0u",
     *         ...
     *     }
     * }
     * }
     * </pre>
     *
     * The "data" node contains all the properties essential to this model.
     *
     * @return The JsonNode to get data from
     */
    public JsonNode getDataNode() {
        return data;
    }

    /**
     * Convenience method to be used in toString() methods that returns the String literal "null" if the value is null.
     * If the object's toString() method throws a NullPointerException, then the String literal "(NullPointerException)
     * is returned. If no exceptions were thrown, then this method returns {@code val.toString()}.
     *
     * @param val The object to evaluate
     * @return A string representation of the object
     */
    protected String asString(Object val) {
        if (val == null) {
            return "null";
        }

        try {
            return val.toString();
        } catch (NullPointerException e) {
            return "(NullPointerException)";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonModel that = (JsonModel) o;

        return (data != null ? !data.equals(that.data) : that.data != null);
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    @Override
    public String toString() {
        // Since JsonModel subclasses don't have many meaningful fields (except for data), a dynamic toString() is
        // more suited for better representing the JsonModel

        Class<? extends JsonModel> clazz = getClass();
        StringBuilder sb = new StringBuilder(clazz.getSimpleName() + " {");

        List<Method> jsonInteractionMethods = getJsonInteractionMethods(clazz);

        // Sort the methods by name
        Collections.sort(jsonInteractionMethods, (o1, o2) -> o1.getName().compareTo(o2.getName()));

        int counter = 0;
        for (Method m : jsonInteractionMethods) {
            try {
                // methodName()="returnVal"
                sb.append(m.getName()).append("()=");
                InvocationTargetException thrown = null;
                Object result = null;
                try {
                    result = m.invoke(this);
                } catch (InvocationTargetException e) {
                    thrown = e;
                }

                if (thrown == null) {
                    // No InvocationTargetException thrown
                    if (result instanceof JsonModel) {
                        // Avoid calling asString on JsonModels
                        sb.append('[').append(result.getClass().getSimpleName()).append(']');
                    } else {
                        String resultString = asString(result);
                        // Remove new lines
                        resultString = resultString.replace("\n", "\\n");
                        if (resultString.length() > MAX_STRING_LENGTH) {
                            // Prevent the resultString from being too long, cut it off at a certain length and add an ellipsis
                            resultString = resultString.substring(0, MAX_STRING_LENGTH - ELLIPSIS.length());
                            resultString += ELLIPSIS;
                        }
                        sb.append('\"').append(resultString).append('\"');
                    }
                } else {
                    // Show the exception and its cause
                    Throwable cause = thrown.getCause();
                    sb.append('[').append(cause.getClass().getName()).append(": ").append(cause.getMessage()).append(']');
                }

                if (counter != jsonInteractionMethods.size() - 1) {
                    // Append the delimiter only if there will be a next element
                    sb.append(", ");
                }
                counter++;
            } catch (IllegalAccessException e) {
                JrawUtils.logger().error("IllegalAccessException. This really shouldn't happen.", e);
            }
        }
        sb.append('}');

        return sb.toString();
    }

    /**
     * Gets a list of fields that have the JsonInteraction annotation attached to them. This method also returns
     * JsonInteraction-annotated methods in this class' superclasses, up until JsonModel. Mainly used for testing.
     *
     * @param thingClass The class to search in
     * @return A list of fields that have the JsonInteraction annotation
     */
    public static List<Method> getJsonInteractionMethods(Class<? extends JsonModel> thingClass) {
        List<Method> methods = new ArrayList<>();

        Class clazz = thingClass;
        List<Method> toObserve = new ArrayList<>();

        while (clazz != null) {
            toObserve.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            for (Class<?> interf : clazz.getInterfaces()) {
                toObserve.addAll(Arrays.asList(interf.getDeclaredMethods()));
            }

            if (clazz.equals(JsonModel.class)) {
                // Already at the highest level and we don't need to scan Object
                break;
            }

            // Can still go deeper...
            clazz = clazz.getSuperclass();
        }

        // Filter out the methods that don't have the JsonInteraction annotation
        methods.addAll(toObserve.stream().filter(m -> m.isAnnotationPresent(JsonInteraction.class)).collect(Collectors.toList()));

        return methods;
    }
}
