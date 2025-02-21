package tringaa.tasks;

/**
 * Abstract base class for all tasks in the task management system.
 * This class provides the core functionality that all tasks share,
 * such as description, completion status, and basic task operations.
 */
public abstract class Task {
    /** The description of the task */
    protected String description;

    /** The completion status of the task */
    protected boolean isDone;

    /**
     * Constructs a new Task with the given description.
     * The task is initially marked as not done.
     *
     * @param description The description of the task
     */
    public Task(String description) {
        this.isDone = false;
        this.description = description;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markDone() {
        this.isDone = true;
    }

    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Gets the raw description of the task without any formatting or status indicators.
     * This method is primarily used for storage and data manipulation purposes.
     *
     * @return The raw description text of the task
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

}
