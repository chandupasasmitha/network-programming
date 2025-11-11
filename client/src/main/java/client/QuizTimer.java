//package client;
//
//import javafx.application.Platform;
//import javafx.scene.control.Label;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class QuizTimer {
//
//    private int timeRemaining;
//    private Timer timer;
//    private Runnable onTimeout;
//
//    public QuizTimer(int timeLimitSeconds, Runnable onTimeout) {
//        this.timeRemaining = timeLimitSeconds;
//        this.onTimeout = onTimeout;
//        this.timer = new Timer(true); // daemon thread
//    }
//
//    public void start(Label timerLabel) {
//        stop(); // stop any previous timer
//
//        // show initial time immediately
//        Platform.runLater(() -> timerLabel.setText("Time: " + timeRemaining + "s"));
//
//        timer = new Timer(true);
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> {
//                    if (timeRemaining <= 0) {
//                        timer.cancel();
//                        timerLabel.setText("Time: 0s");
//                        onTimeout.run(); // auto-submit
//                    } else {
//                        timerLabel.setText("Time: " + timeRemaining + "s");
//                        timeRemaining--;
//                    }
//                });
//            }
//        };
//
//        timer.scheduleAtFixedRate(task, 1000, 1000);
//    }
//
//    public void stop() {
//        if (timer != null) timer.cancel();
//    }
//}
package client;

import javafx.application.Platform;
import javafx.scene.control.Label;
import java.util.Timer;
import java.util.TimerTask;

public class QuizTimer {

    private int timeRemaining;
    private Timer timer;
    private Runnable onTimeout;

    public QuizTimer(int timeLimitSeconds, Runnable onTimeout) {
        this.timeRemaining = timeLimitSeconds;
        this.onTimeout = onTimeout;
        this.timer = new Timer(true);
    }

    public void start(Label timerLabel) {
        stop(); // stop previous timer if any

        Platform.runLater(() -> timerLabel.setText("Time: " + timeRemaining + "s"));

        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (timeRemaining <= 0) {
                        timer.cancel();
                        timerLabel.setText("Time: 0s");
                        onTimeout.run(); // auto-submit
                    } else {
                        timerLabel.setText("Time: " + timeRemaining + "s");
                        timeRemaining--;
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void stop() {
        if (timer != null) timer.cancel();
    }
}
