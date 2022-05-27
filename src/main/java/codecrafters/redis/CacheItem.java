package codecrafters.redis;

public class CacheItem {
    private final RespBulkString value;

    public CacheItem(RespBulkString value) {
        this.value = value;
    }

    public CacheItem(RespBulkString value, int unusedExpiration) {
        this.value = value;
    }

    public RespBulkString getValue() {
        return value;
    }

    public boolean isValid() {
        return true;
    }
}
