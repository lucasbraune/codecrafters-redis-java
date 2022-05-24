package codecrafters.redis;

import java.io.IOException;
import java.io.InputStream;

public class RespBulkString {
    private final String value;

    public RespBulkString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String encode() {
        return "$" + value.length() + "\r\n" + value + "\r\n";
    }

    /**
     * See https://redis.io/docs/reference/protocol-spec/#resp-bulk-strings
     */
    public static RespBulkString decode(InputStream input)
            throws InputMismatchException, IOException {
        int byteOfInput = input.read();
        if (byteOfInput == -1) {
            return null;
        }
        assertDollar(byteOfInput);

        int length = 0;
        for (byteOfInput = input.read(); byteOfInput != '\r'; byteOfInput = input.read()) {
            assertDigit(byteOfInput);
            length = 10 * length + (byteOfInput - '0');
        }

        assertLineFeed(input.read());

        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            byteOfInput = input.read();
            assertPositive(byteOfInput);
            data[i] = (byte) byteOfInput;
        }

        return new RespBulkString(new String(data));
    }

    private static void assertDollar(int d) throws InputMismatchException {
        if (d != '$') {
            throw new InputMismatchException();
        }
    }

    private static void assertDigit(int d) throws InputMismatchException {
        if (d < '0' || d > '9') {
            throw new InputMismatchException();
        }
    }

    private static void assertLineFeed(int d) throws InputMismatchException {
        if (d != '\n') {
            throw new InputMismatchException();
        }
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

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}