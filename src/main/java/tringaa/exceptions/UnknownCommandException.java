package tringaa.exceptions;

public class UnknownCommandException extends TringaException {
    public UnknownCommandException(String message) {
        super("Unknown Command: " + message);
    }
}

