// Task: Event
public class Event extends Task {
    protected boolean isDone;
    private final String start;
    private final String end;

    public Event(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
        this.isDone = false;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }

    @Override
    public String toString() { // added [E]
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }
}
