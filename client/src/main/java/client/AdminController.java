package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import models.Question;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper; // ✅ Jackson JSON mapper
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AdminController {

    @FXML private TextField qText;
    @FXML private TextField optA, optB, optC, optD;
    @FXML private TextField correctField;
    @FXML private Button addButton;
    @FXML private Label status;

    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ Jackson mapper
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void onAddQuestion(ActionEvent e) {
        String text = qText.getText().trim();
        if (text.isEmpty()) {
            status.setText("Question text required");
            return;
        }

        ArrayList<String> opts = new ArrayList<>();
        opts.add(optA.getText());
        opts.add(optB.getText());
        opts.add(optC.getText());
        opts.add(optD.getText());

        int correct = 0;
        try { correct = Integer.parseInt(correctField.getText()); } catch (Exception ignored) {}

        Question q = new Question(text, opts, correct);

        // ✅ Send question to backend REST API in a background thread
        new Thread(() -> {
            try {
                String json = objectMapper.writeValueAsString(q);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/addQuestion"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int code = response.statusCode();

                Platform.runLater(() ->
                        status.setText(code == 200 ? "Question added to MongoDB" : "Failed to add question (" + code + ")")
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> status.setText("Error connecting to server"));
            }
        }).start();

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
