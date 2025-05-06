package Client;

import Card.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CardInsertPage extends Application {
    private final CardReader cardReader = new CardReader();

    @Override
    public void start(Stage primaryStage) {
        Label instruction = new Label("Insert Card (e.g., V12345678):");
        TextField cardInput = new TextField();
        cardInput.setPromptText("Enter card ID...");
        Button submitButton = new Button("Submit");
        Label status = new Label();
        VBox ballotBox = new VBox(10); // Where template info is shown

        submitButton.setOnAction(e -> {
            String cardId = cardInput.getText().trim();
            ballotBox.getChildren().clear(); // Clear previous output

            if (cardId.isEmpty()) {
                ballotBox.getChildren().add(new Label("Card ID cannot be empty."));
                return;
            }

            if (cardReader.failure()) {
                ballotBox.getChildren().add(new Label("Card reader failure. Please contact technician."));
                return;
            }

            try {
                cardReader.insertCard(cardId);
                CardType type = cardReader.getCardType();

                if (type == CardType.VOTER) {
                    // 🔁 Use SocketHandler to send card ID to server
                    new Thread(() -> {
                        String result = SocketHandler.sendCardToScreen(cardId);

                        Platform.runLater(() -> {
                            status.setText("Message from ScreenDriver:\n" + result);


                            Button ejectButton = new Button("Eject Card");
                            ejectButton.setOnAction(ev -> {
                                cardReader.eraseCard();
                                cardReader.ejectCard();
                                status.setText("Card ejected and erased.");
                            });

                            ballotBox.getChildren().addAll(new Label(result), ejectButton);
                        });
                    }).start();

                } else {
                    ballotBox.getChildren().add(new Label("Not a valid voter card."));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                ballotBox.getChildren().add(new Label("Error: " + ex.getMessage()));
            }
        });

        VBox root = new VBox(15, instruction, cardInput, submitButton, new Separator(), ballotBox, status);
        root.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setTitle("Card Insert Page");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
