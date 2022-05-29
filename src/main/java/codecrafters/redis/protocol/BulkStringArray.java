package codecrafters.redis.protocol;

import codecrafters.redis.InputMismatchException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static codecrafters.redis.protocol.BasicAssertions.assertDigit;
import static codecrafters.redis.protocol.BasicAssertions.assertEquals;

public class BulkStringArray implements RedisSerializable {
    final private List<BulkString> bulkStrings;

    public BulkStringArray(List<BulkString> elements) {
        this.bulkStrings = new ArrayList<>(elements);
    }

    public BulkStringArray(BulkString... elements) {
        this(Arrays.asList(elements));
    }

    public List<BulkString> asList() {
        return Collections.unmodifiableList(bulkStrings);
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(bulkStrings.size()).append("\r\n");
        for (BulkString bulkString : bulkStrings) {
            sb.append(bulkString.serialize());
        }
        return sb.toString();
    }

    public static BulkStringArray readFrom(InputStream input) throws InputMismatchException, IOException {
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
            BulkString element = BulkString.readFrom(input);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BulkStringArray bulkStringArray = (BulkStringArray) o;

        return bulkStrings.equals(bulkStringArray.bulkStrings);
    }

    @Override
    public int hashCode() {
        return bulkStrings.hashCode();
    }
}
