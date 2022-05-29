package codecrafters.redis;

public interface CacheService {
    String get(String key);

    void set(String key, String value);

    void set(String key, String value, long px);
}
