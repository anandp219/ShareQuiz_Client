package com.sharequiz.sharequiz.lib;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


@Singleton
public class SharedPrefsHelper {

    private static final String TAG = "SharedPrefsHelper";
    private static final String MY_PREFERNCES = "myPrefs";
    private static final String SAVE = "1";
    private static final String GET = "0";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String USERNAME = "username";
    public static final String LANGUAGE = "language";
    private static SharedPreferences sharedPreferences;

    @Inject
    public SharedPrefsHelper(Application mApplication) {
        sharedPreferences =
            mApplication.getApplicationContext().getSharedPreferences(MY_PREFERNCES,
                Context.MODE_PRIVATE);
    }

    public void saveValue(String key, String value, OnEventListener<String> onEventListener) {
        try {
            BackGroundTask backGroundTask = new BackGroundTask(onEventListener);
            backGroundTask.execute(SAVE, key, value);
        } catch (Exception ex) {
            Log.d(TAG, "Error while saving string value in shared prefs");
        }
    }

    public void getValue(String key, OnEventListener<String> onEventListener) {
        try {
            BackGroundTask backGroundTask = new BackGroundTask(onEventListener);
            backGroundTask.execute(GET, key);
        } catch (Exception ex) {
            Log.d(TAG, "Error while getting value in shared prefs");
        }
    }

    public static class BackGroundTask extends AsyncTask<String, Integer, String> {

        private OnEventListener<String> eventListener;
        Exception mException;

        BackGroundTask(OnEventListener<String> eventListener) {
            this.eventListener = eventListener;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                SharedPreferences.Editor editor = SharedPrefsHelper.sharedPreferences.edit();
                if (params[0].equals(SAVE)) {
                    editor.putString(params[1], params[2]);
                    editor.apply();
                    return params[2];
                } else if (params[0].equals(GET)) {
                    return sharedPreferences.getString(params[1], null);
                }
            } catch (Exception ex) {
                mException = ex;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (eventListener != null) {
                if (mException == null && result != null) {
                    eventListener.onSuccess(result);
                } else {
                    eventListener.onFailure(mException != null ? mException.toString() :
                        "Empty " + "value");
                }
            }
        }
    }

    public interface OnEventListener<T> {
        void onSuccess(T t);

        void onFailure(T t);
    }
}
