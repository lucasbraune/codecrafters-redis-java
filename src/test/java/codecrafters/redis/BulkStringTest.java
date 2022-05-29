package codecrafters.redis;

import codecrafters.redis.protocol.BulkString;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static codecrafters.redis.protocol.Deserialisation.readBulkString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BulkStringTest {
    @Test
    void testEncodeThenDecode() throws IOException, InputMismatchException {
        BulkString original = BulkString.of("Hello");

        InputStream encoded = new ByteArrayInputStream(original.serialize().getBytes());
        BulkString reconstructed = readBulkString(encoded);

        assertEquals(original, reconstructed);
    }

    @Test
    void testDecodeThenEncode() throws IOException, InputMismatchException {
        String original = "$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        BulkString decoded = readBulkString(inputStream);
        String encoded = Objects.requireNonNull(decoded).serialize();

        assertEquals(original, encoded);
    }

    @Test
    void testDecodeReadsEntireEncodedBulkString() throws IOException, InputMismatchException {
        String original = "$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        readBulkString(inputStream);

        assertEquals(-1, inputStream.read());
    }

    @Test
    void testEncodeEmptyString() {
        String expected = "$0\r\n\r\n";

        String actual = BulkString.of("").serialize();

        assertEquals(expected, actual);
    }

    @Test
    void testDecodeEmptyString() throws IOException, InputMismatchException {
        String encoded = "$0\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(encoded.getBytes());

        BulkString actual = readBulkString(inputStream);

        BulkString expected = BulkString.of("");
        assertEquals(expected, actual);
    }

    @Test
    void testEncodeNull() {
        BulkString bulkString = BulkString.NULL;

        String actual = bulkString.serialize();

        String expected = "$-1\r\n";
        assertEquals(expected, actual);
    }

    @Test
    void testDecodeNull() throws IOException, InputMismatchException {
        String encoded = "$-1\r\n";
        InputStream inputStream = new ByteArrayInputStream(encoded.getBytes());

        BulkString actual = readBulkString(inputStream);

        assertEquals(BulkString.NULL, actual);
    }
}
