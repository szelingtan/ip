package tringaa.exceptions;

/**
 * Custom exception for task storage errors.
 */
public class TaskStorageException extends Exception {
    public TaskStorageException(String message) {
        super("TaskStorage Error: " + message);
    }

}
