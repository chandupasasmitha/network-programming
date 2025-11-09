package client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ProgressBar;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import client.models.Question;

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

    @FXML
    public void initialize() {
        // prepared for dynamic updates
    }

    public void setQuestion(Question q) {
        currentQuestion = q;
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
        // TODO: send answer to server via socket
        submitButton.setDisable(true);
        timerLabel.setText("Answer submitted");
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
            // TODO: auto-submit default or empty answer to server
        });
    }
}
