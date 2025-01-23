public class InvalidCommandException extends TringaException {
    public InvalidCommandException(String message) {
        super(message + " is not a valid command. Refer to our documentation for " +
                "valid commands, thank you!");
    }
}
