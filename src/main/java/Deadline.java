import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a deadline task in the task list.
 * A deadline task is a task that needs to be done by a specific date/time.
 * It extends the basic Task class and adds deadline functionality.
 */
public class Deadline extends Task {
    protected boolean isDone;
    private final LocalDateTime deadlineDateTime;
    private final LocalDate deadlineDate;

    /**
     * Creates a new Deadline task with the given description and deadline.
     * The deadline can be specified either as a date only or as a date with time.
     * Accepted formats are parsed through the Parser.parseDeadline method.
     *
     * @param description The task description
     * @param deadline The deadline string to parse
     * @throws IllegalArgumentException if the deadline format is invalid
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.isDone = false;

        String parsedDeadline = Parser.parseDeadline(deadline);
        try {
            if (parsedDeadline.contains(":")) {
                this.deadlineDateTime = LocalDateTime.parse(parsedDeadline,
                        DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm"));
                this.deadlineDate = deadlineDateTime.toLocalDate();
            } else {
                this.deadlineDate = LocalDate.parse(parsedDeadline,
                        DateTimeFormatter.ofPattern("MMM dd yyyy"));
                this.deadlineDateTime = null;
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error parsing deadline: " + e.getMessage());
        }
    }

    /**
     * Gets the string representation of the deadline.
     * If the deadline includes a time component, returns both date and time.
     * Otherwise, returns only the date.
     *
     * @return The formatted deadline string in either "MMM dd yyyy" or "MMM dd yyyy, HH:mm" format
     */
    public String getDeadline() {
        if (deadlineDateTime != null) {
            return deadlineDateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm"));
        }
        return deadlineDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }

    /**
     * Gets the date component of the deadline.
     * If the deadline includes a time component, returns the date part.
     * If the deadline is date-only, returns that date.
     *
     * @return The deadline date
     */
    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    /**
     * Gets the full date-time of the deadline, if specified.
     * Returns null if the deadline was specified as a date only.
     *
     * @return The deadline date and time, or null if no time was specified
     */
    public LocalDateTime getDeadlineDateTime() {
        return deadlineDateTime;
    }

    /**
     * Returns a string representation of the deadline task.
     * Format: [D][X] description (by: deadline)
     * The X mark appears if the task is done.
     *
     * @return The formatted string representation of the deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + "(by:" + getDeadline() + ")";
    }
}