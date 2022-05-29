package codecrafters.redis.protocol;

import codecrafters.redis.InputMismatchException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Deserialisation {
    public static BulkString readBulkString(InputStream input)
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

    public static BulkStringArray readBulkStringArray(InputStream input)
            throws InputMismatchException, IOException {
        int byteOfInput = input.read();
        if (byteOfInput == -1) {
            return null;
        }
        assertEquals('*', byteOfInput);

        int length = 0;
        for (byteOfInput = input.read(); byteOfInput != '\r'; byteOfInput = input.read()) {
            assertDigit(byteOfInput);
            length = 10 * length + (byteOfInput - '0');
        }

        assertEquals('\n', input.read());

        List<BulkString> elements = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            BulkString element = readBulkString(input);
            assertNotNull(element);
            elements.add(element);
        }

        return new BulkStringArray(elements);
    }

    private static void assertNotNull(Object obj) throws InputMismatchException {
        if (obj == null) {
            throw new InputMismatchException("Unexpected EOF");
        }
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

    private static void assertDigit(int actual) throws InputMismatchException {
        if (actual < '0' || actual > '9') {
            throw new InputMismatchException("Expected digit; actual: " + (char) actual);
        }
    }
}
