package tringaa;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A utility class that manages automatic reminders for tasks.
 * This class handles scheduling and displaying reminders for Deadline and Event tasks.
 * Reminders are scheduled one day before the task's due date and displayed at 9 AM.
 */
public class Reminder {
    /** Timer used to schedule all reminders */
    private static final Timer timer = new Timer(true);

    /** Formatter for parsing dates stored in tasks */
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Schedules a reminder for a task based on its type and due date.
     * For both Deadline and Event tasks, a reminder is scheduled for 9 AM
     * on the day before the task is due.
     *
     * @param task The task to schedule a reminder for. Must be either a Deadline or Event task.
     */
    public static void scheduleReminder(Task task) {
        LocalDateTime reminderDateTime = null;

        // Calculate when to show the reminder based on task type
        if (task instanceof Deadline deadline) {
            // Parse the deadline date and set reminder for previous day
            LocalDate deadlineDate = LocalDate.parse(deadline.getDeadline(), formatter);
            reminderDateTime = LocalDateTime.of(deadlineDate.minusDays(1), LocalTime.of(9, 0));
        } else if (task instanceof Event event) {
            // Parse the event start date and set reminder for previous day
            LocalDate eventDate = LocalDate.parse(event.getStart(), formatter);
            reminderDateTime = LocalDateTime.of(eventDate.minusDays(1), LocalTime.of(9, 0));
        }

        // Only schedule if we have a valid future reminder time
        if (reminderDateTime != null && reminderDateTime.isAfter(LocalDateTime.now())) {
            scheduleNotification(task, reminderDateTime);
        }
    }

    /**
     * Schedules a notification to be shown at the specified time.
     * Uses JavaFX Platform.runLater to ensure the alert is shown on the UI thread.
     *
     * @param task The task to show the reminder for
     * @param reminderTime The time at which to show the reminder
     */
    private static void scheduleNotification(Task task, LocalDateTime reminderTime) {
        // Calculate delay until reminder time in milliseconds
        long delay = java.time.Duration.between(
                LocalDateTime.now(),
                reminderTime
        ).toMillis();

        // Schedule the reminder alert
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Ensure alert is shown on JavaFX application thread
                Platform.runLater(() -> showReminderAlert(task));
            }
        }, delay);
    }

    /**
     * Displays a JavaFX alert with the reminder information.
     * The alert includes the task type (Deadline/Event) and description.
     *
     * @param task The task to show the reminder for
     */
    private static void showReminderAlert(Task task) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Task Reminder");
        alert.setHeaderText("Task Due Tomorrow");

        // Customize message based on task type
        if (task instanceof Deadline) {
            alert.setContentText("Deadline tomorrow: " + task.getDescription());
        } else if (task instanceof Event) {
            alert.setContentText("Event tomorrow: " + task.getDescription());
        }

        alert.show();
    }
}
