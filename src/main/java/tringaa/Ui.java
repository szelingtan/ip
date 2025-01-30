package tringaa;

public class Ui {
    public void giveWelcome() {
        System.out.println("Hello! I'm Tringa.");
        System.out.println("What can I do for you?");
    }

    public void showLoadingError() {
        System.out.println("Error loading task file. Starting with empty task list.");
    }

    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    public String readCommand() {
        return new java.util.Scanner(System.in).nextLine();
    }

    public void showResponse(String response) {
        System.out.println(response);
    }
}
