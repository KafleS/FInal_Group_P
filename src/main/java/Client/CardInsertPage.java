package Client;

import Card.CardType;
import Card.CardReader;
import Display.Template;
import Display.VoterPage;
import Hardwares.Screens.Screen;
import Hardwares.Screens.ScreenDriver;
import Manager.VotingManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import  Hardwares.SDCards.SDCard;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class CardInsertPage extends Application {

    private static boolean votingOpen = false;
    private final CardReader cardReader = new CardReader();
    ScreenDriver screenDriver = new ScreenDriver(new Screen());

   private BufferedReader in;
   private PrintWriter  out;


    @Override
    public void start(Stage stage) throws IOException {


        Socket sock = new Socket("localhost", 12345);
        out = new PrintWriter(sock.getOutputStream(), true);
        in  = new BufferedReader(new InputStreamReader(sock.getInputStream()));


        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if ("FAILURE".equals(line)) {
                        Platform.runLater(() -> {
                            stage.close();
                            Platform.exit();
                        });
                        break;
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    stage.close();
                    Platform.exit();
                });
            }
        }).start();

        try {
            // Run VotingManager in a separate thread and wait for it to finish
            VotingManager manager = new VotingManager();
            Thread managerThread = new Thread(manager);
            managerThread.start();
            managerThread.join();

            System.out.println("[INFO] Ballot and templates loaded before UI setup.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("[ERROR] VotingManager thread was interrupted.");
        }


        ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/assets/logo.png"),
                        "Logo image not found")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        // UI Elements
        Label prompt = new Label("Insert Card (e.g., A12345678 or V12345678):");
        prompt.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        TextField cardInput = new TextField();
        cardInput.setPromptText("Enter Card ID...");
        cardInput.setMaxWidth(250);
        Button submitButton = new Button("Insert Card");
        Label status = new Label();
        status.setStyle("-fx-text-fill: white;");

        submitButton.setOnAction(e -> {
            String cardId = cardInput.getText().trim();

            if (cardId.isEmpty()) {
                status.setText("Card ID cannot be empty.");
                return;
            }

            if (cardReader.failure()) {
                status.setText("Card reader failure. Please contact technician.");
                return;
            }

            if (cardReader.isCardPresent()) {
                status.setText("Card already inserted. Please eject first.");
                return;
            }

            try {
                cardReader.insertCard(cardId);
                CardType type = cardReader.getCardType();

                switch (type) {
                    case ADMIN -> showAdminScene(stage);
                    case VOTER -> {
                        if (!votingOpen) {
                            status.setText("Voting is not open. Ask admin.");
                            cardReader.ejectCard();
                        } else {
                            List<Template> templates = VotingManager.getLoadedTemplates();
                            System.out.println("Templates from card insert: " + templates);
                            if (templates == null || templates.isEmpty()) {
                                status.setText("Ballot not loaded yet.");
                                cardReader.ejectCard();
                            } else {
                                displayVoterTemplates(stage, templates, 0);
                            }
                        }
                    }
                    case UNKNOWN -> {
                        status.setText("Invalid card. Use A/V followed by 8 digits.");
                        cardReader.ejectCard();
                    }
                }

            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        VBox contentBox = new VBox(15, prompt, cardInput, submitButton, status);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(300);

        VBox root = new VBox(30, logo, contentBox);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #003366;");

        stage.setScene(new Scene(root, 1300, 900));
        stage.setTitle("Voting Machine");
        stage.show();
    }

    private void showAdminScene(Stage stage) {
        VBox adminLayout = new VBox(20);
        adminLayout.setAlignment(Pos.CENTER);
        Label adminLabel = new Label("Admin Panel");
        adminLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Button controlButton = new Button();
        Label status = new Label();
        Button ejectButton = new Button("Eject Card");



        if (!votingOpen) {
            controlButton.setText("Open Voting");
            controlButton.setOnAction(e -> {
                votingOpen = true;
                status.setText("Voting session started.");
                controlButton.setText("Close Voting");

            });
        } else {
            controlButton.setText("Close Voting");
            controlButton.setOnAction(e -> {
                votingOpen = false;
                status.setText(" Voting session closed.");
                controlButton.setDisable(true);
            });
        }

        ejectButton.setOnAction(e -> {
            cardReader.ejectCard();
            try {
                start(stage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        adminLayout.getChildren().addAll(adminLabel, controlButton, status, ejectButton);
        Scene adminScene = new Scene(adminLayout, 800, 700);
        stage.setScene(adminScene);
    }

    private void displayVoterTemplates(Stage stage, List<Template> templates, int index) {
        Template current = templates.get(index);
        VoterPage voterPage = new VoterPage(current, stage, index, templates.size());

        voterPage.getPreviousButton().setOnAction(e -> {
            if (index > 0) displayVoterTemplates(stage, templates, index - 1);
            else {
                try {
                    start(stage);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        voterPage.getNextButton().setOnAction(e -> {
            if (index < templates.size() - 1)
                displayVoterTemplates(stage, templates, index + 1);
            else {
                VBox finishLayout = new VBox(20);
                finishLayout.setAlignment(Pos.CENTER);
                Label doneLabel = new Label("Your vote has been recorded.");
                Button ejectButton = new Button("Eject Card");

                ejectButton.setOnAction(ev -> {
                    try {
                        cardReader.ejectCard();
                        start(stage);
                    } catch (Exception ex) {
                        doneLabel.setText("Eject failed: " + ex.getMessage());
                    }
                });

                finishLayout.getChildren().addAll(doneLabel, ejectButton);
                stage.setScene(new Scene(finishLayout, 600, 800));
            }
        });

        stage.setScene(voterPage.getScene());
    }

    public static void main(String[] args) {
        launch(args);
    }
}