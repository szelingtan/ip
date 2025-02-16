package tringaa;

public class TringaException extends Exception {
    public TringaException(String message) {
        super("TringaBot Error: " + message);
    }

}
