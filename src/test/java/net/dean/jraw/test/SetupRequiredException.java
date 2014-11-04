package net.dean.jraw.test;

public class SetupRequiredException extends RuntimeException {
    public SetupRequiredException(String message) {
        super(message);
    }

    public SetupRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
