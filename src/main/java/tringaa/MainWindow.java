package tringaa;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;


    private Tringa tringa;


    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/cinna.png"));
    private Image tringaImage = new Image(this.getClass().getResourceAsStream("/images/poc.png"));


    /** Injects the program */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getTringaDialog("Hello, I'm Tringa! How can I help you today?",
                        tringaImage)
        );
    }


    /** Injects the Tringa instance */
    public void setTringa(Tringa t) {
        tringa = t;
    }


    /**
     * Creates two dialog boxes, one echoing user input and the other containing Tringa's reply and
     * then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = tringa.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTringaDialog(response, tringaImage)
        );
        userInput.clear();
    }
}
