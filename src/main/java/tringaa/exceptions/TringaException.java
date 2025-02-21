package tringaa.exceptions;

/**
 * Custom exception for Tringa Bot errors.
 */
public class TringaException extends Exception {
    public TringaException(String message) {
        super("TringaBot Error: " + message);
    }

}
