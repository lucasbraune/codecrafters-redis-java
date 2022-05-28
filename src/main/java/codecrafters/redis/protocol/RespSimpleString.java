package codecrafters.redis.protocol;

public class RespSimpleString implements RespData {
    private final String value;

    public RespSimpleString(String value) {
        this.value = value;
    }

    public String toRawString() {
        return "+" + value + "\r\n";
    }
}