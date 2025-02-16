package tringaa;

public class UnknownCommandException extends TringaException {
    public UnknownCommandException(String message) {
        super("Unknown Command: " + message);
    }
}

