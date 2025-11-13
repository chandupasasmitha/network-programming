package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * JavaFX Leaderboard Screen
 * Displays real-time player rankings with scores and streaks
 * Member 4: Score Calculation & Leaderboard
 */
public class LeaderboardScreen {

    private Stage stage;
    private TableView<PlayerScore> leaderboardTable;
    private ObservableList<PlayerScore> playerData;
    private Label titleLabel;
    private Label gameStatusLabel;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean running = true;

    // Connection details
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    public LeaderboardScreen(Stage stage) {
        this.stage = stage;
        this.playerData = FXCollections.observableArrayList();
    }

    /**
     * Build and display the leaderboard UI
     */
    public void show() {
        // Main container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
        root.setPadding(new Insets(20));

        // Top section - Title
        VBox topSection = createTopSection();
        root.setTop(topSection);

        // Center - Leaderboard Table
        VBox centerSection = createLeaderboardTable();
        root.setCenter(centerSection);

        // Bottom - Buttons
        HBox bottomSection = createBottomSection();
        root.setBottom(bottomSection);

        // Create scene
        Scene scene = new Scene(root, 700, 600);
        stage.setScene(scene);
        stage.setTitle("Quiz Leaderboard - Real-Time Rankings");
        stage.show();

        // Connect to server and start listening for updates
        connectToServer();
    }

    /**
     * Create top section with title and game status
     */
    private VBox createTopSection() {
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        // Title
        titleLabel = new Label("🏆 LEADERBOARD 🏆");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.web("#ffd700"));

        // Game status
        gameStatusLabel = new Label("Live Rankings");
        gameStatusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gameStatusLabel.setTextFill(Color.web("#00ff00"));

        topBox.getChildren().addAll(titleLabel, gameStatusLabel);
        return topBox;
    }

    /**
     * Create leaderboard table
     */
    private VBox createLeaderboardTable() {
        VBox tableBox = new VBox(10);
        tableBox.setAlignment(Pos.CENTER);
        tableBox.setPadding(new Insets(20));

        // Create table
        leaderboardTable = new TableView<>();
        leaderboardTable.setItems(playerData);
        leaderboardTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        leaderboardTable.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white;");

        // Rank Column
        TableColumn<PlayerScore, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        rankCol.setPrefWidth(80);
        rankCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Medal Column (for top 3)
        TableColumn<PlayerScore, String> medalCol = new TableColumn<>("Medal");
        medalCol.setCellValueFactory(new PropertyValueFactory<>("medal"));
        medalCol.setPrefWidth(80);
        medalCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 24px;");

        // Player Name Column
        TableColumn<PlayerScore, String> nameCol = new TableColumn<>("Player Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Score Column
        TableColumn<PlayerScore, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setPrefWidth(100);
        scoreCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Streak Column
        TableColumn<PlayerScore, String> streakCol = new TableColumn<>("Streak");
        streakCol.setCellValueFactory(new PropertyValueFactory<>("streakDisplay"));
        streakCol.setPrefWidth(100);
        streakCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 16px;");

        // Add columns to table
        leaderboardTable.getColumns().addAll(rankCol, medalCol, nameCol, scoreCol, streakCol);

        // Custom row factory for styling
        leaderboardTable.setRowFactory(tv -> new TableRow<PlayerScore>() {
            @Override
            protected void updateItem(PlayerScore item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    // Highlight top 3
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

        tableBox.getChildren().add(leaderboardTable);
        return tableBox;
    }

    /**
     * Create bottom section with buttons
     */
    private HBox createBottomSection() {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        // Refresh button
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        refreshBtn.setStyle("-fx-background-color: #00ff00; -fx-text-fill: black;");
        refreshBtn.setOnAction(e -> requestLeaderboardUpdate());

        // View All-Time Stats button
        Button allTimeBtn = new Button("📊 All-Time Stats");
        allTimeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        allTimeBtn.setStyle("-fx-background-color: #4169e1; -fx-text-fill: white;");
        allTimeBtn.setOnAction(e -> showAllTimeLeaderboard());

        // Exit button
        Button exitBtn = new Button("❌ Exit");
        exitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        exitBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        exitBtn.setOnAction(e -> {
            running = false;
            disconnect();
            stage.close();
        });

        buttonBox.getChildren().addAll(refreshBtn, allTimeBtn, exitBtn);
        return buttonBox;
    }

    /**
     * Connect to quiz server
     */
    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                Platform.runLater(() -> {
                    gameStatusLabel.setText("✅ Connected to Server");
                    gameStatusLabel.setTextFill(Color.web("#00ff00"));
                });

                // Request initial leaderboard
                out.println("GET_LEADERBOARD");

                // Listen for updates
                listenForUpdates();

            } catch (IOException e) {
                Platform.runLater(() -> {
                    gameStatusLabel.setText("❌ Connection Failed");
                    gameStatusLabel.setTextFill(Color.web("#ff0000"));
                    showAlert("Connection Error", "Could not connect to server: " + e.getMessage());
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
                String message;
                while (running && (message = in.readLine()) != null) {
                    final String msg = message;

                    if (msg.startsWith("LEADERBOARD:")) {
                        // Parse and update leaderboard
                        String data = msg.substring(12); // Remove "LEADERBOARD:"
                        parseAndUpdateLeaderboard(data);
                    } else if (msg.startsWith("GAME_STATUS:")) {
                        String status = msg.substring(12);
                        Platform.runLater(() -> gameStatusLabel.setText(status));
                    }
                }
            } catch (IOException e) {
                if (running) {
                    Platform.runLater(() -> {
                        gameStatusLabel.setText("❌ Disconnected");
                        gameStatusLabel.setTextFill(Color.web("#ff0000"));
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
            playerData.clear();

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

                    playerData.add(new PlayerScore(rank, name, score, streak));
                    rank++;
                }
            }

            // Update game status
            gameStatusLabel.setText("🎮 Live Game - " + playerData.size() + " Players");
        });
    }

    /**
     * Request leaderboard update from server
     */
    private void requestLeaderboardUpdate() {
        if (out != null) {
            out.println("GET_LEADERBOARD");
        }
    }

    /**
     * Show all-time statistics from MongoDB
     */
    private void showAllTimeLeaderboard() {
        if (out != null) {
            out.println("GET_ALLTIME_LEADERBOARD");
        }
        showAlert("All-Time Stats", "Requesting historical data from MongoDB...");
    }

    /**
     * Disconnect from server
     */
    private void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
     * Player Score Model Class (for TableView)
     */
    public static class PlayerScore {
        private int rank;
        private String medal;
        private String playerName;
        private int score;
        private int streak;
        private String streakDisplay;

        public PlayerScore(int rank, String playerName, int score, int streak) {
            this.rank = rank;
            this.playerName = playerName;
            this.score = score;
            this.streak = streak;

            // Set medal based on rank
            if (rank == 1) this.medal = "🥇";
            else if (rank == 2) this.medal = "🥈";
            else if (rank == 3) this.medal = "🥉";
            else this.medal = "";

            // Format streak display
            this.streakDisplay = streak > 0 ? "🔥 " + streak : "-";
        }

        // Getters (required for PropertyValueFactory)
        public int getRank() { return rank; }
        public String getMedal() { return medal; }
        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        public int getStreak() { return streak; }
        public String getStreakDisplay() { return streakDisplay; }
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        javafx.application.Application.launch(LeaderboardApp.class, args);
    }

    /**
     * JavaFX Application wrapper
     */
    public static class LeaderboardApp extends javafx.application.Application {
        @Override
        public void start(Stage primaryStage) {
            LeaderboardScreen screen = new LeaderboardScreen(primaryStage);
            screen.show();
        }
    }
}