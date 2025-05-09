
package Client;

import Hardwares.SDCards.VoteRecording;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VotingMachinePage {

    private final Scene scene;
    private final Button previousButton;
    private final Button submitButton;
    private VBox mainContent;
    private final Button nextButton;
    private final Button ejectButton = new Button("Eject Card");
    private final Template template;
    private static final Map<String, VoteData> allSelectedVotes = new HashMap<>();

    public VotingMachinePage(Template t) {

        this.template = t;

        ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/assets/logo2.png"))));
        logo.setFitWidth(500);
        logo.setPreserveRatio(true);


        Label title = new Label(t.getTitle());
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");
        title.setMinHeight(40);
        title.setMaxWidth(600);


        Label instructions = new Label(t.getInstructions());
        instructions.setAlignment(Pos.CENTER);
        instructions.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        instructions.setPrefWidth(600);


        VBox optionsBox = new VBox(10);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPrefWidth(500);

        String[] options = t.getQuestionData().getOptions();
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            Button button = new Button(option);
            button.setMinSize(500, 60);
            button.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            int finalI = i;
            button.setOnAction(e -> {
                template.getQuestionData().setSelectionIndex(finalI);
                VoteData vote = new VoteData(
                        String.valueOf(template.getId()),
                        template.getTitle(),
                        template.getDescription(),
                        option
                );
                allSelectedVotes.put(vote.id, vote);
                System.out.println("Saved: " + vote.id + " → " + vote.option);
            });

            optionsBox.getChildren().add(button);
        }

        // --- Navigation Buttons ---
        previousButton = new Button("← Previous");
        previousButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        setDimensionsOnButton(previousButton, 150, 60);

        submitButton = new Button("Submit ✔");
        submitButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        setDimensionsOnButton(submitButton, 150, 60);

        nextButton = new Button("Next →");
        nextButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        setDimensionsOnButton(nextButton, 150, 60);

        HBox navigationButtons = new HBox(20, previousButton, submitButton, nextButton);
        navigationButtons.setAlignment(Pos.CENTER);
        Label confirmation = new Label("");
        confirmation.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");


        previousButton.setDisable(!t.getPreviousButton().getActive());
        submitButton.setDisable(!t.getSubmitButton().getActive());
        nextButton.setDisable(!t.getNextButton().getActive());


        // --- Main Layout ---
        mainContent = new VBox(20, logo, title, instructions, optionsBox, confirmation,navigationButtons);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: #003366;");
        mainContent.setPadding(new Insets(30));

        // --- Final Scene ---
        scene = new Scene(mainContent, 1000, 700);


        submitButton.setOnAction(e -> {
            submitVote();
            confirmation.setText("Vote submitted successfully.");
            previousButton.setVisible(false);
            submitButton.setVisible(false);
            nextButton.setVisible(false);
            ejectButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            setDimensionsOnButton(ejectButton);
            ((VBox) scene.getRoot()).getChildren().add(ejectButton);
            ejectButton.setOnAction(ev -> {
                try {
                    //screen.resetToCardInsertPage();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        });


    }


    public class VoteData {
        public String id;
        public String title;
        public String description;
        public String option;
        VoteData(String id, String title, String description, String option) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.option = option;
        }
    }

    private void setDimensionsOnButton(Button button, int width, int height) {
        button.setMinSize(width, height);
        button.setMaxSize(width, height);
    }

    void submitVote() {
        if (allSelectedVotes.isEmpty()) {
            System.out.println("No selections made.");
            return;
        }

        try {
            VoteRecording voteRecording = new VoteRecording();
            System.out.println("submitting votes " + allSelectedVotes.values());
            voteRecording.recordVotes(allSelectedVotes.values());
            allSelectedVotes.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setDimensionsOnButton(Button button) {
        button.setMinSize(200, 80);
        button.setMaxSize(200, 80);
    }

    public Scene getScene() { return scene; }
    public Button getPreviousButton() { return previousButton; }
    public Button getSubmitButton() { return submitButton; }
    public Button getNextButton() { return nextButton; }
}