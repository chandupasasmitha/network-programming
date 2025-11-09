package client;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.application.Platform;

public class LobbyController {

    @FXML
    private ListView<String> playersList;
    @FXML
    private Button joinButton;
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

    public void updatePlayerList(java.util.List<String> updated) {
        Platform.runLater(() -> players.setAll(updated));
    }
}
