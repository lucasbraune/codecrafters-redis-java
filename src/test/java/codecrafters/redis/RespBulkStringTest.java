package codecrafters.redis;

import codecrafters.redis.protocol.RespBulkString;
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

        InputStream encoded = new ByteArrayInputStream(original.toRawString().getBytes());
        RespBulkString reconstructed = RespBulkString.readFrom(encoded);

        assertEquals(original, reconstructed);
    }

    @Test
    void testDecodeThenEncode() throws IOException, InputMismatchException {
        String original = "$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        RespBulkString decoded = RespBulkString.readFrom(inputStream);
        String encoded = Objects.requireNonNull(decoded).toRawString();

        assertEquals(original, encoded);
    }

    @Test
    void testDecodeReadsEntireEncodedBulkString() throws IOException, InputMismatchException {
        String original = "$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        RespBulkString.readFrom(inputStream);

        assertEquals(-1, inputStream.read());
    }

    @Test
    void testEncodeEmptyString() {
        String expected = "$0\r\n\r\n";

        String actual = new RespBulkString("").toRawString();

        assertEquals(expected, actual);
    }

    @Test
    void testDecodeEmptyString() throws IOException, InputMismatchException {
        String encoded = "$0\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(encoded.getBytes());

        RespBulkString actual = RespBulkString.readFrom(inputStream);

        RespBulkString expected = new RespBulkString("");
        assertEquals(expected, actual);
    }

    @Test
    void testEncodeNull() {
        RespBulkString bulkString = new RespBulkString(null);

        String actual = bulkString.toRawString();

        String expected = "$-1\r\n";
        assertEquals(expected, actual);
    }

    @Test
    void testDecodeNull() throws IOException, InputMismatchException {
        String encoded = "$-1\r\n";
        InputStream inputStream = new ByteArrayInputStream(encoded.getBytes());

        RespBulkString actual = RespBulkString.readFrom(inputStream);

        assertEquals(new RespBulkString(null), actual);
    }
}
