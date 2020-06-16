package com.sharequiz.sharequiz.lib;

import android.app.Application;

import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.models.Topic;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TopicHelper {

    Application mApplication;

    @Inject
    public TopicHelper(Application application) {
        this.mApplication = application;
    }

    public List<Topic> getTopicsForLanguage(Language language) {
        return new ArrayList<>();
    }
}
