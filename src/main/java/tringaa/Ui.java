package tringaa;

/**
 * Handles user interface operations for the Tringa application.
 * Manages input/output operations, including displaying messages and reading user commands.
 */
public class Ui {
    /**
     * Displays the welcome message when the application starts.
     */
    public void giveWelcome() {
        System.out.println("Hello! I'm Tringa.");
        System.out.println("What can I do for you?");
    }

    /**
     * Displays an error message when the task file cannot be loaded.
     */
    public void showLoadingError() {
        System.out.println("Error loading task file. Starting with empty task list.");
    }

    /**
     * Displays a custom error message.
     *
     * @param message The error message to be displayed
     */
    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    /**
     * Reads a command from the standard input.
     *
     * @return The command string entered by the user
     */
    public String readCommand() {
        return new java.util.Scanner(System.in).nextLine();
    }

    /**
     * Displays a response message to the user.
     *
     * @param response The message to be displayed
     */
    public void showResponse(String response) {
        System.out.println(response);
    }
}