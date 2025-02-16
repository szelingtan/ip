package tringaa;

import javafx.application.Application;

/**
 * A launcher class that serves as the entry point for the JavaFX application.
 * This class delegates the application launch to the Main class using JavaFX
 * Application.launch() method.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
