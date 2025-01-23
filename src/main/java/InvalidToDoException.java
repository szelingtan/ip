public class InvalidToDoException extends TringaException {
    public InvalidToDoException(String message) {
        super(message + "Recall that ToDo tasks " +
                "needs a task description!");
    }
}
