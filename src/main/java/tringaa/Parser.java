package tringaa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final Pattern BASIC_COMMAND_FORMAT =
            Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Pattern TODO_ARGS_FORMAT =
            Pattern.compile("(?<description>.+)");
    private static final Pattern DEADLINE_ARGS_FORMAT =
            Pattern.compile("(?<description>[^/]+)/by(?<deadline>.+)");
    private static final Pattern EVENT_ARGS_FORMAT =
            Pattern.compile("(?<description>[^/]+)/from(?<startDate>[^/]+)/to(?<endDate>.+)");
    private static final Pattern INDEX_ARGS_FORMAT =
            Pattern.compile("(?<targetIndex>\\d+)");

    public static String executeCommand(String input, TaskList tasks, Storage storage)
            throws TringaException {
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
            default -> throw new TringaException("Unknown command: " + commandWord);
        };
    }

    private static String prepareDeadline(String args, TaskList tasks, Storage storage)
            throws TringaException {
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
            String response = tasks.addTask(deadlineTask);
            storage.save(tasks.getTasks());
            return response;
        } catch (DateTimeParseException e) {
            throw new TringaException("Invalid date format. Use: yyyy-MM-dd (e.g., 2023-02-22)");
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving task: " + e.getMessage());
        }
    }

    private static String prepareEvent(String args, TaskList tasks, Storage storage)
            throws TringaException {
        final Matcher matcher = EVENT_ARGS_FORMAT.matcher(args);
        if (!matcher.matches()) {
            throw new TringaException("""
                Invalid event command.
                Format: event DESCRIPTION /from DATE /to DATE
                Date format: yyyy-MM-dd (e.g., 2023-02-22)
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
            throw new TringaException("Invalid date format. Use: yyyy-MM-dd (e.g., 2023-02-22)");
        } catch (TaskStorageException e) {
            throw new TringaException("Error saving task: " + e.getMessage());
        }
    }

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
            throw new TringaException("Error saving task: " + e.getMessage());
        }
    }
}