package tringaa.exceptions;

/**
 * Custom exception for invalid command errors.
 */
public class InvalidCommandException extends TringaException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
