package codecrafters.redis;

import codecrafters.redis.protocol.BulkStringArray;
import codecrafters.redis.protocol.BulkString;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static codecrafters.redis.protocol.Deserialisation.readBulkStringArray;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BulkStringArrayTest {
    @Test
    void testEncodeThenDecode() throws IOException, InputMismatchException {
        BulkStringArray original = new BulkStringArray(BulkString.of("Hello"), BulkString.of("World"));

        InputStream encoded = new ByteArrayInputStream(original.serialize().getBytes());
        BulkStringArray reconstructed = readBulkStringArray(encoded);

        assertEquals(original, reconstructed);
    }

    @Test
    void testDecodeThenEncode() throws IOException, InputMismatchException {
        String original = "*1\r\n$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        BulkStringArray decoded = readBulkStringArray(inputStream);
        String encoded = Objects.requireNonNull(decoded).serialize();

        assertEquals(original, encoded);
    }

    @Test
    void testDecodeReadsEntireEncodedBulkString() throws IOException, InputMismatchException {
        String original = "*1\r\n$4\r\nping\r\n";
        InputStream inputStream = new ByteArrayInputStream(original.getBytes());

        readBulkStringArray(inputStream);

        assertEquals(-1, inputStream.read());
    }
}
