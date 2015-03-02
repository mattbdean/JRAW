package net.dean.jraw;

/**
 * Thrown when an enum constant could not be located based off some input string.
 */
public class NoSuchEnumConstantException extends RuntimeException {
    public NoSuchEnumConstantException(Class<? extends Enum> enumClass, String val) {
        super("Could not find enum constant in " + enumClass.getName() + " for JSON value '" + val + "'.");
    }
}
