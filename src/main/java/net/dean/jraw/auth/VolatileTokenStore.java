package net.dean.jraw.auth;

import java.util.HashMap;
import java.util.Map;

/** Simple TokenStore that uses a Map. Does not persist data. */
public class VolatileTokenStore implements TokenStore {
    private final Map<String, String> map = new HashMap<>();

    @Override
    public boolean isStored(String key) {
        return map.containsKey(key);
    }

    @Override
    public String readToken(String key) throws NoSuchTokenException {
        return map.get(key);
    }

    @Override
    public void writeToken(String key, String token) {
        map.put(key, token);
    }
}
