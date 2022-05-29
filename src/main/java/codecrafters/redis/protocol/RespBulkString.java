package codecrafters.redis.protocol;

import codecrafters.redis.InputMismatchException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static codecrafters.redis.protocol.BasicAssertions.assertEquals;

public class RespBulkString implements RespData {
    private final String value;

    public static final RespBulkString NULL = new RespBulkString(null);

    private RespBulkString(String value) {
        this.value = value;
    }

    public static RespBulkString of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        return new RespBulkString(value);
    }

    public Optional<String> getValue() {
        return value == null ? Optional.empty() : Optional.of(value);
    }

    public String toRawString() {
        return value != null ?
                "$" + value.length() + "\r\n" + value + "\r\n" :
                "$-1\r\n";
    }

    /**
     * See https://redis.io/docs/reference/protocol-spec/#resp-bulk-strings
     */
    public static RespBulkString readFrom(InputStream input)
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
            return RespBulkString.NULL;
        }

        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            byteOfInput = input.read();
            assertPositive(byteOfInput);
            data[i] = (byte) byteOfInput;
        }
        assertEquals('\r', input.read());
        assertEquals('\n', input.read());

        return RespBulkString.of(new String(data));
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

        RespBulkString that = (RespBulkString) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}