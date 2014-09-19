package net.dean.jraw.http;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides the ability to parse and compare various values of the Content-Type header. Parsing is done based
 * on <a href="http://www.w3.org/Protocols/rfc1341/4_Content-Type.html">RFC 1341</a>. Please note that {@link #equals(Object)}
 * does not compare any extra arguments, only the type and subtype.
 */
public class ContentType {
    /** Represents any content type (&#42;/&#42;) */
    public static final ContentType ANY = ContentType.parse("*/*");
    /** Represents a JavaScript Object Notation file (application/json) */
    public static final ContentType JSON = ContentType.parse("application/json");
    /** Represents a HyperText Markup Language file (text/html) */
    public static final ContentType HTML = ContentType.parse("text/html");
    /** Represents a Cascading Style Sheet file (text/css) */
    public static final ContentType CSS = ContentType.parse("text/css");

    /**
     * Attempts to parse a ContentType object based on the value one might receive as the value of a Content-Type header.
     * @param headerVal The string to parse
     * @return A new ContentType object that represents the given input string
     */
    public static ContentType parse(String headerVal) {
        String[] parts = headerVal.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid Content-Type: " + headerVal);
        }

        String type = parts[0];
        if (!parts[1].contains(";")) {
            // No arguments
            return new ContentType(type, parts[1]);
        }

        String[] args = parts[1].split(";");
        String subtype = args[0];

        Map<String, String> argsMap = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            // Start at 1 to ignore the subtype
            String arg = args[i];
            if (!arg.contains("=")) {
                throw new IllegalArgumentException("Argument \"" + arg + "\" must contain an equals sign (\"=\")");
            }
            String[] keyValue = arg.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("More than one assignment in argument \"" + arg + "\"");
            }
            argsMap.put(keyValue[0].trim(), keyValue[1].trim());
        }

        return new ContentType(type, subtype, argsMap);
    }

    private String type;
    private String subtype;
    private Map<String, String> args;

    /**
     * Instantiates a new ContentType
     * @param type The type (ex: "application", "text")
     * @param subtype The subtype (ex: "x-www-form-urlencoded", "css")
     */
    public ContentType(String type, String subtype) {
        this(type, subtype, new HashMap<>());
    }

    /**
     * Instantiates a new ContentType with additional information
     * @param type The type (ex: "application", "text")
     * @param subtype The subtype (ex: "x-www-form-urlencoded", "css")
     * @param args The additional arguments (ex: "encoding=utf-8")
     */
    public ContentType(String type, String subtype, Map<String, String> args) {
        this.type = type;
        this.subtype = subtype;
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentType that = (ContentType) o;

        return !(subtype != null ? !subtype.equals(that.subtype) : that.subtype != null) &&
                !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (subtype != null ? subtype.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContentType {" +
                "type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", args=" + args +
                '}';
    }

    /**
     * Formats this ContentType as it could appear in an HTTP header. For example, a ContentType where the type is "text",
     * the subtype is "css", and the extra arguments are {@code {encoding=utf-8, foo=bar}} would produce the output of
     * {@code text/css; encoding=utf-8; foo=bar}
     *
     * @return A string representing how this class would appear in an HTTP header
     */
    public String asHeader() {
        StringBuilder sb = new StringBuilder(getBasicForm());
        for (Map.Entry<String, String> arg : args.entrySet()) {
            sb.append(String.format("; %s=%s", arg.getKey(), arg.getValue()));
        }

        // type/subtype; key1=val1
        return sb.toString();
    }

    /**
     * Formats this ContentType like {@link #asHeader()}, but excluding the extra arguments. For example, a ContentType
     * where the type is "text", the subtype is "css", and the extra arguments are {@code {encoding=utf-8, foo=bar}}
     * would produce the output of {@code text/css}
     *
     * @return The basic form of this ContentType
     */
    public String getBasicForm() {
        return String.format("%s/%s", type, subtype);
    }
}
