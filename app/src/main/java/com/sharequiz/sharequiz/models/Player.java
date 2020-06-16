package com.sharequiz.sharequiz.models;

import java.io.Serializable;

public class Player implements Serializable {
    private String id;
    private Integer score;

    public Player(String id, Integer score, Integer selected) {
        this.id = id;
        this.score = score;
        this.selected = selected;
    }

    private Integer selected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }
}
