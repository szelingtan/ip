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
            throw new TringaException("Invalid command format. Refer to documentation.");
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
            case "find" -> prepareFindTask(arguments, tasks);
            default -> throw new TringaException("Unknown command: " + commandWord);
        };
    }


    /**
     * Prepares and executes a deadline task creation command.
     *
     * @param args The arguments string containing description and deadline
     * @param tasks The TaskList to add the deadline to
     * @param storage The Storage object for saving the task
     * @return A response message indicating the result
     * @throws TringaException if the deadline format is invalid or saving fails
     */
    private static String prepareDeadline(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";


        if (args.trim().isEmpty()) {
            throw new TringaException("""
               Description cannot be empty.
               Format: deadline DESCRIPTION /by DATE
               """);
        }


        if (args.trim().startsWith("/by")) {
            throw new TringaException("""
               Missing task description.
               Format: deadline DESCRIPTION /by DATE
               """);
        }


        final Matcher matcher = DEADLINE_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException("""
               Invalid deadline command.
               Format: deadline DESCRIPTION /by DATE
               Date format: yyyy-MM-dd (e.g., 2025-02-20)
               """);
        }


        try {
            String description = matcher.group("description").trim();
            if (description.isEmpty()) {
                throw new TringaException("Task description cannot be empty.");
            }
            String dateStr = matcher.group("deadline").trim();
            Task deadlineTask = new Deadline(description, dateStr);
            String response = tasks.addTask(deadlineTask);
            storage.save(tasks.getTasks());
            return response;
        } catch (DateTimeParseException e) {
            throw new TringaException("Invalid date format. Use: yyyy-MM-dd (e.g., 2025-02-20)");
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
     * @throws TringaException if the event format is invalid or saving fails
     */
    private static String prepareEvent(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";


        final Matcher matcher = EVENT_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException("""
               Invalid event command.
               Format: event DESCRIPTION /from DATE /to DATE
               Date format: yyyy-MM-dd (e.g., 2025-02-20)
               """);
        }


        try {
            String description = matcher.group("description").trim();
            if (description.isEmpty()) {
                throw new TringaException("Task description cannot be empty.");
            }


            // Process start date
            String startDateStr = matcher.group("startDate").trim();
            LocalDate startDate = LocalDate.parse(startDateStr);
            String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));


            // Process end date
            String endDateStr = matcher.group("endDate").trim();
            LocalDate endDate = LocalDate.parse(endDateStr);
            String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));


            Task event = new Event(description, formattedStartDate, formattedEndDate);
            String response = tasks.addTask(event);
            storage.save(tasks.getTasks());
            return response;
        } catch (DateTimeParseException e) {
            throw new TringaException("Invalid date format. Use: yyyy-MM-dd (e.g., 2025-02-20)");
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
     * @throws TringaException if the index is invalid or saving fails
     */
    private static String prepareMark(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";


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
     * Prepares and executes a delete task command.
     *
     * @param args The arguments string containing the task index
     * @param tasks The TaskList containing the task to delete
     * @param storage The Storage object for saving the changes
     * @return A response message indicating the result
     * @throws TringaException if the index is invalid or saving fails
     */
    private static String prepareDelete(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";


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
     * Prepares and executes a todo task creation command.
     *
     * @param args The arguments string containing the todo description
     * @param tasks The TaskList to add the todo to
     * @param storage The Storage object for saving the task
     * @return A response message indicating the result
     * @throws TringaException if the todo format is invalid or saving fails
     */
    private static String prepareTodo(String args, TaskList tasks, Storage storage)
            throws TringaException {
        assert args != null : "Input string cannot be null";
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";


        final Matcher matcher = TODO_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException("Invalid todo command. Format: todo DESCRIPTION");
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


    private static String prepareFindTask(String args, TaskList tasks)
            throws TringaException {
        if (args.trim().isEmpty()) {
            throw new TringaException("Find command cannot be empty");
        }
        final Matcher matcher = FIND_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException("Invalid find command. Usage: find KEYWORD");
        }
        String keyword = matcher.group("keyword").trim();
        if (keyword.isEmpty()) {
            throw new TringaException("find KEYWORD cannot be empty.");
        }
        return tasks.findTasks(keyword);
    }
}
