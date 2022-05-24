package codecrafters.redis;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RespBulkStringTest {
    @Test
    void testEncodeThenDecode() throws IOException, InputMismatchException {
        RespBulkString original = new RespBulkString("Hello");

        InputStream encoded = new ByteArrayInputStream(original.encode().getBytes());
        RespBulkString reconstructed = RespBulkString.decode(encoded);

        assertEquals(original, reconstructed);
    }

    @Test
    void testDecodeThenEncode() throws IOException, InputMismatchException {
        String original = "$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        RespBulkString decoded = RespBulkString.decode(inputStream);
        String encoded = Objects.requireNonNull(decoded).encode();

        assertEquals(original, encoded);
    }
}
