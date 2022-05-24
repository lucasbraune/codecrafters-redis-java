package codecrafters.redis;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RespArrayTest {
    @Test
    void testEncodeThenDecode() throws IOException, InputMismatchException {
        RespArray original = new RespArray(new RespBulkString("Hello"), new RespBulkString("World"));

        InputStream encoded = new ByteArrayInputStream(original.encode().getBytes());
        RespArray reconstructed = RespArray.decode(encoded);

        assertEquals(original, reconstructed);
    }

    @Test
    void testDecodeThenEncode() throws IOException, InputMismatchException {
        String original = "*1\r\n$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        RespArray decoded = RespArray.decode(inputStream);
        String encoded = Objects.requireNonNull(decoded).encode();

        assertEquals(original, encoded);
    }

    @Test
    void testDecodeReadsEntireEncodedBulkString() throws IOException, InputMismatchException {
        String original = "*1\r\n$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        RespArray.decode(inputStream);

        assertEquals(-1, inputStream.read());
    }
}
