// Task: ToDos
public class ToDos extends Task {
    protected String description;
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
