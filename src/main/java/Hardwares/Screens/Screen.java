package Hardwares.Screens;

import Card.CardReader;
import Card.CardType;
import Display.Template;
import Display.VoterPage;
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

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class Screen extends Application {

    private boolean isOn = false;
    private boolean status = false;
    private String currentTemplate = null;
    private boolean userReady = false;
    private static boolean votingOpen = false;
    private ScreenDriver screenDriver = new ScreenDriver(this);

    private final CardReader cardReader = new CardReader();
    private BufferedReader in;
    private PrintWriter out;


    // checks if the screen is on.
    public void screenOn() {
        if (!status) {
            isOn = true;
            System.out.println("[Screen] Powered ON.");
        } else {
            System.out.println("[Screen] Cannot power on: screen failure detected.");
        }
    }

    public void presentTemplate(String template) {
        if (!status && isOn) {
            currentTemplate = template;
            userReady = false;
            System.out.println("[Screen] Presenting template: " + template);
        } else {
            System.out.println("[Screen] Cannot present template. Screen is either off or failed.");
        }
    }

    public boolean screenFailed() {
        return status;
    }

    public void screenOff() {
        isOn = false;
        currentTemplate = null;
        System.out.println("[Screen] Powered OFF.");
    }

    public void setFailure(boolean status) {
        this.status = status;
        if (status) System.out.println("[Screen] FAILURE detected!");
    }

    public void simulateUserDone() {
        if (!status && isOn) {
            userReady = true;
            System.out.println("[Screen] User interaction complete.");
        }
    }


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
            VotingManager manager = new VotingManager();
            Thread managerThread = new Thread(manager);
            managerThread.start();
            managerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/assets/logo.png"))));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        VBox contentBox = getVBox(stage);
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


    /**
     * Initial screen to get started
     * @param stage
     * @return
     */
    private VBox getVBox(Stage stage) {

        Label prompt = new Label("Insert Card (eg: A12345678 for Admin or V12345678 for Voter):");
        prompt.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        TextField cardInput = new TextField();
        cardInput.setPromptText("Enter Card ID...");
        cardInput.setMaxWidth(250);
        Button submitButton = new Button("Insert Card");
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: white;");

        submitButton.setOnAction(e -> {
            String cardId = cardInput.getText().trim();

            if (cardId.isEmpty()) {
                statusLabel.setText("Card ID cannot be empty.");
                return;
            }

            if (cardReader.failure()) {
                statusLabel.setText("Card reader failure. Please contact technician.");
                return;
            }

            if (cardReader.isCardPresent()) {
                statusLabel.setText("Card already inserted. Please eject first.");
                return;
            }

            try {
                cardReader.insertCard(cardId);
                CardType type = cardReader.getCardType();

                switch (type) {
                    case ADMIN -> showAdminScene(stage);
                    case VOTER -> {
                        if (!votingOpen) {
                            statusLabel.setText("Voting is not open. Ask admin.");
                            cardReader.ejectCard();
                        } else {
                            List<Template> templates = screenDriver.fetchTemplate();
                            if (templates == null || templates.isEmpty()) {
                                statusLabel.setText("Ballot not loaded yet.");
                                cardReader.ejectCard();
                            } else {
                                displayVoterTemplates(stage, templates, 0);
                            }
                        }
                    }
                    case UNKNOWN -> {
                        statusLabel.setText("Invalid card. Use A/V followed by 8 digits.");
                        cardReader.ejectCard();
                    }
                }

            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        VBox contentBox = new VBox(15, prompt, cardInput, submitButton, statusLabel);
        return contentBox;
    }


    /**
     * Displays User Interface for Admin Screen.
     * @param stage
     */
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
                status.setText("Voting session closed.");
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
        stage.setScene(new Scene(adminLayout, 800, 700));
    }


    /**
     * Calls the Voter page and displays the Voter screen.
     * @param stage
     * @param templates
     * @param index
     */
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