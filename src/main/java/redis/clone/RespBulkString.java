package redis.clone;

public class RespBulkString {
    private final String value;

    public RespBulkString(String value) {
        this.value = value;
    }

    public String toString() {
        return "$" + value.length() + "\r\n" + value + "\r\n";
    }
}