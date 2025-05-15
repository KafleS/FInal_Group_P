
package Client;

import Hardwares.Screens.ScreenDriver;
import Managers.VotingManager;
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
import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class Screen extends Application {
    private Stage stage;
    private Template template;
    private Listener listener;
    private boolean failed = false;
    private boolean ready = true;
    private VBox ballotBox;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isOn = false;
    private static boolean status = false;
    private ScreenDriver screenDriver = new ScreenDriver();
    private List<Template> templates;
    private int currentTemplateIndex = 0;



    private void playSceneTransition(Node oldNode, Scene newScene) {
        RotateTransition rotateOut = new RotateTransition(Duration.millis(400), oldNode);
        rotateOut.setAxis(javafx.geometry.Point3D.ZERO.add(0, 1, 0));
        rotateOut.setFromAngle(0);
        rotateOut.setToAngle(90);
        rotateOut.setInterpolator(Interpolator.EASE_IN);

        rotateOut.setOnFinished(event -> {
            stage.setScene(newScene);
            Node root = newScene.getRoot();
            root.setRotationAxis(javafx.geometry.Point3D.ZERO.add(0, 1, 0));
            root.setRotate(-90);

            RotateTransition rotateIn = new RotateTransition(Duration.millis(400), root);
            rotateIn.setFromAngle(-90);
            rotateIn.setToAngle(0);
            rotateIn.setInterpolator(Interpolator.EASE_OUT);
            rotateIn.play();
        });

        rotateOut.play();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Socket sock = new Socket("localhost", 12345);
        out = new PrintWriter(sock.getOutputStream(), true);
        in  = new BufferedReader(new InputStreamReader(sock.getInputStream()));

        // listen for failure tag
        new Thread(() -> {
            try {
                String line;
                //new code
                while ((line = in.readLine()) != null) {
                    switch (line) {
                        case "FAILURE", "TURN_OFF" -> Platform.runLater(() -> {
                            stage.close();
                            Platform.exit();
                        });
                        case "SCREEN_FAILURE" -> setFailure(true);
                        case "SCREEN_OK"      -> setFailure(false);
                    }
                }

            } catch (IOException e) {
                Platform.runLater(() -> {
                    stage.close();
                    Platform.exit();
                });
            }
        }).start();
        this.stage = primaryStage;
        resetToCardInsertPage();
    }

    public void screenOff() {
        isOn = false;
        template = null;
        System.out.println("[Screen] Powered OFF.");
    }

    //new code
    public void setFailure(boolean status) {
        this.status = status;
        if (status) System.out.println("[Screen] FAILURE detected!");
    }

    public void resetToCardInsertPage() {
        Platform.runLater(() -> {
            ImageView logo = new ImageView(new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/assets/logo.png"))));
            logo.setFitWidth(500);
            logo.setPreserveRatio(true);

            VBox root = new VBox(20);
            root.setStyle("-fx-background-color: #003366;");
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.TOP_CENTER);

            Label instruction = new Label("Insert Card (e.g., O12345678 or V12345678):");
            instruction.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

            TextField cardInput = new TextField();
            cardInput.setMaxWidth(320);
            cardInput.setPromptText("Enter card ID...");
            cardInput.setStyle("-fx-font-size: 14px;");

            Button submitButton = new Button("Submit");
            submitButton.setStyle("-fx-font-size: 16px;");
            submitButton.setPrefWidth(100);

            ballotBox = new VBox(10);
            ballotBox.setAlignment(Pos.CENTER);

            submitButton.setOnAction(e -> {
                String cardId = cardInput.getText().trim();
                System.out.println("[Screen]  Submit clicked: " + cardId);
                ballotBox.getChildren().clear();

                if (cardId.isEmpty()) {
                    Label warning = new Label(" Card ID cannot be empty.");
                    warning.setStyle("-fx-text-fill: yellow; -fx-font-size: 14px;");
                    ballotBox.getChildren().add(warning);
                    return;
                }

                new Thread(() -> {
                    try {
                        if (socket == null || socket.isClosed()) {
                            System.out.println("[Screen]  Connecting to server...");
                            socket = new Socket("localhost", 9999);
                            System.out.println("[Screen]  Socket connected");

                            listener = new Listener(socket, this);
                            Thread listenerThread = new Thread(listener);
                            listenerThread.start();
                            System.out.println("[Screen] Listener thread started");
                        }

                        listener.sendCardId(cardId);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            Label error = new Label("Could not connect: " + ex.getMessage());
                            error.setStyle("-fx-text-fill: red;");
                            ballotBox.getChildren().add(error);
                        });
                    }
                }).start();
            });

            root.getChildren().addAll(logo, instruction, cardInput, submitButton, ballotBox);
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("Voting Machine");
            stage.show();
        });
    }

    public void receiveTemplate(Template template) {
        System.out.println("Display.Template received");
        this.template = template;
        ready = false;

        Platform.runLater(() -> {
            VotingMachinePage newPage = new VotingMachinePage(template);

            newPage.getPreviousButton().setOnAction(event -> {
                template.getPreviousButton().pressButton();
                ready = true;
            });

            newPage.getNextButton().setOnAction(event -> {
                if (template.getQuestionData().getSelection() == -1) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an option before proceeding.");
                    alert.showAndWait();
                } else {
                    template.getNextButton().pressButton();
                    ready = true;
                }
            });

            newPage.getSubmitButton().setOnAction(event -> {
                if (template.getQuestionData().getSelection() == -1) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an option before submitting.");
                    alert.showAndWait();
                } else {
                    newPage.submitVote();
                    ready = true;

                    ImageView logo = new ImageView(new Image(
                            Objects.requireNonNull(getClass().getResourceAsStream("/assets/logo.png"))
                    ));
                    logo.setFitWidth(400);
                    logo.setPreserveRatio(true);

                    // --- End Screen Layout ---
                    Label message = new Label("Voting completed.\n💳 Card ejected.");
                    message.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
                    VBox endBox = new VBox(20, logo, message);
                    endBox.setAlignment(Pos.CENTER);
                    endBox.setPadding(new Insets(30));
                    endBox.setStyle("-fx-background-color: #003366;");

                    Scene endScene = new Scene(endBox, 1000, 700);
                    stage.setScene(endScene);

                    new Thread(() -> {
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(this::resetToCardInsertPage);
                    }).start();
                }
            });

            playSceneTransition(stage.getScene().getRoot(), newPage.getScene());
        });
    }

    public boolean isReady() {
        return ready;
    }

    public boolean failure() {
        return failed;
    }

    public Template returnTemplate() {
        return template;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
