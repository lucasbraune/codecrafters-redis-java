package codecrafters.redis.protocol;

/**
 * See https://redis.io/docs/reference/protocol-spec/
 */
public interface RedisSerializable {
    String serialize();
}
