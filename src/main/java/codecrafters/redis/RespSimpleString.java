package codecrafters.redis;

public class RespSimpleString implements RespData {
    private final String value;

    public RespSimpleString(String value) {
        this.value = value;
    }

    public String encode() {
        return "+" + value + "\r\n";
    }
}