package codecrafters.redis;

public interface CacheService {
    String OK = "OK";
    String PONG = "PONG";

    String ping();

    String echo(String message);

    String get(String key);

    String set(String key, String value);

    String set(String key, String value, long px);
}
