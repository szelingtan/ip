package tringaa;

import java.util.List;

/**
 * Main class for the Tringa chatbot.
 */
public class Tringa {
    private TaskList tasks;
    private final Storage storage;
    private final Ui ui;

    /**
     * Initializes the Tringa task management system.
     *
     * @param filePath Path to the storage file
     */
    public Tringa(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            List<Task> loadedTasks = storage.load();
            tasks = new TaskList(loadedTasks);
        } catch (TaskStorageException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main program loop that handles user interactions.
     */
    public void run() {
        ui.giveWelcome();
        boolean isRunning = true;

        while (isRunning) {
            try {
                String command = ui.readCommand();

                // Check for exit command
                if (command.equals("bye")) {
                    getResponse("Bye. Hope to see you again soon!");
                    isRunning = false;
                    continue;
                }

                // Process the command
                String response = Parser.executeCommand(command, tasks, storage);
                getResponse(response);

            } catch (TringaException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Entry point of the application.
     *
     * @param args Command line inputs
     */
    public static void main(String[] args) {
        Tringa tringa = new Tringa("./src/main/data/tringa.txt");
        tringa.run();
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) {
        try {
            if (input.equals("bye")) {
                return "Bye. Hope to see you again soon!";
            }
            String response = Parser.executeCommand(input, tasks, storage);
            return response;
        } catch (TringaException e) {
            return e.getMessage();
        }
    }

}
