package tringaa;

/**
 * Custom exception for task storage errors.
 */
class TaskStorageException extends Exception {
    public TaskStorageException(String message) {
        super("TaskStorage Error: " + message);
    }

}
