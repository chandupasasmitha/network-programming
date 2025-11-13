package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuizClient extends Application {

    private static final String DEFAULT_FXML = "/fxml/Lobby.fxml";
    private static final String QUIZ_FXML = "/fxml/quiz.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Decide which FXML to load
        String fxmlToLoad = getParameters().getNamed().getOrDefault("scene", "lobby").equalsIgnoreCase("quiz")
                ? QUIZ_FXML
                : DEFAULT_FXML;

        Parent root = FXMLLoader.load(getClass().getResource(fxmlToLoad));
        Scene scene = new Scene(root);

        // Apply CSS
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        // Stage settings
        primaryStage.setTitle(fxmlToLoad.contains("quiz") ? "Quiz Game" : "Multiplayer Quiz Game - Lobby");
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
