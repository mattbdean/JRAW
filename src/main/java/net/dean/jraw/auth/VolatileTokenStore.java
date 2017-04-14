package net.dean.jraw.auth;

import java.util.HashMap;
import java.util.Map;

/** Simple TokenStore that uses a Map. Does not persist data. */
public class VolatileTokenStore implements TokenStore {
    private final Map<String, String> tokenMap = new HashMap<>();
    private final Map<String, Long> acquireTimeMap = new HashMap<>();

    @Override
    public boolean isStored(String key) {
        return tokenMap.containsKey(key);
    }

    @Override
    public boolean isAcquiredTimeStored(String key) {
        return acquireTimeMap.containsKey(key);
    }

    @Override
    public String readToken(String key) throws NoSuchTokenException {
        return tokenMap.get(key);
    }

    @Override
    public void writeToken(String key, String token) {
        tokenMap.put(key, token);
    }

    @Override
    public long readAcquireTimeMillis(String key) {
        return acquireTimeMap.get(key);
    }

    @Override
    public void writeAcquireTimeMillis(String key, long acquireTimeMs) {
        acquireTimeMap.put(key, acquireTimeMs);
    }
}
