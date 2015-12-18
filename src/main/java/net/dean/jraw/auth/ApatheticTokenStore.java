package net.dean.jraw.auth;

/**
 * A TokenStore implementation that disregards all calls to {@link #writeToken(String, String)}, returns false for
 * all calls to {@link #isStored(String)}, and throws a {@link NoSuchTokenException} for all calls to
 * {@link #readToken(String)}. Useful when using a {@link RefreshTokenHandler} which does not need to store any refresh
 * tokens.
 */
public final class ApatheticTokenStore implements TokenStore {
    @Override
    public boolean isStored(String key) {
        return false;
    }

    @Override
    public String readToken(String key) throws NoSuchTokenException {
        throw new NoSuchTokenException("TokenStore is apathetic");
    }

    @Override
    public void writeToken(String key, String token) {}
}
