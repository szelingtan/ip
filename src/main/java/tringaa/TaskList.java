package tringaa;

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
        return String.format("Noted. I've removed this task:\n  %s\nNow you " +
                        "have %d tasks in the list.",
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
        return String.format("Got it. I've added this task:\n  %s\nNow you " +
                        "have %d tasks in the list.",
                task, tasks.size());
    }

    /**
     * Validates that an index is within the valid range for the task list.
     *
     * @param index One-based index to validate
     * @throws TringaException if index is invalid
     */
    private void validateIndex(int index) throws TringaException {
        if (index < 1 || index > tasks.size()) {
            throw new TringaException(
                    String.format("Invalid task number: %d. Please provide a number" +
                                    " between 1 and %d.",
                            index, tasks.size()));
        }
    }

}