package tringaa.exceptions;

/**
 * Custom exception for unknown command errors.
 */
public class UnknownCommandException extends TringaException {
    public UnknownCommandException(String message) {
        super("Unknown Command: " + message);
    }
}

