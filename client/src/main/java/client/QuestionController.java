package client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ProgressBar;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import models.Question;

import java.util.Timer;
import java.util.TimerTask;

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
    private Timer timer;
    private int timeLeft;
    private QuizConnection connection; // ⛓️ socket communication

    public void setConnection(QuizConnection connection) {
        this.connection = connection;
    }

    @FXML
    public void initialize() {
        timeProgress.setProgress(1.0);
    }

    /** ✅ Called when a new question arrives */
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

        startTimer(q.getTimeLimitSeconds());
    }

    /** 🕒 Start question timer */
    private void startTimer(int totalSeconds) {
        if (timer != null) timer.cancel();
        timeLeft = totalSeconds;
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer(timeLeft, totalSeconds);
                timeLeft--;
                if (timeLeft < 0) {
                    timer.cancel();
                    onAutoSubmit();
                }
            }
        }, 0, 1000);
    }

    @FXML
    public void onSubmit(ActionEvent e) {
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        if (selected == null) return;

        String answer = selected.getText();
        System.out.println("✅ Answer submitted: " + answer);

        // ✅ Send answer to server
        if (connection != null) {
            connection.sendAnswer(answer);
        }

        submitButton.setDisable(true);
        timer.cancel();
        Platform.runLater(() -> timerLabel.setText("Answer submitted"));
    }

    public void updateTimer(int secondsLeft, int total) {
        Platform.runLater(() -> {
            timerLabel.setText(secondsLeft + "s");
            if (total > 0) {
                timeProgress.setProgress((double) secondsLeft / total);
            }
        });
    }

    /** 🕓 Auto-submit when time ends */
    private void onAutoSubmit() {
        Platform.runLater(() -> {
            submitButton.setDisable(true);
            timerLabel.setText("Time up - auto-submitted");

            // ⏰ Send empty or default answer
            if (connection != null) {
                connection.sendAnswer("No answer (timeout)");
            }
        });
    }
}
