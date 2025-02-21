package tringaa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import tringaa.exceptions.TaskStorageException;
import tringaa.tasks.Deadline;
import tringaa.tasks.Event;
import tringaa.tasks.Task;
import tringaa.tasks.ToDo;

/**
 * Handles loading and saving of tasks to a file.
 */
public class Storage {
    /**
     * The file path where tasks are stored, located in the data directory.
     */
    private static final Path FILE_PATH = Paths.get("data", "tringa.txt");

    /**
     * Loads tasks from the storage file. If the storage directory or file doesn't exist,
     * they will be created automatically.
     *
     * @return List of Task objects read from the storage file. Returns an empty list if
     *         the file is empty or newly created.
     * @throws TaskStorageException if there are any errors during file operations or task
     *         deserialization.
     */
    public List<Task> load() throws TaskStorageException {
        try {
            // Create data directory if it doesn't exist
            Files.createDirectories(FILE_PATH.getParent());
            // If file doesn't exist, create it and return empty list
            if (!Files.exists(FILE_PATH)) {
                Files.createFile(FILE_PATH);
                return new ArrayList<>();
            }
            List<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(FILE_PATH);

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                tasks.add(deserializeTask(line));
            }
            return tasks;
        } catch (IOException e) {
            throw new TaskStorageException("Error loading tasks: " + e.getMessage());
        }
    }



    /**
     * Saves the given list of tasks to the storage file.
     *
     * @param tasks List of tasks to save
     * @throws TaskStorageException if there are errors writing to the file
     */
    public void save(List<Task> tasks) throws TaskStorageException {
        try {
            FileWriter writer = new FileWriter(FILE_PATH.toFile());
            for (Task task : tasks) {
                writer.write(serializeTask(task) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new TaskStorageException("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Converts a task to its string representation for storage.
     * Format: TYPE | IS_DONE | DESCRIPTION [| ADDITIONAL_DATA]
     *
     * @param task Input task taken in to serialise
     */
    private String serializeTask(Task task) {
        StringBuilder sb = new StringBuilder();

        // Add task type
        if (task instanceof ToDo) {
            sb.append("T");
        } else if (task instanceof Deadline) {
            sb.append("D");
        } else if (task instanceof Event) {
            sb.append("E");
        }

        // Add done status and description
        sb.append(" | ").append(task.isDone() ? "1" : "0");
        sb.append(" | ").append(task.getDescription());

        // Add type-specific data
        if (task instanceof Deadline deadline) {
            sb.append(" | ").append(deadline.getDeadline());
        } else if (task instanceof Event event) {
            sb.append(" | ").append(event.getStart());
            sb.append(" | ").append(event.getEnd());
        }

        return sb.toString();
    }

    /**
     * Creates a task from its stored string representation.
     */
    private Task deserializeTask(String line) throws TaskStorageException {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new TaskStorageException("Invalid task format: " + line);
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task = createTaskByType(type, description, parts);

        if (isDone) {
            task.markDone();
        }

        return task;
    }

    /**
     * Creates the appropriate task type based on the type identifier and data.
     */
    @SuppressWarnings("checkstyle:Indentation")
    private Task createTaskByType(String type, String description, String[] parts)
            throws TaskStorageException {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");

        return switch (type) {
            case "T" -> new ToDo(description);
            case "D" -> {
                if (parts.length < 4) {
                    throw new TaskStorageException("Invalid deadline format: " + String.join(" | ", parts));
                }
                LocalDate date = LocalDate.parse(parts[3], inputFormatter);
                yield new Deadline(description, date.toString());
            }
            case "E" -> {
                if (parts.length < 5) {
                    throw new TaskStorageException("Invalid event format: " + String.join(" | ", parts));
                }
                yield new Event(description, parts[3], parts[4]);
            }
            default -> throw new TaskStorageException("Unknown task type: " + type);
        };
    }
}
