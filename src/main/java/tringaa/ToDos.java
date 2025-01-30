package tringaa;

// Task: ToDos
public class ToDos extends Task {
    protected boolean isDone;

    public ToDos(String description) {
        super(description);
        this.isDone = false;
    }

    @Override
    public String toString() { //added [T]
        return "[T]" + super.toString();
    }
}
