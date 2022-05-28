package codecrafters.redis;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapCacheService implements CacheService {

    private static class CacheItem {
        private final String value;
        private final Instant expiresAt;

        private CacheItem(String value, Instant expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        private CacheItem(String value) {
            this(value, null);
        }

        public String getValue() {
            return value;
        }

        public boolean hasExpired() {
            return expiresAt != null && Instant.now().isAfter(expiresAt);
        }
    }

    private final ConcurrentHashMap<String, CacheItem> map = new ConcurrentHashMap<>();

    @Override
    public String ping() {
        return PONG;
    }

    @Override
    public String echo(String message) {
        return message;
    }

    @Override
    public String get(String key) {
        CacheItem item = map.get(key);
        return item.hasExpired() ? null : item.getValue();
    }

    @Override
    public String set(String key, String value) {
        map.put(key, new CacheItem(value));
        return OK;
    }

    @Override
    public String set(String key, String value, long px) {
        Instant expiresAt = Instant.now().plus(Duration.ofMillis(px));
        map.put(key, new CacheItem(value, expiresAt));
        return OK;
    }
}
