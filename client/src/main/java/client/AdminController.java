package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import models.Question;
import javafx.application.Platform;
import java.util.ArrayList;

public class AdminController {

    @FXML
    private TextField qText;
    @FXML
    private TextField optA, optB, optC, optD;
    @FXML
    private TextField correctField;
    @FXML
    private Button addButton;
    @FXML
    private Label status;

    @FXML
    public void onAddQuestion(ActionEvent e) {
        String text = qText.getText().trim();
        if (text.isEmpty()) {
            status.setText("Question text required");
            return;
        }
        java.util.List<String> opts = new ArrayList<>();
        opts.add(optA.getText());
        opts.add(optB.getText());
        opts.add(optC.getText());
        opts.add(optD.getText());
        int correct = 0;
        try {
            correct = Integer.parseInt(correctField.getText());
        } catch (Exception ex) {
        }
        Question q = new Question(text, opts, correct);
        // TODO: send to REST API or save to JSON on server
        status.setText("Question added (local only)");
        clearForm();
    }

    private void clearForm() {
        qText.clear();
        optA.clear();
        optB.clear();
        optC.clear();
        optD.clear();
        correctField.clear();
    }
}
