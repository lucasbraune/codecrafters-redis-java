package codecrafters.redis.protocol;

import codecrafters.redis.InputMismatchException;

public class BasicAssertions {
    public static void assertEquals(int expected, int actual) throws InputMismatchException {
        if (expected != actual) {
            throw new InputMismatchException("Expected: " + (char) expected + "; actual: " + (char) actual);
        }
    }

    public static void assertDigit(int actual) throws InputMismatchException {
        if (actual < '0' || actual > '9') {
            throw new InputMismatchException("Expected digit; actual: " + (char) actual);
        }
    }
}
