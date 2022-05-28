package codecrafters.redis.protocol;

import codecrafters.redis.InputMismatchException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static codecrafters.redis.BasicAssertions.assertDigit;
import static codecrafters.redis.BasicAssertions.assertEquals;

public class RespArray implements RespData {
    final private List<RespBulkString> elements;

    public RespArray(List<RespBulkString> elements) {
        this.elements = new ArrayList<>(elements);
    }

    public RespArray(RespBulkString... elements) {
        this(Arrays.asList(elements));
    }

    public List<RespBulkString> getElements() {
        return elements;
    }

    public String toRawString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(elements.size()).append("\r\n");
        for (RespBulkString element : elements) {
            sb.append(element.toRawString());
        }
        return sb.toString();
    }

    public static RespArray readFrom(InputStream input) throws InputMismatchException, IOException {
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

        List<RespBulkString> elements = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            RespBulkString element = RespBulkString.readFrom(input);
            assertNotNull(element);
            elements.add(element);
        }

        return new RespArray(elements);
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

        RespArray respArray = (RespArray) o;

        return elements.equals(respArray.elements);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }
}
