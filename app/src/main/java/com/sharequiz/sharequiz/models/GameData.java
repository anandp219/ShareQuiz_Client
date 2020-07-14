package com.sharequiz.sharequiz.models;

import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.enums.Topic;

import java.io.Serializable;

public class GameData implements Serializable {
    private Language language;
    private com.sharequiz.sharequiz.enums.Topic topic;

    public GameData(Language language, com.sharequiz.sharequiz.enums.Topic topic) {
        this.language = language;
        this.topic = topic;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public com.sharequiz.sharequiz.enums.Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
