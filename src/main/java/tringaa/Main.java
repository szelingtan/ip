package tringaa;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Tringa using FXML.
 */
public class Main extends Application {

    private Tringa tringa = new Tringa("./src/main/data/tringa.txt");

    @Override
    public void start(Stage stage) {
        stage.setTitle("Tringa");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setTringa(tringa);  // inject the Tringa instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
