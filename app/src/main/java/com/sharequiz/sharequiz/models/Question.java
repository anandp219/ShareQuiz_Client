package com.sharequiz.sharequiz.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Question implements Serializable {
    private String questionText;
    private List<String> options;
    private String answer;
    private Map<String, String> playerAnswers;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public Question(String questionText, List<String> options, String answer,
                    Map<String, String> playerAnswers) {
        this.questionText = questionText;
        this.options = options;
        this.answer = answer;
        this.playerAnswers = playerAnswers;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Map<String, String> getPlayerAnswers() {
        return playerAnswers;
    }

    public void setPlayerAnswers(Map<String, String> playerAnswers) {
        this.playerAnswers = playerAnswers;
    }
}
