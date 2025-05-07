package Client;

import Hardwares.Screens.ScreenDriver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Screen extends Application {

    private boolean exitReady = false;
    private Template currentTemplate = null;
    private VBox root;
    private VBox ballotBox;

    @Override
    public void start(Stage primaryStage) {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f0f8ff;");

        Label instruction = new Label("Insert Card (e.g., V12345678):");
        TextField cardInput = new TextField();
        cardInput.setPromptText("Enter card ID...");
        Button submitButton = new Button("Submit");
        ballotBox = new VBox(10);

        submitButton.setOnAction(e -> {
            String cardId = cardInput.getText().trim();
            ballotBox.getChildren().clear();
            root.getChildren().removeIf(node -> node instanceof VBox && node != ballotBox);

            if (cardId.isEmpty()) {
                ballotBox.getChildren().add(new Label("Card ID cannot be empty."));
                return;
            }

            new Thread(() -> {
                SocketHandler.sendCardInfoToCardReader("CRreader:" + cardId);

                if (cardId.toUpperCase().startsWith("V")) {
                    Template presidentTemplate = new Template(
                            0,
                            "President",
                            "USA 2025 President",
                            "Please select one:",
                            new ButtonData(false),
                            new ButtonData(true),
                            new ButtonData(true),
                            new QuestionInfo(new String[]{"Trump", "Kamala"})
                    );

                    Template vpTemplate = new Template(
                            1,
                            "Vice President",
                            "USA vice president",
                            "Please select one:",
                            new ButtonData(true),
                            new ButtonData(true),
                            new ButtonData(false),
                            new QuestionInfo(new String[]{"JD Vance", "Roman"})
                    );

                    this.currentTemplate = presidentTemplate;

                    Platform.runLater(() -> {
                        presentTemplate(presidentTemplate);

                        new Thread(() -> {
                            try {
                                Thread.sleep(6000);
                                Platform.runLater(() -> presentTemplate(vpTemplate));
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }).start();
                    });
                } else {
                    Platform.runLater(() -> ballotBox.getChildren().add(new Label("Non-voter card detected.")));
                }
            }).start();
        });

        root.getChildren().addAll(instruction, cardInput, submitButton, new Separator(), ballotBox);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Voting Screen");
        primaryStage.show();
    }

    /**
     * Shows the given template on the screen, replacing the current view.
     */
    public void presentTemplate(Template template) {
        VBox votingPane = new VBox(12);
        votingPane.setAlignment(Pos.CENTER);
        votingPane.setPadding(new Insets(15));
        votingPane.setStyle("-fx-background-color: #e6f2ff;");

        Label title = new Label(template.getTitle());
        title.setFont(Font.font("Verdana", 22));
        Label description = new Label(template.getDescription());
        Label instructions = new Label(template.getInstructions());

        title.setWrapText(true);
        description.setWrapText(true);
        instructions.setWrapText(true);

        votingPane.getChildren().addAll(title, description, instructions);

        ToggleGroup group = new ToggleGroup();
        QuestionInfo qInfo = template.getQuestionData();
        String[] options = qInfo.getOptions();

        for (int i = 0; i < options.length; i++) {
            String opt = options[i];
            RadioButton btn = new RadioButton(opt);
            btn.setToggleGroup(group);
            final int idx = i;
            btn.setOnAction(e -> {
                template.getQuestionData().setSelectionIndex(idx);
            });
            votingPane.getChildren().add(btn);
        }

        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);

        if (template.getSubmitButton().getActive()) {
            Button submit = new Button("Submit");
            submit.setOnAction(e -> {
                template.getSubmitButton().pressButton();
                exitReady = true;
                ballotBox.getChildren().clear();
                ballotBox.getChildren().add(new Label("✅ Vote submitted."));
            });
            buttonsBox.getChildren().add(submit);
        }

        if (template.getNextButton().getActive()) {
            Button next = new Button("Next");
            next.setOnAction(e -> {
                template.getNextButton().pressButton();
                ballotBox.getChildren().clear();
                ballotBox.getChildren().add(new Label("Next pressed (not yet implemented)."));
            });
            buttonsBox.getChildren().add(next);
        }

        if (template.getPreviousButton().getActive()) {
            Button back = new Button("Back");
            back.setOnAction(e -> {
                template.getPreviousButton().pressButton();
                ballotBox.getChildren().clear();
                ballotBox.getChildren().add(new Label("Back pressed (not yet implemented)."));
            });
            buttonsBox.getChildren().add(back);
        }

        votingPane.getChildren().add(buttonsBox);

        Platform.runLater(() -> {
            root.getChildren().clear();
            root.getChildren().add(votingPane);
        });
    }

    public boolean exitReady() {
        return exitReady;
    }

    public Template returnTemplate() {
        return currentTemplate;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
