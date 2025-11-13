package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.stage.Stage;
import models.Question;

/**
 * Enhanced QuestionController with leaderboard integration
 * Automatically shows leaderboard when quiz is complete
 * 
 * Member 4: Score Calculation & Leaderboard Integration
 */
public class QuestionController {

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

    private Question currentQuestion;
    private volatile boolean quizActive = true;
    private int currentScore = 0;
    private int questionCount = 0;
    private final int MAX_QUESTIONS = 5; // Demo: show leaderboard after 5 questions

    @FXML
    public void initialize() {
        // Initialize without backend connection for demo
        // In real implementation, this would connect to your ScoreManager backend
    }

    public void setQuestion(Question q) {
        currentQuestion = q;
        questionCount++;
        Platform.runLater(() -> {
            questionLabel.setText(q.getText());
            if (q.getOptions().size() >= 4) {
                optA.setText(q.getOptions().get(0));
                optB.setText(q.getOptions().get(1));
                optC.setText(q.getOptions().get(2));
                optD.setText(q.getOptions().get(3));
            }
            submitButton.setDisable(false);
        });
    }

    @FXML
    public void onSubmit(ActionEvent e) {
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        if (selected == null) {
            return;
        }
        
        String answer = selected.getText();
        
        // Simulate scoring (in real implementation, this would be handled by ScoreManager)
        boolean isCorrect = simulateAnswerCheck(answer);
        if (isCorrect) {
            currentScore += 10; // Add points for correct answer
        }
        
        submitButton.setDisable(true);
        timerLabel.setText("Answer submitted");
        
        // Check if quiz should end and show leaderboard
        checkQuizCompletion();
    }

    /**
     * Simulate answer checking (replace with actual logic)
     */
    private boolean simulateAnswerCheck(String answer) {
        // For demo: randomly make some answers correct
        return Math.random() > 0.3; // 70% chance of being correct
    }

    /**
     * Check if quiz is complete and show leaderboard
     */
    private void checkQuizCompletion() {
        if (questionCount >= MAX_QUESTIONS) {
            // Quiz completed - show leaderboard
            Platform.runLater(() -> {
                try {
                    Thread.sleep(1000); // Brief pause before showing results
                    showLeaderboard();
                } catch (InterruptedException ex) {
                    showLeaderboard();
                }
            });
        } else {
            // Continue with next question
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Wait 2 seconds before next question
                    Platform.runLater(() -> {
                        // In real implementation, this would get the next question from server
                        // For now, just reset the UI for demo
                        resetForNextQuestion();
                    });
                } catch (InterruptedException ex) {
                    // Ignore
                }
            }).start();
        }
    }

    /**
     * Reset UI for next question (demo purposes)
     */
    private void resetForNextQuestion() {
        questionLabel.setText("Waiting for next question... (Question " + (questionCount + 1) + ")");
        optA.setText("Option A");
        optB.setText("Option B");
        optC.setText("Option C");
        optD.setText("Option D");
        optionsGroup.selectToggle(null);
        submitButton.setDisable(true);
        timerLabel.setText("30s");
        timeProgress.setProgress(1.0);
    }

    public void updateTimer(int secondsLeft, int total) {
        Platform.runLater(() -> {
            timerLabel.setText(String.valueOf(secondsLeft) + "s");
            if (total > 0) {
                timeProgress.setProgress(1.0 * secondsLeft / total);
            }
            if (secondsLeft <= 0) {
                onAutoSubmit();
            }
        });
    }

    private void onAutoSubmit() {
        Platform.runLater(() -> {
            submitButton.setDisable(true);
            timerLabel.setText("Time up - auto-submitted");
            
            // Check if quiz should end
            checkQuizCompletion();
        });
    }

    /**
     * Show leaderboard when quiz is complete
     * This is the key integration point for Member 4's leaderboard functionality
     */
    private void showLeaderboard() {
        try {
            quizActive = false;
            
            // Navigate to leaderboard screen
            Parent leaderboardRoot = FXMLLoader.load(getClass().getResource("/fxml/Leaderboard.fxml"));
            Scene leaderboardScene = new Scene(leaderboardRoot);
            leaderboardScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(leaderboardScene);
            stage.setTitle("Multiplayer Quiz Game - Final Results");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Navigation Error", "Could not show leaderboard: " + ex.getMessage());
        }
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Cleanup resources when controller is destroyed
     */
    public void cleanup() {
        quizActive = false;
    }

    /**
     * Get current score (for integration with ScoreManager)
     */
    public int getCurrentScore() {
        return currentScore;
    }
}
