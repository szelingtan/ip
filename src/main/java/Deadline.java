// Task: Deadline
public class Deadline extends Task {
    protected String description;
    protected boolean isDone;
    private final String deadline;

    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = Parser.parseDeadline(deadline);
        this.isDone = false;
    }

    public String getDeadline() {
        return this.deadline;
    }

    @Override
    public String toString() { //added [D]
        return "[D]" + super.toString() + "(by:" + deadline + ")";
    }
}