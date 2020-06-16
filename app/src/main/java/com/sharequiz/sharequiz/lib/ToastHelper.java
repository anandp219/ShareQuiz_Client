package com.sharequiz.sharequiz.lib;

import android.app.Application;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ToastHelper {

    Application mApplication;

    @Inject
    public ToastHelper(Application application) {
        this.mApplication = application;
    }

    public void makeToast(String text) {
        Toast.makeText(mApplication.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
