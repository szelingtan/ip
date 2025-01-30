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
                    ui.showResponse("Bye. Hope to see you again soon!");
                    isRunning = false;
                    continue;
                }

                // Process the command
                String response = Parser.executeCommand(command, tasks, storage);
                ui.showResponse(response);

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
}



