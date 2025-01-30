import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses user input for chatbot operations.
 */
public class Parser {
    /** Format: command word followed by arguments. */
    public static final Pattern BASIC_COMMAND_FORMAT =
            Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /** Format for tasks that have only description (todo). */
    public static final Pattern TODO_ARGS_FORMAT =
            Pattern.compile("(?<description>.+)");

    /** Format for deadline tasks: description + /by + deadline. */
    public static final Pattern DEADLINE_ARGS_FORMAT =
            Pattern.compile("(?<description>[^/]+)/by(?<deadline>.+)");

    /** Format for event tasks: description + /from + start + /to + end. */
    public static final Pattern EVENT_ARGS_FORMAT =
            Pattern.compile("(?<description>[^/]+)/from(?<startTime>[^/]+)/to(?<endTime>.+)");

    /** Format for index-based commands (mark, delete). */
    public static final Pattern INDEX_ARGS_FORMAT =
            Pattern.compile("(?<targetIndex>\\d+)");

    /** Message for invalid datetime format. */
    private static final String DATETIME_FORMAT_MESSAGE =
            "Date formats: d/MM/yyyy or yyyy-MM-dd\n" +
                    "Time format (optional): 24-hour (e.g., 1800)";

    /**
     * Parses user input into command for execution.
     *
     * @param input full user input string
     * @param tasks TaskList to operate on
     * @param storage Storage for saving changes
     * @return response message from command execution
     * @throws TringaException if the input is invalid
     */
    public static String executeCommand(String input, TaskList tasks, Storage storage)
            throws TringaException {

        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(input.trim());
        if (!matcher.matches()) {
            throw new TringaException("Invalid command format. Type 'help' for usage instructions.");
        }

        final String commandWord = matcher.group("commandWord").toLowerCase();
        final String arguments = matcher.group("arguments").trim();

        return switch (commandWord) {
            case "list" -> tasks.listTasks();
            case "mark" -> prepareMark(arguments, tasks, storage);
            case "delete" -> prepareDelete(arguments, tasks, storage);
            case "todo" -> prepareTodo(arguments, tasks, storage);
            case "deadline" -> prepareDeadline(arguments, tasks, storage);
            case "event" -> prepareEvent(arguments, tasks, storage);
            case "bye" -> "Bye. Hope to see you again soon!";
            default -> throw new TringaException("Unknown command: " + commandWord);
        };
    }

    /**
     * Parses a deadline string and returns a formatted string representation.
     * Accepted Date and Time formats: d/MM/yyyy HHmm, yyyy-MM-dd HHmm
     * Accepted Date formats: d/MM/yyyy, yyyy-MM-dd
     * For date only: returns "MMM dd yyyy" format (e.g., "Dec 04 2024")
     * For date and time: returns "MMM dd yyyy, HH:mm" format (e.g., "Dec 04 2024, 18:00")
     *
     * @param deadline The deadline string to parse
     * @return String formatted according to the requirements
     * @throws IllegalArgumentException if the deadline format is invalid
     */
    public static String parseDeadline(String deadline) {
        try {
            deadline = deadline.trim();
            String[] parts = deadline.split("\\s+");

            // Parse the date first
            LocalDate date;
            try {
                // Try parsing as d/MM/yyyy format
                date = LocalDate.parse(parts[0],
                        DateTimeFormatter.ofPattern("d/MM/yyyy"));
            } catch (DateTimeParseException e) {
                // If that fails, try yyyy-MM-dd format
                date = LocalDate.parse(parts[0],
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            // Check if we have a time component
            if (parts.length > 1) {
                validateAndParseTime(parts[1]);

                // Extract hours and minutes
                int hours = Integer.parseInt(parts[1].substring(0, 2));
                int minutes = Integer.parseInt(parts[1].substring(2));

                // Create LocalDateTime and format
                LocalDateTime dateTime = date.atTime(hours, minutes);
                return dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm"));
            } else {
                // Format date only
                return date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
            }

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. Expected: d/MM/yyyy or yyyy-MM-dd\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Validates the time format and values.
     *
     * @param time Time string in 24-hour format (e.g., "1800")
     * @throws IllegalArgumentException if time format is invalid
     */
    private static void validateAndParseTime(String time) {
        if (!time.matches("\\d{4}")) {
            throw new IllegalArgumentException(
                    "Invalid time format. Expected 24-hour time (e.g., 1800)");
        }

        int hours = Integer.parseInt(time.substring(0, 2));
        int minutes = Integer.parseInt(time.substring(2));

        if (hours < 0 || hours > 23) {
            throw new IllegalArgumentException("Hours must be between 00 and 23");
        }
        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Minutes must be between 00 and 59");
        }
    }

    /**
     * Prepares the mark command.
     */
    private static String prepareMark(String args, TaskList tasks, Storage storage)
            throws TringaException {
        final Matcher matcher = INDEX_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException("Invalid mark command. Format: mark INDEX");
        }

        try {
            int index = Integer.parseInt(matcher.group("targetIndex"));
            String response = tasks.markTaskDone(index);
            storage.save(tasks.getTasks());
            return response;
        } catch (NumberFormatException e) {
            throw new TringaException("Task index must be a number.");
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving changes: " + e.getMessage());
        }
    }

    /**
     * Prepares the delete command.
     */
    private static String prepareDelete(String args, TaskList tasks, Storage storage)
            throws TringaException {
        final Matcher matcher = INDEX_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException("Invalid delete command. Format: delete INDEX");
        }

        try {
            int index = Integer.parseInt(matcher.group("targetIndex"));
            String response = tasks.deleteTask(index);
            storage.save(tasks.getTasks());
            return response;
        } catch (NumberFormatException e) {
            throw new TringaException("Task index must be a number.");
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving changes: " + e.getMessage());
        }
    }

    /**
     * Prepares the todo command.
     */
    private static String prepareTodo(String args, TaskList tasks, Storage storage)
            throws TringaException {
        final Matcher matcher = TODO_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException("Invalid todo command. Format: todo DESCRIPTION");
        }

        try {
            Task todo = new ToDos(matcher.group("description").trim());
            String response = tasks.addTask(todo);
            storage.save(tasks.getTasks());
            return response;
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving changes: " + e.getMessage());
        }
    }

    /**
     * Prepares the deadline command.
     */
    private static String prepareDeadline(String args, TaskList tasks, Storage storage)
            throws TringaException {
        final Matcher matcher = DEADLINE_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException(
                    "Invalid deadline command. Format: deadline DESCRIPTION /by DATE TIME\n" +
                            DATETIME_FORMAT_MESSAGE);
        }

        try {
            String description = matcher.group("description").trim();
            String deadline = parseDeadline(matcher.group("deadline").trim());

            Task deadlineTask = new Deadline(description, deadline);
            String response = tasks.addTask(deadlineTask);
            storage.save(tasks.getTasks());
            return response;
        } catch (IllegalArgumentException e) {
            throw new TringaException("Invalid date/time format: " + e.getMessage());
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving changes: " + e.getMessage());
        }
    }

    /**
     * Prepares the event command.
     */
    private static String prepareEvent(String args, TaskList tasks, Storage storage)
            throws TringaException {
        final Matcher matcher = EVENT_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException(
                    "Invalid event command. Format: event DESCRIPTION /from DATE TIME /to DATE TIME\n" +
                            DATETIME_FORMAT_MESSAGE);
        }

        try {
            String description = matcher.group("description").trim();
            String startTime = parseDeadline(matcher.group("startTime").trim());
            String endTime = parseDeadline(matcher.group("endTime").trim());

            validateEventTimes(startTime, endTime);

            Task event = new Event(description, startTime, endTime);
            String response = tasks.addTask(event);
            storage.save(tasks.getTasks());
            return response;
        } catch (IllegalArgumentException e) {
            throw new TringaException("Invalid date/time format: " + e.getMessage());
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving changes: " + e.getMessage());
        }
    }

    /**
     * Validates that event end time is after start time.
     */
    private static void validateEventTimes(String startTime, String endTime)
            throws TringaException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");

        // Only validate times if both times are provided (contain ":")
        if (startTime.contains(":") && endTime.contains(":")) {
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);

            if (!end.isAfter(start)) {
                throw new TringaException("Event end time must be after start time");
            }
        }
    }
}