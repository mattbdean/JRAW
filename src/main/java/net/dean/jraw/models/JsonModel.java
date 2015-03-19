package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.models.meta.JsonProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * This class provides an abstract model for retrieving data from a JSON node, although not necessarily relating to the
 * Reddit API.
 */
public abstract class JsonModel {
    protected final JsonNode data;
    /** The maximum length of a result of a {@link JsonProperty} method in {@link #toString()} */
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
     * Retrieves a textual value from the JSON node.
     *
     * @param key The key to look up in the JSON node.
     * @return The textual value of the value of the Object returned by looking up the given key.
     */
    public String data(String key) {
        return data(key, String.class);
    }

    /**
     * <p>Retrieves a value from the JSON node (provided in the constructor) of type T. Supported types:
     *
     * <ul>
     *     <li>Classes that autobox/unbox to primitive types (Boolean, Double, Integer, Float, Long)
     *     <li>{@code java.lang.String}
     *     <li>{@code java.net.URI}
     *     <li>{@code java.net.URL}
     *     <li>{@code java.util.Date}
     * </ul>
     *
     * @param key The key to look up in the JSON node. Must not be null.
     * @param type The wanted return value. Supported values are any class representing a primitive data type, such as
     *             {@link Integer} or {@link Boolean}. Must not be null.
     * @param <T> The desired return data type
     * @return An object of type T in the JSON node
     * @throws IllegalArgumentException If the class given was not one mentioned above
     */
    @SuppressWarnings("unchecked")
    public <T> T data(String key, Class<T> type) {
        if (key == null || type == null)
            throw new NullPointerException("Key or class type was null");
        if (data == null)
            throw new NullPointerException("Trying to retrieve data from a null JsonNode");

        // Make sure the key is actually there
        if (!data.has(key)) {
            return null;
        }

        JsonNode node = data.get(key);

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
        } else if (type.equals(String.class)) {
            returnVal = (T) String.valueOf(node.asText());
        } else {
            throw new IllegalArgumentException("Unrecognized class: " + type.getName());
        }

        return returnVal;
    }

    /**
     * <p>This method gets the "data" JsonNode. In a traditional API request (let's say to
     * <a href="http://www.reddit.com/r/pics/about.json">/r/pics/about.json</a>), the Reddit API returns some
     * JSON data. An example would look like this:
     *
     * <pre>{@code
     * {
     *     "kind": "t5",
     *     "data": {
     *         "submit_text_html": null,
     *         "user_is_banned": false,
     *         "id": "2qh0u",
     *         ...
     *     }
     * }
     * }</pre>
     *
     * <p>The "data" node contains all the properties essential to this model.
     *
     * @return The JsonNode to get data from
     */
    public JsonNode getDataNode() {
        return data;
    }

    /**
     * Convenience method to be used in toString() methods that returns the String literal "null" if the given value is
     * null. If the object's toString() method throws a NullPointerException, then the String literal
     * "(NullPointerException) is returned. If no exceptions were thrown, then this method returns
     * {@code val.toString()}.
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

        List<Method> jsonInteractionMethods = getJsonProperties(clazz);

        // Sort the methods by name
        Collections.sort(jsonInteractionMethods, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

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
                    sb.append('[')
                            .append("threw ")
                            .append(cause.getClass().getName())
                            .append(": ")
                            .append(cause.getMessage())
                            .append(']');
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
     * JsonInteraction-annotated methods in this class' superclasses, up until JsonModel. Used for testing.
     *
     * @param thingClass The class to search in
     * @return A list of fields that have the JsonInteraction annotation
     */
    public static List<Method> getJsonProperties(Class<? extends JsonModel> thingClass) {
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

        // Filter out the methods that don't have the JsonProperty annotation
        for (Method m : toObserve) {
            if (m.isAnnotationPresent(JsonProperty.class)) {
                methods.add(m);
            }
        }

        return methods;
    }
}
