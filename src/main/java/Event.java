// Task: Event
public class Event extends Task {
    protected String description;
    protected boolean isDone;
    private String start;
    private String end;

    public Event(String description, String end, String start) {
        super(description);
        this.end = end;
        this.start = start;
        this.isDone = false;
    }

    @Override
    public String toString() { // added [E]
        return "[E]" + super.toString() + "(from:" + start + "to:" + end + ")";
    }
}
