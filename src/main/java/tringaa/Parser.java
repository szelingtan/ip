package tringaa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the parsing and execution of user commands in the Tringa application.
 * This class processes raw input strings and converts them into appropriate task operations.
 * It uses regular expressions to validate and extract information from user commands.
 * Regular expression patterns used in this class are based on examples from:
 * @see <a href="https://www.w3schools.com/java/java_regex.asp">W3Schools Java Regex Tutorial</a>
 */

public class Parser {
    /** Pattern to match the basic command format: command word followed by arguments */
    private static final Pattern BASIC_COMMAND_FORMAT =
            Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    /** Pattern to match todo task arguments: just the description */
    private static final Pattern TODO_ARGS_FORMAT =
            Pattern.compile("(?<description>.+)");
    /** Pattern to match deadline task arguments: description and deadline date */
    private static final Pattern DEADLINE_ARGS_FORMAT =
            Pattern.compile("(?<description>[^/]+)/by(?<deadline>.+)");
    /** Pattern to match event task arguments: description, start date, and end date */
    private static final Pattern EVENT_ARGS_FORMAT =
            Pattern.compile("(?<description>[^/]+)/from(?<startDate>[^/]+)/to(?<endDate>.+)");
    /** Pattern to match index arguments for mark and delete commands */
    private static final Pattern INDEX_ARGS_FORMAT =
            Pattern.compile("(?<targetIndex>\\d+)");
    private static final Pattern FIND_ARGS_FORMAT =
            Pattern.compile("(?<keyword>.+)");
    /** Pattern to match upcoming tasks command: must be exactly "tasks" */
    private static final Pattern UPCOMING_TASKS_FORMAT =
            Pattern.compile("^upcoming tasks$", Pattern.CASE_INSENSITIVE);

    /**
     * Executes a command based on the user input.
     *
     * @param input The raw input string from the user
     * @param tasks The TaskList object containing all tasks
     * @param storage The Storage object for saving task data
     * @return A response message indicating the result of the command execution
     * @throws TringaException if the command is invalid or execution fails
     */
    public static String executeCommand(String input, TaskList tasks, Storage storage)
            throws TringaException {
        assert input != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";

        input = input.trim();
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(input);

        if (!matcher.matches()) {
            throw new InvalidCommandException("Invalid command format. Refer to documentation.");
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
        case "find" -> prepareFind(arguments, tasks);
        case "upcoming" -> prepareUpcomingTasks(input, tasks);
        default -> throw new UnknownCommandException(commandWord);
        };
    }

    /**
     * Prepares and executes a deadline task creation command.
     *
     * @param args The arguments string containing description and deadline
     * @param tasks The TaskList to add the deadline to
     * @param storage The Storage object for saving the task
     * @return A response message indicating the result
     * @throws InvalidCommandException if the deadline command is invalid
     * @throws TringaException if saving fails
     */
    private static String prepareDeadline(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";

        if (args.trim().isEmpty()) {
            throw new InvalidCommandException("""
                Description cannot be empty.
                Format: deadline DESCRIPTION /by DATE
                """);
        }

        if (args.trim().startsWith("/by")) {
            throw new InvalidCommandException("""
                Missing task description.
                Format: deadline DESCRIPTION /by DATE
                """);
        }

        final Matcher matcher = DEADLINE_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new InvalidCommandException("""
                Invalid deadline command.
                Format: deadline DESCRIPTION /by DATE
                Date format: yyyy-MM-dd (e.g., 2023-02-22)
                """);
        }

        try {
            String description = matcher.group("description").trim();
            if (description.isEmpty()) {
                throw new TringaException("Task description cannot be empty.");
            }
            String dateStr = matcher.group("deadline").trim();
            Task deadlineTask = new Deadline(description, dateStr);

            // Add task to current task list
            String response = tasks.addTask(deadlineTask);
            // Schedule tasks to be reminded at 9AM the day before event starts
            Reminder.scheduleReminder(deadlineTask);
            // Save new task list in file
            storage.save(tasks.getTasks());

            return response;
        } catch (DateTimeParseException e) {
            throw new InvalidCommandException("Invalid date format. Use: yyyy-MM-dd (e.g., " +
                    "2023-02-22)");
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving task: " + e.getMessage());
        }
    }

    /**
     * Prepares and executes an event task creation command.
     *
     * @param args The arguments string containing description and event dates
     * @param tasks The TaskList to add the event to
     * @param storage The Storage object for saving the task
     * @return A response message indicating the result
     * @throws InvalidCommandException if the event command is invalid
     * @throws TringaException if saving fails
     */
    private static String prepareEvent(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";

        final Matcher matcher = EVENT_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new InvalidCommandException("""
                Invalid event command.
                Format: event DESCRIPTION /from DATE /to DATE
                Date format: yyyy-MM-dd (e.g., 2023-02-22)
                """);
        }

        try {
            String description = matcher.group("description").trim();
            if (description.isEmpty()) {
                throw new InvalidCommandException("Task description cannot be empty.");
            }

            // Process start date
            String startDateStr = matcher.group("startDate").trim();
            LocalDate startDate = LocalDate.parse(startDateStr);
            String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));

            // Process end date
            String endDateStr = matcher.group("endDate").trim();
            LocalDate endDate = LocalDate.parse(endDateStr);
            String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
            Task eventTask = new Event(description, formattedStartDate, formattedEndDate);

            // Add task to current task list
            String response = tasks.addTask(eventTask);
            // Schedule tasks to be reminded at 9AM the day before event starts
            Reminder.scheduleReminder(eventTask);
            // Save new task list in file
            storage.save(tasks.getTasks());

            return response;
        } catch (DateTimeParseException e) {
            throw new InvalidCommandException("Invalid date format. Use: yyyy-MM-dd (e.g., " +
                    "2023-02-22)");
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving task: " + e.getMessage());
        }
    }

    /**
     * Prepares and executes a mark-as-done command.
     *
     * @param args The arguments string containing the task index
     * @param tasks The TaskList containing the task to mark
     * @param storage The Storage object for saving the changes
     * @return A response message indicating the result
     * @throws InvalidCommandException if the mark command index is missing or invalid
     * @throws TringaException if saving fails
     */
    private static String prepareMark(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";

        final Matcher matcher = INDEX_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new InvalidCommandException("Invalid mark command. Format: mark INDEX");
        }

        try {
            int index = Integer.parseInt(matcher.group("targetIndex"));
            String response = tasks.markTaskDone(index);
            storage.save(tasks.getTasks());
            return response;
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Task index must be a number.");
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving changes: " + e.getMessage());
        }
    }

    /**
     * Prepares and executes a delete task command.
     *
     * @param args The arguments string containing the task index
     * @param tasks The TaskList containing the task to delete
     * @param storage The Storage object for saving the changes
     * @return A response message indicating the result
     * @throws InvalidCommandException if the delete command index is missing or invalid
     * @throws TringaException if saving fails
     */
    private static String prepareDelete(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";

        final Matcher matcher = INDEX_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new InvalidCommandException("Invalid delete command. Format: delete INDEX");
        }

        try {
            int index = Integer.parseInt(matcher.group("targetIndex"));
            String response = tasks.deleteTask(index);
            storage.save(tasks.getTasks());
            return response;
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Task index must be a number.");
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving changes: " + e.getMessage());
        }
    }

    /**
     * Prepares and executes a todo task creation command.
     *
     * @param args The arguments string containing the todo description
     * @param tasks The TaskList to add the todo to
     * @param storage The Storage object for saving the task
     * @return A response message indicating the result
     * @throws InvalidCommandException if the todo format is invalid
     * @throws TringaException if saving fails
     */
    private static String prepareTodo(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";

        final Matcher matcher = TODO_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new InvalidCommandException("Invalid todo command. Format: todo DESCRIPTION");
        }

        try {
            Task todo = new ToDo(matcher.group("description").trim());
            String response = tasks.addTask(todo);
            storage.save(tasks.getTasks());
            return response;
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving task: " + e.getMessage());
        }
    }

    /**
     * Prepares and executes a find task command.
     *
     * @param args The arguments string containing the find keyword
     * @param tasks The TaskList of all current tasks in the list
     * @return A formatted string containing the list tasks which match the keyword
     * @throws InvalidCommandException if the todo format is invalid
     */
    private static String prepareFind(String args, TaskList tasks)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";

        final Matcher matcher = FIND_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new InvalidCommandException("Invalid find command. Usage: find KEYWORD");
        }
        String keyword = matcher.group("keyword").trim();
        if (keyword.isEmpty()) {
            throw new InvalidCommandException("find KEYWORD cannot be empty.");
        }
        return tasks.findTasks(keyword);
    }
    /**
     * Executes the command to list upcoming tasks.
     * Only accepts the exact phrase "upcoming tasks".
     *
     * @param input The input string which must be "upcoming tasks"
     * @param tasks The TaskList to search for upcoming tasks
     * @return A formatted string containing the list of upcoming tasks
     * @throws InvalidCommandException if the command format is invalid
     */
    private static String prepareUpcomingTasks(String input, TaskList tasks)
            throws TringaException {
        assert input != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";

        if (!UPCOMING_TASKS_FORMAT.matcher(input.trim()).matches()) {
            throw new InvalidCommandException("Invalid command format. Usage: upcoming task");
        }
        return tasks.listUpcomingTasks();
    }

}
