package redis.clone;

public class RespSimpleString {
    private final String value;

    public RespSimpleString(String value) {
        this.value = value;
    }

    public String toString() {
        return "+" + value + "\r\n";
    }
}