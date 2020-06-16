package com.sharequiz.sharequiz;

import android.app.Application;

import com.sharequiz.sharequiz.di.AppComponent;
import com.sharequiz.sharequiz.di.AppModule;
import com.sharequiz.sharequiz.di.DaggerAppComponent;
import com.sharequiz.sharequiz.di.ResourceModule;
import com.sharequiz.sharequiz.utils.HttpUtils;


public class ShareQuizApplication extends Application {

    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent =
            DaggerAppComponent.builder().appModule(new AppModule(this)).resourceModule(new ResourceModule(this)).build();
        mAppComponent.inject(this);
        init();
    }


    private void init() {
        HttpUtils.init(this);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
