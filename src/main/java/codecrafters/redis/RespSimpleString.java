package codecrafters.redis;

public class RespSimpleString {
    private final String value;

    public RespSimpleString(String value) {
        this.value = value;
    }

    public String encode() {
        return "+" + value + "\r\n";
    }
}