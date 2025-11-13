package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

public class LobbyController {

    @FXML
    private ListView<String> playersList;
    @FXML
    private Button joinButton;
    @FXML
    private Button leaderboardButton;
    @FXML
    private TextField nameField;
    @FXML
    private Label statusLabel;
    private ObservableList<String> players = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        playersList.setItems(players);
        // demo placeholder entries
        players.addAll("Alice", "Bob");
    }

    @FXML
    public void onJoin(ActionEvent e) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("Enter a name");
            return;
        }
        statusLabel.setText("Joining as " + name + "...");
        // TODO: implement socket connect and registration with server
        if (!players.contains(name)) {
            players.add(name);
        }
        statusLabel.setText("Joined");
    }

    @FXML
    public void onOpenLeaderboard(ActionEvent e) {
        try {
            Parent leaderboardRoot = FXMLLoader.load(getClass().getResource("/fxml/Leaderboard.fxml"));
            Scene leaderboardScene = new Scene(leaderboardRoot);
            leaderboardScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) leaderboardButton.getScene().getWindow();
            stage.setScene(leaderboardScene);
            stage.setTitle("Multiplayer Quiz Game - Leaderboard");
            
        } catch (IOException ex) {
            showAlert("Navigation Error", "Could not open leaderboard: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updatePlayerList(java.util.List<String> updated) {
        Platform.runLater(() -> players.setAll(updated));
    }
}
