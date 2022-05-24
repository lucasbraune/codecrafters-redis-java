package codecrafters.redis;

public class InputMismatchException extends Exception {
    public InputMismatchException(String message) {
        super(message);
    }

    public InputMismatchException() {
        this("");
    }
}
