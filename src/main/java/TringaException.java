public class TringaException extends Exception {
    public TringaException(String command) {
        super("ERROR (Please Try Again) - " + command);
    }
}
