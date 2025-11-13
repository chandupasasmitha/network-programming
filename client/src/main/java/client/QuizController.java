package client;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import models.Question;
import models.QuestionWithStartTime;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class QuizController {

    @FXML
    private Label questionNumberLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private RadioButton option1;
    @FXML
    private RadioButton option2;
    @FXML
    private RadioButton option3;
    @FXML
    private RadioButton option4;
    @FXML
    private Label timerLabel;
    @FXML
    private ProgressBar timerProgress;
    @FXML
    private Button submitButton;

    private ToggleGroup group;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private QuizTimer timer;

    private int currentQuestionIndex = 0;
    private int totalQuestions = 0;

    @FXML
    public void initialize() {
        group = new ToggleGroup();
        option1.setToggleGroup(group);
        option2.setToggleGroup(group);
        option3.setToggleGroup(group);
        option4.setToggleGroup(group);

        submitButton.setOnAction(e -> submitAnswer());

        new Thread(this::connectToServer).start();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5050);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object obj = in.readObject();

                if (obj instanceof QuestionWithStartTime qwt) {
                    Question q = qwt.getQuestion();

                    // Calculate remaining time for this question
                    long remainingMillis = (qwt.getStartTimeMillis() + q.getTimeLimit() * 1000)
                            - System.currentTimeMillis();
                    int remainingSeconds = Math.max((int) (remainingMillis / 1000), 0);

                    Platform.runLater(() -> displayQuestion(q, remainingSeconds));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion(Question q, int remainingSeconds) {
        currentQuestionIndex++;
        if (totalQuestions == 0) {
            totalQuestions = 10; // can dynamically set if server provides total
        }
        questionNumberLabel.setText("Question " + currentQuestionIndex + " of " + totalQuestions);
        questionLabel.setText(q.getQuestionText());
        option1.setText(q.getOptions().get(0));
        option2.setText(q.getOptions().get(1));
        option3.setText(q.getOptions().get(2));
        option4.setText(q.getOptions().get(3));

        group.selectToggle(null);
        highlightSelectedOption(null);

        group.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) {
                highlightSelectedOption((RadioButton) newT);
            }
        });

        submitButton.setDisable(false);

        if (timer != null) {
            timer.stop();
        }

        timer = new QuizTimer(remainingSeconds, this::autoSubmit, remainingSeconds, timerProgress, timerLabel);
        timer.start();

        fadeInQuestion();
    }

    private void highlightSelectedOption(RadioButton selected) {
        option1.setStyle("");
        option2.setStyle("");
        option3.setStyle("");
        option4.setStyle("");

        if (selected != null) {
            selected.setStyle("-fx-background-color: linear-gradient(to right, #4a90e2, #5aa1f3);"
                    + " -fx-text-fill: white; -fx-font-weight: bold;");
        }
    }

    private void submitAnswer() {
        sendAnswer();
    }

    private void autoSubmit() {
        sendAnswer();
    }

    private void sendAnswer() {
        try {
            String answer = group.getSelectedToggle() != null
                    ? ((RadioButton) group.getSelectedToggle()).getText()
                    : "UNANSWERED";

            out.writeObject(answer);
            out.flush();

            submitButton.setDisable(true);
            highlightSelectedOption(null);

            if (timer != null) {
                timer.stop();
            }

            System.out.println("✅ Sent answer: " + answer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fadeInQuestion() {
        FadeTransition fade = new FadeTransition(Duration.millis(600), questionLabel.getParent());
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}
