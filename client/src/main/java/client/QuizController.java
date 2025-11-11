//package client;
//
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import models.Question;
//
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//
//public class QuizController {
//
//    @FXML private Label questionLabel;
//    @FXML private RadioButton option1;
//    @FXML private RadioButton option2;
//    @FXML private RadioButton option3;
//    @FXML private RadioButton option4;
//    @FXML private Label timerLabel;
//    @FXML private Button submitButton;
//
//    private ToggleGroup group;
//    private Socket socket;
//    private ObjectOutputStream out;
//    private ObjectInputStream in;
//    private Question currentQuestion;
//    private QuizTimer timer;
//
//    @FXML
//    public void initialize() {
//        group = new ToggleGroup();
//        option1.setToggleGroup(group);
//        option2.setToggleGroup(group);
//        option3.setToggleGroup(group);
//        option4.setToggleGroup(group);
//
//        submitButton.setOnAction(e -> submitAnswer());
//
//        new Thread(this::connectToServer).start();
//    }
//
//    private void connectToServer() {
//        try {
//            socket = new Socket("localhost", 5000);
//            out = new ObjectOutputStream(socket.getOutputStream());
//            in = new ObjectInputStream(socket.getInputStream());
//
//            while (true) {
//                Object obj = in.readObject();
//                if (obj instanceof Question q) {
//                    currentQuestion = q;
//                    Platform.runLater(() -> displayQuestion(q));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void displayQuestion(Question q) {
//        questionLabel.setText(q.getQuestionText());
//        option1.setText(q.getOptions().get(0));
//        option2.setText(q.getOptions().get(1));
//        option3.setText(q.getOptions().get(2));
//        option4.setText(q.getOptions().get(3));
//
//        group.selectToggle(null); // clear previous selection
//
//        if (timer != null) timer.stop();
//        timer = new QuizTimer(q.getTimeLimit(), this::autoSubmit);
//        timer.start(timerLabel);
//    }
//
//    private void submitAnswer() {
//        try {
//            String answer = group.getSelectedToggle() != null
//                    ? ((RadioButton) group.getSelectedToggle()).getText()
//                    : "";
//            out.writeObject(answer);
//            out.flush();
//            if (timer != null) timer.stop();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void autoSubmit() {
//        try {
//            String answer = group.getSelectedToggle() != null
//                    ? ((RadioButton) group.getSelectedToggle()).getText()
//                    : "";
//            out.writeObject(answer);
//            out.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Question;

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
                if (obj instanceof Question q) {
                    Platform.runLater(() -> displayQuestion(q));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion(Question q) {
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

        timer = new QuizTimer(q.getTimeLimit(), this::autoSubmit);
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
                // default to first option if nothing selected
                answer = option1.getText();
            }

            out.writeObject(answer);
            out.flush();

            if (timer != null) {
                timer.stop();
                timer = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
