package codecrafters.redis.protocol;

public class Error implements RedisSerializable {
    private final String value;

    public Error(String message) {
        this.value = message;
    }

    public String serialize() {
        return "-" + value + "\r\n";
    }
}