package net.dean.jraw.auth;

/** Thrown when trying to read a token in a {@link TokenStore} that does not exist */
public class NoSuchTokenException extends Exception {
    public NoSuchTokenException() {
        super();
    }
    public NoSuchTokenException(String msg) {
        super(msg);
    }
}
