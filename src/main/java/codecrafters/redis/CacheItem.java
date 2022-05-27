package codecrafters.redis;

import java.time.Duration;
import java.time.Instant;

public class CacheItem {
    private final RespBulkString value;
    private final Instant expiresAt;

    public CacheItem(RespBulkString value) {
        this.value = value;
        expiresAt = null;
    }

    public CacheItem(RespBulkString value, int unusedExpiration) {
        this.value = value;
        expiresAt = Instant.now().plus(Duration.ofMillis(unusedExpiration));
    }

    public RespBulkString getValue() {
        return value;
    }

    public boolean isValid() {
        return expiresAt == null || Instant.now().isBefore(expiresAt);
    }
}
