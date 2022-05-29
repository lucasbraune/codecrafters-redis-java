package codecrafters.redis;

import java.util.Optional;

public interface CacheService {
    Optional<String> get(String key);

    void set(String key, String value);

    void set(String key, String value, long px);
}
