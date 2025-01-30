package tringaa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving of tasks to a file.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a new Storage instance that loads/saves to the specified file path.
     *
     * @param filePath Path to the storage file
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return List of tasks read from the file
     * @throws TaskStorageException if there are errors reading the file
     */
    public List<Task> load() throws TaskStorageException {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            List<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(Path.of(filePath));

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
            FileWriter writer = new FileWriter(filePath);
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
     */
    private String serializeTask(Task task) {
        StringBuilder sb = new StringBuilder();

        // Add task type
        if (task instanceof ToDos) {
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
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");

        Task task = switch (type) {
            case "T" -> new ToDos(description);
            case "D" -> {
                if (parts.length < 4) {
                    throw new TaskStorageException("Invalid deadline format: " + line);
                }
                LocalDate date = LocalDate.parse(parts[3], inputFormatter);
                yield new Deadline(description, date.toString());
            }
            case "E" -> {
                if (parts.length < 5) {
                    throw new TaskStorageException("Invalid event format: " + line);
                }
                yield new Event(description, parts[3], parts[4]);
            }
            default -> throw new TaskStorageException("Unknown task type: " + type);
        };

        if (isDone) {
            task.markDone();
        }

        return task;
    }
}