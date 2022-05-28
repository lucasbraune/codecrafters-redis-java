package codecrafters.redis.protocol;

public class RespError implements RespData {
    private final String value;

    public RespError(String value) {
        this.value = value;
    }

    public String toRawString() {
        return "-" + value + "\r\n";
    }
}