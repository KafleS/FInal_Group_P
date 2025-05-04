package Client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class CardInsertPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label instructionLabel = new Label("Enter Card ID (e.g., A12345678 or V87654321):");
        TextField cardInput = new TextField();
        cardInput.setPromptText("Card ID...");
        Button submitButton = new Button("Insert Card");
        Label statusLabel = new Label();

        submitButton.setOnAction(e -> {
            String cardId = cardInput.getText().trim();
            if (cardId.isEmpty()) {
                statusLabel.setText("Please enter a card ID.");
                return;
            }

            try (Socket socket = new Socket("localhost", 12345)) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                out.println(cardId);
                statusLabel.setText("Card data sent successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Failed to connect to server.");
            }
        });

        VBox root = new VBox(10, instructionLabel, cardInput, submitButton, statusLabel);
        root.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(root, 350, 180));
        primaryStage.setTitle("Card Insert Page");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
