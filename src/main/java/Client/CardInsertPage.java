package Client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class CardInsertPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/assets/logo.png"),
                        "Logo image not found")));
        logo.setFitWidth(600);
        logo.setPreserveRatio(true);

        Label instructionLabel = new Label("Enter Card ID (e.g., A12345678 or V87654321):");
        instructionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField cardInput = new TextField();
        cardInput.setPromptText("Insert Card ID...");
        cardInput.setMaxWidth(280);

        Button submitButton = new Button("Insert Card");
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: white;");

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

        VBox contentBox = new VBox(15, instructionLabel, cardInput, submitButton, statusLabel);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(300);

        VBox root = new VBox(30, logo, contentBox);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #003366;");

        Scene scene = new Scene(root, 1300, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Voting Machine");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}