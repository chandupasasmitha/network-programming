
package models;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private String questionText;
    private List<String> options;
    private String correctOption;
    private int timeLimitSeconds;

    public Question(String questionText, List<String> options, String correctOption, int timeLimitSeconds) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public String getCorrectOption() { return correctOption; }
    public int getTimeLimit() { return timeLimitSeconds; }
}
