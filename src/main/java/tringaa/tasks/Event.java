package tringaa.tasks;

/**
 * Represents an Event task with a start and end time.
 * This class extends the base Task class to include temporal information.
 */
public class Event extends Task {
    protected boolean isDone;
    private final String start;
    private final String end;

    /**
     * Constructs a new Event with the specified description and time frame.
     *
     * @param description The description of the event
     * @param start The starting time/date of the event
     * @param end The ending time/date of the event
     */
    public Event(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
        this.isDone = false;
    }

    /**
     * Gets the start time/date of the event.
     *
     * @return The start time/date as a String
     */
    public String getStart() {
        return this.start;
    }

    /**
     * Gets the end time/date of the event.
     *
     * @return The end time/date as a String
     */
    public String getEnd() {
        return this.end;
    }

    /**
     * Returns a string representation of the Event.
     * Prefixes the output with [E] to indicate this is an Event type task.
     *
     * @return A formatted string containing the event details
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }
}
