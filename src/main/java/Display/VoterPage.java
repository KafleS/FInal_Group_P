package Display;

import Hardwares.SDCards.SDCard;
import Client.CardInsertPage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Pos;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VoterPage {
    private final Scene scene;
    private final Button previousButton;
    private final Button submitButton;
    private final Button nextButton;
    private final Button ejectButton = new Button("Eject Card");
    private static final Map<String, VoteData> allSelectedVotes = new HashMap<>();
    private final Template template;
    private final Stage stage;

    public VoterPage(Template t, Stage stage) {
        this.template = t;
        this.stage = stage;

        Label title = new Label(t.getTitle());
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label description = new Label(t.getDescription());
        description.setAlignment(Pos.CENTER);
        description.setStyle("-fx-font-size: 18px;");
        description.setWrapText(true);
        description.setPrefWidth(600);

        Label instructions = new Label(t.getInstructions());
        instructions.setAlignment(Pos.CENTER);
        instructions.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox optionsBox = new VBox();
        optionsBox.setAlignment(Pos.CENTER);
        for (String option : t.getQuestionData().getOptions()) {
            Button button = new Button(option);
            button.setMinSize(600, 100);
            button.setStyle("-fx-font-size: 18px;");
            button.setOnAction(e -> {
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

        this.previousButton = createNavButton("\u2190 Previous");
        this.submitButton = createNavButton("Submit \u2714");
        this.nextButton = createNavButton("Next \u2192");

        Label confirmation = new Label("");
        confirmation.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

        HBox navigationButtons = new HBox(0, previousButton, submitButton, nextButton);
        navigationButtons.setAlignment(Pos.BOTTOM_CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox mainContent = new VBox(0, title, description, instructions, optionsBox, confirmation, spacer, navigationButtons);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxWidth(600);
        mainContent.setMinHeight(800);

        scene = new Scene(mainContent, 600, 800);

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
                new CardInsertPage().start(stage);
               //stage.setScene(new CardInsertPage(stage).getScene());
            });
        });


    }

    private Button createNavButton(String label) {
        Button button = new Button(label);
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        setDimensionsOnButton(button);
        return button;
    }

    private static class VoteData {
        String id, title, description, option;
        VoteData(String id, String title, String description, String option) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.option = option;
        }
    }

    private void setDimensionsOnButton(Button button) {
        button.setMinSize(200, 80);
        button.setMaxSize(200, 80);
    }

    private void submitVote() {
        if (allSelectedVotes.isEmpty()) {
            System.out.println("No selections made.");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            SDCard readCard1 = new SDCard(1, SDCard.Operation.read);
            SDCard readCard2 = new SDCard(2, SDCard.Operation.read);
            ArrayNode root1 = mapper.createArrayNode();
            ArrayNode root2 = mapper.createArrayNode();

            String json1 = String.join("\n", readCard1.read());
            if (!json1.isBlank()) root1 = (ArrayNode) mapper.readTree(json1);

            String json2 = String.join("\n", readCard2.read());
            if (!json2.isBlank()) root2 = (ArrayNode) mapper.readTree(json2);

            for (String id : allSelectedVotes.keySet()) {
                for (int i = root1.size() - 1; i >= 0; i--) {
                    if (root1.get(i).get("id").asText().equals(id)) root1.remove(i);
                }
                for (int i = root2.size() - 1; i >= 0; i--) {
                    if (root2.get(i).get("id").asText().equals(id)) root2.remove(i);
                }
            }

            for (String id : allSelectedVotes.keySet()) {
                VoteData voteData = allSelectedVotes.get(id);
                ObjectNode vote = mapper.createObjectNode();
                vote.put("id", voteData.id);
                vote.put("title", voteData.title);
                vote.put("description", voteData.description);
                vote.put("selectedOption", voteData.option);
                root1.add(vote);
                root2.add(vote.deepCopy());
            }

            new SDCard(1, SDCard.Operation.overwrite)
                    .overwrite(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root1));
            new SDCard(2, SDCard.Operation.overwrite)
                    .overwrite(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root2));

            System.out.println("All votes saved.");
            allSelectedVotes.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() { return scene; }
    public Button getPreviousButton() { return previousButton; }
    public Button getSubmitButton() { return submitButton; }
    public Button getNextButton() { return nextButton; }
}