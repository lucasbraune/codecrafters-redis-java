package codecrafters.redis.protocol;

public class SimpleString implements RedisSerializable {
    private final String message;

    public SimpleString(String message) {
        this.message = message;
    }

    public String serialize() {
        return "+" + message + "\r\n";
    }
}