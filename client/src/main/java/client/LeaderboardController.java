package client;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import client.models.Score;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LeaderboardController {

    @FXML
    private TableView<Score> table;
    @FXML
    private TableColumn<Score, String> nameCol;
    @FXML
    private TableColumn<Score, Integer> scoreCol;
    private ObservableList<Score> scores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        table.setItems(scores);
    }

    public void setScores(java.util.List<Score> list) {
        scores.setAll(list);
    }
}
