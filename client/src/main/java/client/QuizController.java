
package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Question;
import models.QuestionWithStartTime;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class QuizController {

    @FXML private Label questionLabel;
    @FXML private RadioButton option1;
    @FXML private RadioButton option2;
    @FXML private RadioButton option3;
    @FXML private RadioButton option4;
    @FXML private Label timerLabel;
    @FXML private Button submitButton;

    private ToggleGroup group;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private QuizTimer timer;

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
            socket = new Socket("localhost", 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object obj = in.readObject();

                // ✅ Handle synchronized question with start time
                if (obj instanceof QuestionWithStartTime qwt) {
                    Question q = qwt.getQuestion();

                    // Calculate remaining time based on server timestamp
                    long remainingMillis = (qwt.getStartTimeMillis() + q.getTimeLimit() * 1000)
                            - System.currentTimeMillis();
                    int remainingSeconds = (int) (remainingMillis / 1000);

                    // Avoid negative timer if client is slow
                    if (remainingSeconds < 0) remainingSeconds = 0;

                    System.out.println("⏱ Remaining time for client " + socket.getLocalPort() + ": " + remainingSeconds + "s");

                    // ✅ Make variables effectively final for lambda
                    final Question question = q;
                    final int timeLeft = remainingSeconds;

                    Platform.runLater(() -> displayQuestion(question, timeLeft));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion(Question q, int remainingSeconds) {
        questionLabel.setText(q.getQuestionText());
        option1.setText(q.getOptions().get(0));
        option2.setText(q.getOptions().get(1));
        option3.setText(q.getOptions().get(2));
        option4.setText(q.getOptions().get(3));

        group.selectToggle(null); // clear previous selection

        if (timer != null) {
            timer.stop();
            timer = null;
        }

        // Use the synchronized remaining time
        timer = new QuizTimer(remainingSeconds, this::autoSubmit);
        timer.start(timerLabel);
    }


    private void submitAnswer() {
        sendAnswer();
    }

    private void autoSubmit() {
        sendAnswer();
    }

    private void sendAnswer() {
        try {
            String answer;

            if (group.getSelectedToggle() != null) {
                answer = ((RadioButton) group.getSelectedToggle()).getText();
            } else {
                // mark unanswered explicitly
                answer = "UNANSWERED";
            }

            out.writeObject(answer);
            out.flush();

            if (timer != null) {
                timer.stop();
                timer = null;
            }

            System.out.println("✅ Sent answer: " + answer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
