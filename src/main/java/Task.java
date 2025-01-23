public class Task {
    protected String description;
    protected boolean isDone;
    // every task has a unique count number
    private static int count = 0;
    private int tag;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        count++;
        this.tag = count;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markDone() {
        this.isDone = true;
    }

    @Override
    public String toString() {
        return tag + ".[" + getStatusIcon() + "] " + description;
    }

    public String taskOnly() {
        return "[" + getStatusIcon() + "] " + description;
    }
}