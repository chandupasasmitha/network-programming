package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

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
    private SocketClient socketClient;
    private String playerName;

    @FXML
    public void initialize() {
        playersList.setItems(players);
    }

    @FXML
    public void onJoin(ActionEvent e) {
        playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            statusLabel.setText("Enter a name");
            return;
        }

        statusLabel.setText("Connecting...");
        socketClient = new SocketClient();

        try {
            // Connect and provide a callback for server messages
            socketClient.connect("localhost", 5000, playerName, this::handleServerMessage);

            // Request the full current lobby list immediately after connecting
            socketClient.requestLobbyList();

            statusLabel.setText("Connected as " + playerName);
            joinButton.setDisable(true);
        } catch (Exception ex) {
            statusLabel.setText("Connection failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleServerMessage(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("Player joined:")) {
                String joined = message.replace("Player joined:", "").trim();
                if (!players.contains(joined)) {
                    players.add(joined);
                }
            } else if (message.startsWith("Player left:")) {
                String left = message.replace("Player left:", "").trim();
                players.remove(left);
            } else if (message.startsWith("LobbyList:")) {
                // Full lobby list from server
                String listData = message.replace("LobbyList:", "").trim();
                String[] allPlayers = listData.split(",");
                players.clear();
                for (String p : allPlayers) {
                    if (!p.isEmpty()) players.add(p.trim());
                }
            } else {
                System.out.println("Server message: " + message);
            }
        });
    }
}
