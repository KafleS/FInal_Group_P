package TextEditor;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * Initial foundation for editor, need to se
 */
public class BallotEditor extends Application{

    private VBox preambleBox;
    private VBox propositionsBox;
    private ObservableList<PropositionPane> propositionPanes = FXCollections.observableArrayList();
    private PreambleBuilder preambleBuilder = new PreambleBuilder();

    private int propLimit = 5;

    public void start(Stage primaryStage) {

        preambleBox = initPreamble();

        // Filler for now
        propositionsBox = new VBox(10);
        propositionsBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(propositionsBox);
        scrollPane.setFitToWidth(true);

        // Tweak button contents/add image?
        Button addPropositionButton = new Button("Add Proposition");
        addPropositionButton.setOnAction(e -> {
            if (propositionPanes.size() < propLimit) addProposition();
        });

        Button finalizeBallotButton = new Button("Finalize");
        finalizeBallotButton.setOnAction(e -> {
            if (finalizeBallot()) primaryStage.close();
        });

        // Similarly as removeProposition button, shift Finalize to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox controls = new HBox(10, addPropositionButton, spacer,finalizeBallotButton);
        controls.setPadding(new Insets(10));

        // Use this to default to no propositions, can change to default to a single
        VBox root = new VBox(preambleBox, scrollPane, controls);

        // Add preamble as well
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("Gruia-Catalin and Co. Ballot Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox initPreamble() {
        String regex = "[a-zA-Z0-9.() ]";
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        TextField ballotTitle = new TextField();
        ballotTitle.setPromptText("Ballot Title");
        ballotTitle.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) preambleBuilder.setTitle(ballotTitle.getText());
        });
        setupTextFormatter(ballotTitle, 50, regex);

        TextField county = new TextField();
        county.setPromptText("County");
        county.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) preambleBuilder.setCounty(county.getText());
        });
        setupTextFormatter(county, 50, regex);

        TextField state = new TextField();
        state.setPromptText("State");
        state.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) preambleBuilder.setState(state.getText());
        });
        setupTextFormatter(state, 50, regex);

        TextField ballotID = new TextField();
        ballotID.setPromptText("Ballot ID");
        ballotID.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) preambleBuilder.setID(ballotID.getText());
        });
        setupTextFormatter(ballotID, 50, regex);

        DatePicker endDate = new DatePicker();
        endDate.setPromptText("End Date (MM/DD/YYYY)");
        setupDatePicker(endDate);

        box.getChildren().addAll(ballotTitle, county, state, ballotID, endDate);
        return box;

    }

    /**
     *
     * @param textField
     * @param maxCharacters
     */
    private void setupTextFormatter(TextField textField, int maxCharacters, String regex) {
        textField.setTextFormatter(new TextFormatter<String >( change -> {
            String newText = change.getControlNewText();
            if (newText.matches(regex + "{0," + maxCharacters + "}")) return change;
            else return null;
        }));

    }

    private void setupDatePicker(DatePicker endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Handles manual date inputs
        endDate.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) return formatter.format(date);
                else return "";

            }

            @Override
            public LocalDate fromString(String str) {
                if (str == null || str.isEmpty()) {
                    endDate.getEditor().setStyle("-fx-border-color: red;");
                    return null;
                }

                try {
                    LocalDate parsedDate = LocalDate.parse(str, formatter);
                    if (parsedDate.isBefore(LocalDate.now())) {
                        endDate.getEditor().setStyle("-fx-border-color: red;");
                        return null;
                    }

                    endDate.setStyle("");
                    return parsedDate;
                } catch (DateTimeParseException e) {
                    endDate.getEditor().setStyle("-fx-border-color: red;");
                    return null;
                }
            }
        });

        // Changes invalid dates in calendar pop out
        endDate.setDayCellFactory( picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now().plusDays(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb");
                }
            }
        });

        // Handles the listener and checks changes before updating preamble
        endDate.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String dateString = endDate.getEditor().getText();
                LocalDate dateValue = endDate.getConverter().fromString(dateString);
                if (dateValue != null) {
                    endDate.setValue(dateValue);
                    // TODO: We can pass the String to preamble in either format
                    // Option A: Passes it as the value of the date so likely to match Java's internal format
                    preambleBuilder.setEndDate(endDate.getValue().toString());
                    //Option B: Passes in the MM/DD/YYYY format used in the US and shown on the editor
                    //preambleBuilder.setEndDate(dateString);

                }
                else {
                    endDate.getEditor().setText("");
                    endDate.getEditor().setStyle("-fx-background-color: #ffc0cb");
                }
            }
        });


    }

    /**
     * adds a blank proposition
     */
    private void addProposition() {
        int index = propositionPanes.size();
        PropositionPane pane = new PropositionPane(index, this::removeProposition);
        propositionPanes.add(pane);
        propositionsBox.getChildren().add(pane);
    }

    /**
     * Removes the provided pane from the gui and list
     * @param pane
     */
    private void removeProposition(PropositionPane pane) {
        propositionsBox.getChildren().remove(pane);
        propositionPanes.remove(pane);

        for (int i = 0; i < propositionPanes.size(); i++) {
            propositionPanes.get(i).updateIndex(i);
        }
    }

    public static boolean validateTextField(TextField textField) {
        boolean valid = true;
        String text = textField.getText().trim();
        if (text.isEmpty() && !text.matches("[a-zA-Z0-9.() $]+")) {
            textField.setStyle("-fx-background-color: #ffc0cb");
            valid = false;
        }
        else textField.setStyle("");
        return valid;
    }

    private boolean validatePreamble() {
        boolean valid = true;

        // Iterate through all preamble nodes, return false if any are invalid
        for (Node node : preambleBox.getChildren()) {
            if (node instanceof TextField textField) {
                if (!validateTextField(textField)) valid = false;
            }
            else if (node instanceof DatePicker datePicker) {
                LocalDate date = datePicker.getValue();
                if (date == null || date.isBefore(LocalDate.now().plusDays(1))) {
                    datePicker.getEditor().setStyle("-fx-background-color: #ffc0cb");
                    valid = false;
                }
                else datePicker.getEditor().setStyle("");
            }
        }

        return valid;
    }

    private boolean validatePropositions() {
        boolean valid = true;
         for (PropositionPane pane : propositionPanes) {
             if (!pane.validate()) valid = false;
         }
        return valid;
    }

    private boolean validateBallot() {
        boolean valid = true;
        if (!validatePropositions()) valid = false;
        if (!validatePreamble()) valid = false;
        return valid;

    }

    /**
     * Finalizes ballot and generates JSON file from it using Jackson
     */
    private boolean finalizeBallot() {
        if (!validateBallot()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please provide valid entries in the highlighted fields.");
            alert.showAndWait();
            return false;
        }

        // Make list from proposition panes
        ArrayList<PropositionBuilder> propositionList = new ArrayList<>();
        for (PropositionPane pane : propositionPanes) {
            propositionList.add(pane.toProposition());
        }

        BallotBuilder ballot = new BallotBuilder(preambleBuilder, propositionList);
        ballot.buildJSON();

        return true;
    }

    public static void main(String[] args) {
        launch(args);
    }


}

class PropositionPane extends VBox {
    private int index;
    private TextField propositionTitle = new TextField();
    private TextField propositionDescription = new TextField();
    private ComboBox<Integer> maxSelections = new ComboBox<>();
    private List<HBox> optionBoxes = new ArrayList<>();
    private List<TextField> optionFields = new ArrayList<>();
    private VBox optionsBox = new VBox(5);
    private PropositionBuilder propositionBuilder = new PropositionBuilder();

    public PropositionPane(int index, java.util.function.Consumer<PropositionPane> onDelete) {
        String regex = "^[a-zA-Z0-9.() ]";
        // allow $ and % symbols for the description
        String descRegex = "^[a-zA-Z0-9,.() $%]";
        this.index = index;
        setPadding(new Insets(10));
        setSpacing(8);
        setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: #f0f0f0;");

        Label title = new Label("Proposition ID: " + (index));

        // Appearance is actually not that bad
        Button deleteButton = new Button("X");
        deleteButton.setOnAction(e -> onDelete.accept(this));

        // Spacer to keep it on the right of the panel
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, title, spacer, deleteButton);

        propositionTitle.setPromptText("Proposition");
        propositionTitle.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) propositionBuilder.setTitle(propositionTitle.getText());
        });
        setupTextFormatter(propositionTitle, 50, regex);

        propositionDescription.setPromptText("Description");
        propositionDescription.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) propositionBuilder.setDescription(propositionDescription.getText());
        });
        setupTextFormatter(propositionDescription, 100, descRegex);

        maxSelections.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
        maxSelections.setValue(1);
        maxSelections.valueProperty().addListener((obs, oldVal, newVal) -> {
            propositionBuilder.setMaxSelections(newVal);
        });

        Label maxSelectionsLabel = new Label("Maximum Selections Allowed:");
        HBox selectionLimitBox = new HBox(10, maxSelectionsLabel, maxSelections);

        Button addOptionButton = new Button("Add Option");
        addOptionButton.setOnAction(e -> addOption());

        //Default with at least 2 blank options
        addOption();
        addOption();

        getChildren().addAll(
                header,
                propositionTitle,
                propositionDescription,
                selectionLimitBox,
                optionsBox,
                addOptionButton);
    }

    /**
     *
     * @param textField
     * @param maxCharacters
     */
    private void setupTextFormatter(TextField textField, int maxCharacters, String regex) {
        textField.setTextFormatter(new TextFormatter<String >( change -> {
            String newText = change.getControlNewText();
            if (newText.matches(regex + "{0," + maxCharacters + "}")) return change;
            else return null;
        }));

    }

    /**
     * Adds new blank option field only if optionBox has fewer than 5 options
     */
    private void addOption() {
        String regex = "^[a-zA-Z0-9.() ]";
        if (optionFields.size() < 5) {
            TextField optionField = new TextField();
            optionField.setPromptText("Option");
            optionField.prefWidthProperty().bind(this.widthProperty().divide(2));
            setupTextFormatter(optionField, 50, regex);

            Button deleteButton = new Button("X");
            deleteButton.setOnAction(e -> {
                if (optionFields.size() > 2) {
                    deleteOption(optionField);
                }
            });

            HBox optionBox = new HBox(10, optionField, deleteButton);
            optionsBox.getChildren().add(optionBox);

            optionBoxes.add(optionBox);
            optionFields.add(optionField);

            updateBuilderOptions();
        }
    }

    /**
     *
     * @param toRemove
     */
    private void deleteOption(TextField toRemove) {
        for (int i = 0; i < optionBoxes.size(); i++) {
            HBox hbox = optionBoxes.get(i);
            if (hbox.getChildren().contains(toRemove)) {
                optionsBox.getChildren().remove(hbox);
                optionBoxes.remove(hbox);
                optionFields.remove(toRemove);
                break;
            }
        }
        updateBuilderOptions();
    }

    /**
     *
     */
    private void updateBuilderOptions() {
        List<String> currentOptions = optionFields.stream()
                .map(TextField::getText)
                .toList();
        propositionBuilder.setOptions(currentOptions);
    }




    /**
     *
     */
    public void updateIndex(int newIndex) {
        this.index = newIndex;
        HBox header = (HBox) getChildren().get(0);
        Label titleLabel = (Label) header.getChildren().get(0);
        titleLabel.setText("Proposition ID: " + (index));
    }

    /**
     *
     * @return
     */
    public boolean validate() {
        boolean valid = true;
        if (!BallotEditor.validateTextField(propositionTitle)) valid = false;
        if (!BallotEditor.validateTextField(propositionDescription)) valid = false;
        for (TextField option : optionFields) {
            if (!BallotEditor.validateTextField(option)) valid = false;
        }

        return valid;
    }


    /**
     * Generates a Proposition from the filled fields
     * @return
     */
    public PropositionBuilder toProposition() {
        int id = this.index;
        String title = propositionTitle.getText();
        String description = propositionDescription.getText();
        List<String> options = new ArrayList<>();

        for (HBox box : optionBoxes) {
            for (Node node : box.getChildren()) {
                if (node instanceof TextField textField) { options.add(textField.getText()); }
            }
        }

        return new PropositionBuilder(
                id,
                title,
                description,
                options,
                maxSelections.getValue()
        );
    }

}