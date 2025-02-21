package tringaa.tasks;

/**
 * Represents a To-Do task in the Tringa task management system.
 * This class extends the basic Task class and adds functionality specific to To-Do items.
 */
public class ToDo extends Task {
    protected boolean isDone;

    /**
     * Creates a new To-Do task with the specified description.
     * The task is initially marked as not done.
     *
     * @param description The description of the To-Do task
     */
    public ToDo(String description) {
        super(description);
        this.isDone = false;
    }

    /**
     * Returns a string representation of the To-Do task.
     * The string begins with [T] to indicate it's a To-Do task.
     *
     * @return A formatted string representing the To-Do task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
