// Task: Deadline
public class Deadline extends Task {
    protected String description;
    protected boolean isDone;
    private String deadline;

    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
        this.isDone = false;
    }

    @Override
    public String toString() { //added [D]
        return "[D]" + super.toString() + "(by:" + deadline + ")";
    }
}