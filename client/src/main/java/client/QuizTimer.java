package client;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class QuizTimer {

    private int timeRemaining;
    private Timer timer;
    private Runnable onTimeout;
    private int totalTime;
    private ProgressBar progressBar;
    private Label timerLabel;

    public QuizTimer(int timeRemaining, Runnable onTimeout, int totalTime, ProgressBar progressBar, Label timerLabel) {
        this.timeRemaining = timeRemaining;
        this.onTimeout = onTimeout;
        this.totalTime = totalTime;
        this.progressBar = progressBar;
        this.timerLabel = timerLabel;
        this.timer = new Timer(true);
    }

    public void start() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (timeRemaining <= 0) {
                        timer.cancel();
                        timerLabel.setText("Time: 0s");
                        progressBar.setProgress(0);
                        onTimeout.run();
                    } else {
                        timerLabel.setText("Time: " + timeRemaining + "s");
                        if (progressBar != null) {
                            progressBar.setProgress((double) timeRemaining / totalTime);
                        }
                        timeRemaining--;
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void stop() {
        timer.cancel();
    }
}
