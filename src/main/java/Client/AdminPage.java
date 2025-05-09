package Client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPage {

    private final Scene scene;

    public AdminPage(Stage stage) {
        // Create buttons
        Button openVotingButton = new Button("Open Voting Session");
        Button closeVotingButton = new Button("Close Voting Session");

        openVotingButton.setStyle("-fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        closeVotingButton.setStyle("-fx-font-size: 16px; -fx-background-color: #f44336; -fx-text-fill: white;");

        openVotingButton.setMinSize(300, 80);
        closeVotingButton.setMinSize(300, 80);

        // Add event handlers
        openVotingButton.setOnAction(e -> {
            System.out.println("[Admin] ✅ Voting session opened.");
            // You can trigger your backend method here to actually open voting
        });

        closeVotingButton.setOnAction(e -> {
            System.out.println("[Admin] 🛑 Voting session closed.");
            // Trigger the backend method to close voting session
        });

        // Layout
        VBox layout = new VBox(30, openVotingButton, closeVotingButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));

        scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Admin Control Panel");
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}
