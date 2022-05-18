import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Resp {
    private static final Charset charset = StandardCharsets.UTF_8;

    public static byte[] encode(String str) {
        return ("+" + str + "\r\n").getBytes(charset);
    }
}
