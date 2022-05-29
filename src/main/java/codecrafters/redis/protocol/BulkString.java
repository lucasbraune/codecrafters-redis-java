package codecrafters.redis.protocol;

import java.util.Optional;

public class BulkString implements RedisSerializable {
    private final String content;

    public static final BulkString NULL = new BulkString(null);

    /**
     * Use the {@code BulkString.of} factory instead.
     */
    private BulkString(String content) {
        this.content = content;
    }

    public static BulkString of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        return new BulkString(value);
    }

    public Optional<String> getContent() {
        return content == null ? Optional.empty() : Optional.of(content);
    }

    public String serialize() {
        return content != null ?
                "$" + content.length() + "\r\n" + content + "\r\n" :
                "$-1\r\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BulkString that = (BulkString) o;

        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}