package com.sharequiz.sharequiz.models;

import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.enums.Status;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Game implements Serializable {
    private int id;
    private Language language;
    private int maxQuestions;
    private int numberOfPlayers;
    private int questionNumber;
    private Map<String, Player> players;

    public Game(int id, Language language, int maxQuestions, int numberOfPlayers,
                int questionNumber, Map<String, Player> players, Status status,
                Long createdTimestamp, List<Question> questions,
                Map<String, List<Integer>> scores) {
        this.id = id;
        this.language = language;
        this.maxQuestions = maxQuestions;
        this.numberOfPlayers = numberOfPlayers;
        this.questionNumber = questionNumber;
        this.players = players;
        this.status = status;
        this.createdTimestamp = createdTimestamp;
        this.questions = questions;
        this.scores = scores;
    }

    private Status status;
    private Long createdTimestamp;
    private List<Question> questions;
    private Map<String, List<Integer>> scores;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public int getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Map<String, List<Integer>> getScores() {
        return scores;
    }

    public void setScores(Map<String, List<Integer>> scores) {
        this.scores = scores;
    }
}
