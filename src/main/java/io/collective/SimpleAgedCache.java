package io.collective;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

public class SimpleAgedCache {
    private final Clock clock;
    private final Map<Object, CacheEntry> cacheMap;

    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
        this.cacheMap = new HashMap<>();
    }

    public SimpleAgedCache() {
        this(Clock.system(Clock.systemDefaultZone().getZone()));
    }

    public void put(Object key, Object value, int retentionInMillis) {
        if (key != null && retentionInMillis > 0){
            long expirationTime = clock.millis() + retentionInMillis;
            cacheMap.put(key, new CacheEntry(value, expirationTime));
        }
    }

    public boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    public int size() {
        cleanExpiredRecords();
        return cacheMap.size();
    }

    public Object get(Object key) {
        cleanExpiredRecords();
        CacheEntry entry = cacheMap.get(key);
        if (entry != null){
            return entry.value;
        }
        return null;
    }

    private void cleanExpiredRecords(){
        long currentTime = clock.millis();
        cacheMap.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
    }

    private static class CacheEntry{
        Object value;
        long expirationTime;

        CacheEntry(Object value, long expirationTime){
            this.value = value;
            this.expirationTime = expirationTime;
        }

        boolean isExpired(long currentTime){
            return currentTime >= expirationTime;
        }

        boolean isNotExpired(long currentTime){
            return !isExpired(currentTime);
        }
    }
}