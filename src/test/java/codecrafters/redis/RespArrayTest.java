package codecrafters.redis;

import codecrafters.redis.protocol.RespArray;
import codecrafters.redis.protocol.RespBulkString;
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

        InputStream encoded = new ByteArrayInputStream(original.toRawString().getBytes());
        RespArray reconstructed = RespArray.readFrom(encoded);

        assertEquals(original, reconstructed);
    }

    @Test
    void testDecodeThenEncode() throws IOException, InputMismatchException {
        String original = "*1\r\n$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        RespArray decoded = RespArray.readFrom(inputStream);
        String encoded = Objects.requireNonNull(decoded).toRawString();

        assertEquals(original, encoded);
    }

    @Test
    void testDecodeReadsEntireEncodedBulkString() throws IOException, InputMismatchException {
        String original = "*1\r\n$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        RespArray.readFrom(inputStream);

        assertEquals(-1, inputStream.read());
    }
}
