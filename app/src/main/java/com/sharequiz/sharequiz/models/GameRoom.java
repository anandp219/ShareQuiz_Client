package com.sharequiz.sharequiz.models;

import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.enums.Topic;

import java.io.Serializable;

public class GameRoom implements Serializable {
    private Language language;
    private Topic topic;
    private String roomID;

    public GameRoom(Language language, Topic topic, String roomID) {
        this.language = language;
        this.topic = topic;
        this.roomID = roomID;
    }

    public GameRoom(Language language, Topic topic) {
        this.language = language;
        this.topic = topic;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
