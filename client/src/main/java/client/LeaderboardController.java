package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Score;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;

public class LeaderboardController {

    @FXML
    private TableView<Score> leaderboardTable;
    @FXML
    private TableColumn<Score, String> medalCol;
    @FXML
    private TableColumn<Score, String> nameCol;
    @FXML
    private TableColumn<Score, Integer> scoreCol;
    @FXML
    private TableColumn<Score, String> streakCol;
    @FXML
    private Label titleLabel;
    @FXML
    private Label gameStatusLabel;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button allTimeBtn;
    @FXML
    private Button backBtn;

    private ObservableList<Score> scores = FXCollections.observableArrayList();
    
    // Socket connection
    private Socket socket;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private volatile boolean running = true;
    
    // Connection details
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    @FXML
    public void initialize() {
        // Set up table columns
        medalCol.setCellValueFactory(new PropertyValueFactory<>("medal"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        streakCol.setCellValueFactory(new PropertyValueFactory<>("streakDisplay"));
        
        leaderboardTable.setItems(scores);

        // Custom row factory for highlighting top 3
        leaderboardTable.setRowFactory(tv -> new TableRow<Score>() {
            @Override
            protected void updateItem(Score item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    // Highlight top 3 players
                    if (item.getRank() == 1) {
                        setStyle("-fx-background-color: #ffd700; -fx-text-fill: black;");
                    } else if (item.getRank() == 2) {
                        setStyle("-fx-background-color: #c0c0c0; -fx-text-fill: black;");
                    } else if (item.getRank() == 3) {
                        setStyle("-fx-background-color: #cd7f32; -fx-text-fill: black;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                    }
                }
            }
        });

        // Add some demo data for testing
        addDemoData();
        
        // Connect to server for real-time updates (optional)
        // connectToServer();
    }

    /**
     * Add demo data for testing the leaderboard UI
     */
    private void addDemoData() {
        scores.add(new Score(1, "Alice", 95, 5));
        scores.add(new Score(2, "Bob", 87, 3));
        scores.add(new Score(3, "Charlie", 82, 2));
        scores.add(new Score(4, "Diana", 76, 1));
        scores.add(new Score(5, "Eve", 71, 0));
        
        gameStatusLabel.setText("🎊 Quiz Complete - Final Results!");
        gameStatusLabel.setStyle("-fx-text-fill: #ffd700;");
    }

    /**
     * Connect to quiz server for real-time leaderboard updates
     */
    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                objOut = new ObjectOutputStream(socket.getOutputStream());
                objIn = new ObjectInputStream(socket.getInputStream());

                Platform.runLater(() -> {
                    gameStatusLabel.setText("✅ Connected to Server");
                    gameStatusLabel.setStyle("-fx-text-fill: #00ff00;");
                });

                // Request initial leaderboard
                objOut.writeObject("GET_LEADERBOARD");
                objOut.flush();

                // Listen for updates
                listenForUpdates();

            } catch (IOException e) {
                Platform.runLater(() -> {
                    gameStatusLabel.setText("❌ Connection Failed - Using Demo Data");
                    gameStatusLabel.setStyle("-fx-text-fill: #ff6600;");
                });
            }
        }).start();
    }

    /**
     * Listen for leaderboard updates from server
     */
    private void listenForUpdates() {
        new Thread(() -> {
            try {
                Object message;
                while (running && (message = objIn.readObject()) != null) {
                    
                    if (message instanceof String) {
                        String msg = (String) message;
                        
                        if (msg.startsWith("LEADERBOARD:")) {
                            // Parse and update leaderboard
                            String data = msg.substring(12); // Remove "LEADERBOARD:"
                            parseAndUpdateLeaderboard(data);
                        } else if (msg.startsWith("GAME_STATUS:")) {
                            String status = msg.substring(12);
                            Platform.runLater(() -> gameStatusLabel.setText(status));
                        } else if (msg.equals("GAME_FINISHED")) {
                            // Game ended, read final leaderboard
                            Object leaderboardData = objIn.readObject();
                            if (leaderboardData instanceof java.util.List) {
                                parseFinalLeaderboard((java.util.List<String>) leaderboardData);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (running) {
                    Platform.runLater(() -> {
                        gameStatusLabel.setText("❌ Disconnected - Showing Cached Data");
                        gameStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    });
                }
            }
        }).start();
    }

    /**
     * Parse leaderboard data and update UI
     * Format: "PlayerName:Score:Streak,PlayerName:Score:Streak,..."
     */
    private void parseAndUpdateLeaderboard(String data) {
        Platform.runLater(() -> {
            scores.clear();

            if (data == null || data.trim().isEmpty()) {
                return;
            }

            String[] entries = data.split(",");
            int rank = 1;

            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length >= 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    int streak = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

                    scores.add(new Score(rank, name, score, streak));
                    rank++;
                }
            }

            // Update game status
            gameStatusLabel.setText("🎮 Live Game - " + scores.size() + " Players");
        });
    }

    /**
     * Parse final leaderboard when game finishes
     * Format: ["1. PlayerName - 100 points", "2. PlayerName - 80 points", ...]
     */
    private void parseFinalLeaderboard(java.util.List<String> leaderboardStrings) {
        Platform.runLater(() -> {
            scores.clear();
            
            int rank = 1;
            for (String entry : leaderboardStrings) {
                // Parse "1. PlayerName - 100 points"
                try {
                    String[] parts = entry.split(" - ");
                    if (parts.length >= 2) {
                        String namePart = parts[0].substring(parts[0].indexOf('.') + 2); // Remove "1. "
                        String scorePart = parts[1].replace(" points", "");
                        
                        int score = Integer.parseInt(scorePart);
                        scores.add(new Score(rank, namePart, score, 0)); // Streak not provided in final
                        rank++;
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing leaderboard entry: " + entry);
                }
            }
            
            // Update game status
            gameStatusLabel.setText("🎊 Quiz Complete - Final Results!");
            gameStatusLabel.setStyle("-fx-text-fill: #ffd700;");
        });
    }

    @FXML
    private void onRefreshClicked() {
        if (objOut != null) {
            try {
                objOut.writeObject("GET_LEADERBOARD");
                objOut.flush();
            } catch (IOException e) {
                showAlert("Error", "Failed to request leaderboard update");
            }
        } else {
            // Refresh demo data
            addDemoData();
            showAlert("Refresh", "Demo data refreshed!");
        }
    }

    @FXML
    private void onAllTimeClicked() {
        if (objOut != null) {
            try {
                objOut.writeObject("GET_ALLTIME_LEADERBOARD");
                objOut.flush();
            } catch (IOException e) {
                showAlert("Error", "Failed to request all-time stats");
            }
        } else {
            showAlert("All-Time Stats", "Feature requires server connection. Currently showing demo data.");
        }
    }

    @FXML
    private void onBackClicked() {
        try {
            // Close connection
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            
            // Navigate back to lobby
            Parent lobbyRoot = FXMLLoader.load(getClass().getResource("/fxml/Lobby.fxml"));
            Scene lobbyScene = new Scene(lobbyRoot);
            lobbyScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(lobbyScene);
            stage.setTitle("Multiplayer Quiz Game - Lobby");
            
        } catch (Exception e) {
            showAlert("Navigation Error", "Could not return to lobby: " + e.getMessage());
        }
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clean up when controller is destroyed
     */
    public void cleanup() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Static method to show leaderboard when quiz ends
     */
    public static void showFinalLeaderboard(Stage currentStage) {
        try {
            Parent leaderboardRoot = FXMLLoader.load(LeaderboardController.class.getResource("/fxml/Leaderboard.fxml"));
            Scene leaderboardScene = new Scene(leaderboardRoot);
            leaderboardScene.getStylesheets().add(LeaderboardController.class.getResource("/css/styles.css").toExternalForm());
            
            currentStage.setScene(leaderboardScene);
            currentStage.setTitle("Multiplayer Quiz Game - Final Results");
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not show leaderboard: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
