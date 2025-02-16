package tringaa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of tasks and operations on them.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a new TaskList with an existing list of tasks.
     *
     * @param tasks The initial list of tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Creates a new empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Gets a copy of the current task list.
     *
     * @return A new list containing all tasks
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }


    /**
     * Lists all tasks in a formatted string.
     *
     * @return String containing numbered list of all tasks
     */
    public String listTasks() {
        if (tasks.isEmpty()) {
            return "No tasks in your list yet!";
        }

        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(String.format("%d.%s\n", i + 1, tasks.get(i)));
        }
        return sb.toString().trim();
    }

    /**
     * Marks a task as done.
     *
     * @param index One-based index of the task
     * @return Response message indicating success
     * @throws TringaException if index is invalid
     */
    public String markTaskDone(int index) throws TringaException {
        validateIndex(index);
        Task task = tasks.get(index - 1);
        task.markDone();
        return String.format("Nice! I've marked this task as done:\n  %s", task);
    }

    /**
     * Deletes a task from the list.
     *
     * @param index One-based index of the task to delete
     * @return Response message indicating success
     * @throws TringaException if index is invalid
     */
    public String deleteTask(int index) throws TringaException {
        validateIndex(index);
        Task deletedTask = tasks.remove(index - 1);
        return String.format("Noted. I've removed this task:\n  %s\nNow you "
                        + "have %d tasks in the list.",
                deletedTask, tasks.size());
    }

    /**
     * Adds a new task to the list.
     *
     * @param task Task to add
     * @return Response message indicating success
     * @throws TringaException if task is null
     */
    public String addTask(Task task) throws TringaException {
        if (task == null) {
            throw new TringaException("Cannot add null task");
        }
        tasks.add(task);
        return String.format("Got it. I've added this task:\n  %s\nNow you "
                        + "have %d tasks in the list.",
                task, tasks.size());
    }

    /**
     * Validates that an index is within the valid range for the task list.
     *
     * @param index One-based index to validate
     * @throws TringaException if index is invalid or list is empty
     */
    private void validateIndex(int index) throws TringaException {
        if (tasks.isEmpty()) {
            throw new TringaException("No tasks in list!");
        }

        if (index < 1 || index > tasks.size()) {
            throw new TringaException(
                    String.format("Invalid task number: %d. Please provide a number"
                                    + " that is between 1 and %d.",
                            index, tasks.size()));
        }
    }

    /**
     * Searches for tasks that contain the specified keyword in their description.
     * The search is case-insensitive and ignores leading/trailing whitespace.
     *
     * @param keyword The search term to look for in task descriptions
     * @return A formatted string containing the list of matching tasks, or a message if no tasks are found
     */

    public String findTasks(String keyword) {
        List<Integer> matchingIndices = new ArrayList<>();
        String search = keyword.toLowerCase().trim();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(search)) {
                matchingIndices.add(i);
            }
        }
        if (matchingIndices.isEmpty()) {
            return "No matching tasks found in current list.";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Here are the matching tasks in your " + "list\n");
            int resultNumber = 1;
            for (int originalIndex : matchingIndices) {
                Task task = tasks.get(originalIndex);
                sb.append(String.format("%d.%s\n", resultNumber, task));
                resultNumber++;
            }
            return sb.toString().trim();
        }
    }

    /**
     * Lists all upcoming tasks (events and deadlines that haven't passed).
     * Tasks are considered upcoming if they are:
     * - Not marked as done
     * - Due date/event date is in the future
     *
     * @return A formatted string containing the list of upcoming tasks
     */
    public String listUpcomingTasks() {
        List<Task> upcomingTasks = getUpcomingTasks();

        if (upcomingTasks.isEmpty()) {
            return "No upcoming tasks!";
        }

        // Sort tasks by date
        upcomingTasks.sort((t1, t2) -> {
            LocalDate date1 = getTaskDate(t1);
            LocalDate date2 = getTaskDate(t2);
            return date1.compareTo(date2);
        });

        StringBuilder sb = new StringBuilder();
        sb.append("Here are your upcoming tasks:\n");
        for (int i = 0; i < upcomingTasks.size(); i++) {
            Task task = upcomingTasks.get(i);
            sb.append(i + 1).append(". ").append(task.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets a list of all upcoming tasks that aren't done and are due in the future.
     *
     * @return List of upcoming tasks
     */
    private List<Task> getUpcomingTasks() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        LocalDate today = LocalDate.now();
        List<Task> upcomingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isDone()) {
                continue;
            }

            LocalDate taskDate = null;
            if (task instanceof Deadline deadline) {
                taskDate = LocalDate.parse(deadline.getDeadline(), formatter);
            } else if (task instanceof Event event) {
                taskDate = LocalDate.parse(event.getStart(), formatter);
            }

            if (taskDate != null && !taskDate.isBefore(today)) {
                upcomingTasks.add(task);
            }
        }

        return upcomingTasks;
    }

    /**
     * Helper method to get the date from a task
     */
    private LocalDate getTaskDate(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        if (task instanceof Deadline deadline) {
            return LocalDate.parse(deadline.getDeadline(), formatter);
        } else if (task instanceof Event event) {
            return LocalDate.parse(event.getStart(), formatter);
        }
        return null;
    }

}
