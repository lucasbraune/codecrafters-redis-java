package codecrafters.redis.protocol;

import codecrafters.redis.InputMismatchException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Deserialization {
    public static BulkString readBulkString(InputStream input)
            throws InputMismatchException, IOException {
        int firstByte = input.read();
        assertEquals('$', firstByte);
        return readBulkStringAfterFirstByte(input);
    }

    private static BulkString readBulkStringAfterFirstByte(InputStream input)
            throws InputMismatchException, IOException {
        int length = readIntCrlf(input);
        if (length < 0) {
            return BulkString.NULL;
        }
        byte[] bytes = readBytes(length, input);
        assertEquals('\r', input.read());
        assertEquals('\n', input.read());
        return BulkString.of(new String(bytes));
    }

    public static BulkStringArray readBulkStringArray(InputStream input)
            throws InputMismatchException, IOException {
        int firstByte = input.read();
        assertEquals('*', firstByte);
        return readBulkStringArrayAfterFirstByte(input);
    }

    public static Optional<BulkStringArray> readBulkStringArrayOrEof(InputStream input)
            throws InputMismatchException, IOException {
        int firstByte = input.read();
        switch (firstByte) {
            case '*':
                return Optional.of(readBulkStringArrayAfterFirstByte(input));
            case -1:
                return Optional.empty();
            default:
                throw new InputMismatchException("First byte: " + (char) firstByte);
        }
    }

    private static BulkStringArray readBulkStringArrayAfterFirstByte(InputStream input)
            throws InputMismatchException, IOException {
        int length = readIntCrlf(input);
        if (length < 0) {
            throw new InputMismatchException("Null array");
        }
        List<BulkString> bulkStrings = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            bulkStrings.add(readBulkString(input));
        }
        return new BulkStringArray(bulkStrings);
    }

    private static int readIntCrlf(InputStream input) throws IOException, InputMismatchException {
        ByteArrayOutputStream lengthBytes = new ByteArrayOutputStream();
        for (int byteOfInput = input.read(); byteOfInput != '\r'; byteOfInput = input.read()) {
            lengthBytes.write(byteOfInput);
        }
        assertEquals('\n', input.read());
        try {
            return Integer.parseInt(lengthBytes.toString());
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    private static byte[] readBytes(int length, InputStream input)
            throws IOException, InputMismatchException {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int byteOfInput = input.read();
            assertPositive(byteOfInput);
            bytes[i] = (byte) byteOfInput;
        }
        return bytes;
    }

    private static void assertPositive(int d) throws InputMismatchException {
        if (d < 0) {
            throw new InputMismatchException();
        }
    }

    private static void assertEquals(int expected, int actual) throws InputMismatchException {
        if (expected != actual) {
            throw new InputMismatchException("Expected: " + (char) expected + "; actual: " + (char) actual);
        }
    }
}
