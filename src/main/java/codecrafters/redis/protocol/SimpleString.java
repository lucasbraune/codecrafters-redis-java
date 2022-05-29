package codecrafters.redis.protocol;

public class SimpleString implements RedisSerializable {
    private final String content;

    public SimpleString(String content) {
        this.content = content;
    }

    public String serialize() {
        return "+" + content + "\r\n";
    }
}