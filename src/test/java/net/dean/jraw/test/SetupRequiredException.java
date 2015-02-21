package net.dean.jraw.test;

public class SetupRequiredException extends RuntimeException {
    public SetupRequiredException(String message) {
        super(message + " See https://github.com/thatJavaNerd/JRAW#contributing for more information.");
    }

    public SetupRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
