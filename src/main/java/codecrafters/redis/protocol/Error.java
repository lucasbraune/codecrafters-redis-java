package codecrafters.redis.protocol;

public class Error implements RedisSerializable {
    private final String message;

    public Error(String message) {
        this.message = message;
    }

    public String serialize() {
        return "-" + message + "\r\n";
    }
}