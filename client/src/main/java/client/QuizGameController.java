package main.java.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Question;

import java.io.*;
import java.net.Socket;

/**
 * Enhanced QuestionController that handles quiz flow and automatically 
 * shows leaderboard when quiz is complete
 * 
 * Member 4: Score Calculation & Leaderboard Integration
 */
public class QuizGameController {

    @FXML
    private Label questionLabel;
    @FXML
    private RadioButton optA, optB, optC, optD;
    @FXML
    private ToggleGroup optionsGroup;
    @FXML
    private Button submitButton;
    @FXML
    private Label timerLabel;
    @FXML
    private ProgressBar timeProgress;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label statusLabel;

    // Socket communication
    private Socket socket;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private String playerName;
    private volatile boolean quizActive = true;

    // Connection details
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    @FXML
    public void initialize() {
        statusLabel.setText("Connecting to server...");
        connectToServer();
    }

    /**
     * Connect to quiz server
     */
    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                objOut = new ObjectOutputStream(socket.getOutputStream());
                objIn = new ObjectInputStream(socket.getInputStream());

                Platform.runLater(() -> {
                    statusLabel.setText("✅ Connected! Enter your name:");
                });

                // Start game flow
                startQuizFlow();

            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Connection failed: " + e.getMessage());
                    showAlert("Connection Error", "Could not connect to quiz server.");
                });
            }
        }).start();
    }

    /**
     * Handle the complete quiz flow
     */
    private void startQuizFlow() {
        new Thread(() -> {
            try {
                // Get player name from server request
                Object nameRequest = objIn.readObject();
                if ("ENTER_NAME".equals(nameRequest)) {
                    // For demo purposes, use a default name
                    // In real implementation, this would come from lobby
                    playerName = "Player_" + System.currentTimeMillis() % 1000;
                    objOut.writeObject(playerName);
                    objOut.flush();

                    Platform.runLater(() -> {
                        statusLabel.setText("Welcome " + playerName + "! Quiz starting...");
                        scoreLabel.setText("Score: 0");
                    });
                }

                // Receive and process questions
                Object message;
                while (quizActive && (message = objIn.readObject()) != null) {
                    
                    if (message instanceof Question) {
                        Question question = (Question) message;
                        Platform.runLater(() -> displayQuestion(question));
                        
                        // Wait for user answer or timeout
                        waitForAnswer(question);
                        
                    } else if (message instanceof String) {
                        String msg = (String) message;
                        
                        if (msg.equals("GAME_FINISHED")) {
                            // Quiz completed, read final leaderboard
                            Object leaderboardData = objIn.readObject();
                            Platform.runLater(() -> showFinalLeaderboard());
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Quiz error: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Display question in UI
     */
    private void displayQuestion(Question question) {
        questionLabel.setText(question.getText());
        
        if (question.getOptions().size() >= 4) {
            optA.setText(question.getOptions().get(0));
            optB.setText(question.getOptions().get(1));
            optC.setText(question.getOptions().get(2));
            optD.setText(question.getOptions().get(3));
        }
        
        // Clear previous selection
        optionsGroup.selectToggle(null);
        submitButton.setDisable(false);
        
        // Start question timer with default 30 seconds
        startQuestionTimer(30);
        
        statusLabel.setText("Answer the question!");
    }

    /**
     * Start countdown timer for question
     */
    private void startQuestionTimer(int timeLimit) {
        new Thread(() -> {
            for (int i = timeLimit; i > 0; i--) {
                final int timeLeft = i;
                Platform.runLater(() -> {
                    timerLabel.setText(timeLeft + "s");
                    timeProgress.setProgress((double) timeLeft / timeLimit);
                });
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            // Time's up - auto submit
            Platform.runLater(() -> {
                timerLabel.setText("Time's up!");
                submitButton.setDisable(true);
                autoSubmitAnswer();
            });
        }).start();
    }

    /**
     * Wait for user answer with timeout
     */
    private void waitForAnswer(Question question) {
        // This will be handled by the submit button or timeout
        // The actual answer sending is done in onSubmit() or autoSubmitAnswer()
    }

    @FXML
    public void onSubmit() {
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        String answer = selected != null ? selected.getText() : "";
        
        sendAnswer(answer);
        submitButton.setDisable(true);
        statusLabel.setText("Answer submitted! Waiting for next question...");
    }

    /**
     * Auto-submit when time runs out
     */
    private void autoSubmitAnswer() {
        sendAnswer(""); // Empty answer for timeout
        statusLabel.setText("Time's up! Moving to next question...");
    }

    /**
     * Send answer to server
     */
    private void sendAnswer(String answer) {
        new Thread(() -> {
            try {
                objOut.writeObject(answer);
                objOut.flush();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Failed to send answer");
                });
            }
        }).start();
    }

    /**
     * Show final leaderboard when quiz ends
     */
    private void showFinalLeaderboard() {
        try {
            // Close quiz connection
            quizActive = false;
            if (socket != null) {
                socket.close();
            }
            
            // Navigate to leaderboard
            Parent leaderboardRoot = FXMLLoader.load(getClass().getResource("/fxml/Leaderboard.fxml"));
            Scene leaderboardScene = new Scene(leaderboardRoot);
            leaderboardScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(leaderboardScene);
            stage.setTitle("Multiplayer Quiz Game - Final Results");
            
        } catch (Exception e) {
            showAlert("Navigation Error", "Could not show leaderboard: " + e.getMessage());
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
     * Cleanup when controller is destroyed
     */
    public void cleanup() {
        quizActive = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}