public abstract class Task {
    protected String description;
    protected boolean isDone;

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

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

}