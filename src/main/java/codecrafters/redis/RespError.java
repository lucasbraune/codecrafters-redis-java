package codecrafters.redis;

public class RespError implements RespData {
    private final String value;

    public RespError(String value) {
        this.value = value;
    }

    public String encode() {
        return "-" + value + "\r\n";
    }
}