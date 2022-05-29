package codecrafters.redis.protocol;

import codecrafters.redis.InputMismatchException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static codecrafters.redis.protocol.BasicAssertions.assertEquals;

public class BulkString implements RedisSerializable {
    private final String content;

    public static final BulkString NULL = new BulkString(null);

    private BulkString(String content) {
        this.content = content;
    }

    public static BulkString of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        return new BulkString(value);
    }

    public Optional<String> getContent() {
        return content == null ? Optional.empty() : Optional.of(content);
    }

    public String serialize() {
        return content != null ?
                "$" + content.length() + "\r\n" + content + "\r\n" :
                "$-1\r\n";
    }

    /**
     * See https://redis.io/docs/reference/protocol-spec/#resp-bulk-strings
     */
    public static BulkString readFrom(InputStream input)
            throws InputMismatchException, IOException {
        int byteOfInput = input.read();
        if (byteOfInput == -1) {
            return null;
        }
        assertEquals('$', byteOfInput);

        ByteArrayOutputStream lengthBytes = new ByteArrayOutputStream();
        for (byteOfInput = input.read(); byteOfInput != '\r'; byteOfInput = input.read()) {
            lengthBytes.write(byteOfInput);
        }
        assertEquals('\n', input.read());

        int length;
        try {
            length = Integer.parseInt(lengthBytes.toString());
        } catch (NumberFormatException e) {
            throw new InputMismatchException("Unable to parse length");
        }

        if (length < 0) {
            return BulkString.NULL;
        }

        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            byteOfInput = input.read();
            assertPositive(byteOfInput);
            data[i] = (byte) byteOfInput;
        }
        assertEquals('\r', input.read());
        assertEquals('\n', input.read());

        return BulkString.of(new String(data));
    }

    private static void assertPositive(int d) throws InputMismatchException {
        if (d < 0) {
            throw new InputMismatchException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BulkString that = (BulkString) o;

        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}