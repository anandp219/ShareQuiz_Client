package com.sharequiz.sharequiz.di;

import android.app.Application;

import com.sharequiz.sharequiz.lib.LanguageHelper;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.lib.TopicHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ResourceModule {

    Application mApplication;

    public ResourceModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    LanguageHelper provideLanguageHelper() {
        return new LanguageHelper(mApplication);
    }

    @Provides
    @Singleton
    SharedPrefsHelper provideSharedPrefsHelper() {
        return new SharedPrefsHelper(mApplication);
    }

    @Provides
    @Singleton
    TopicHelper provideTopicHelper() {
        return new TopicHelper(mApplication);
    }

    @Provides
    @Singleton
    ToastHelper provideToastHelper() {
        return new ToastHelper(mApplication);
    }
}
