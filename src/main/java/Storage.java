import java.io.*;
import java.util.*;

/**
 * Handles load and save
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a new Storage instance that handles saving/loading from the specified file.
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
     * @throws TaskStorageException if there's an error reading from the file
     */
    public List<Task> load() throws TaskStorageException {
        List<Task> tasks = new ArrayList<>();
        try {
            File file = new File(filePath);

            // If file doesn't exist, return empty list
            if (!file.exists()) {
                return tasks;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        tasks.add(parseTaskFromString(line));
                    }
                }
            }
            return tasks;

        } catch (IOException e) {
            throw new TaskStorageException("Error loading from file: " + e.getMessage());
        }
    }

    /**
     * Saves the list of tasks to the storage file.
     *
     * @param tasks List of tasks to save
     * @throws TaskStorageException if there's an error writing to the file
     */
    public void save(List<Task> tasks) throws TaskStorageException {
        try {
            // Create directories if they don't exist
            File file = new File(filePath);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Task task : tasks) {
                    writer.write(formatTaskForStorage(task));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new TaskStorageException("Error saving to file: " + e.getMessage());
        }
    }

    /**
     * Parses a task from its string representation in the storage file.
     * Format: [TYPE]|[STATUS]|[DESCRIPTION]|[ADDITIONAL_INFO]
     *
     * @param line The line from the storage file
     * @return The parsed Task object
     * @throws TaskStorageException if the line format is invalid
     */
    private Task parseTaskFromString(String line) throws TaskStorageException {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 3) {
                throw new TaskStorageException("Invalid task format in file");
            }
            return getTask(parts);
        } catch (Exception e) {
            throw new TaskStorageException("Error parsing task: " + e.getMessage());
        }
    }

    private static Task getTask(String[] parts) throws TaskStorageException {
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task = switch (type) {
            case "T" -> new ToDos(description);
            case "D" -> new Deadline(description, parts[3]);
            case "E" -> new Event(description, parts[3], parts[4]);
            default -> throw new TaskStorageException("Unknown task type: " + type);
        };

        if (isDone) {
            task.markDone();
        }
        return task;
    }

    /**
     * Formats a task for storage in the file.
     * Format: [TYPE]|[STATUS]|[DESCRIPTION]|[ADDITIONAL_INFO]
     *
     * @param task The task to format
     * @return The formatted string
     */
    private String formatTaskForStorage(Task task) {
        StringBuilder sb = new StringBuilder();

        // Add task type
        if (task instanceof ToDos) {
            sb.append("T");
        } else if (task instanceof Deadline) {
            sb.append("D");
        } else if (task instanceof Event) {
            sb.append("E");
        }

        // Add status and description
        sb.append("|").append(task.isDone() ? "1" : "0")
                .append("|").append(task.toString());

        // Add additional info for specific task types
        if (task instanceof Deadline) {
            sb.append("|").append(((Deadline) task).getDeadline());
        } else if (task instanceof Event event) {
            sb.append("|").append(event.getStartTime())
                    .append("|").append(event.getEndTime());
        }

        return sb.toString();
    }
}

