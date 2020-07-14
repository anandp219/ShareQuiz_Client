package com.sharequiz.sharequiz.utils;


import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sharequiz.sharequiz.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;


public class HttpUtils {
    private static RequestQueue mRequestQueue;
    public static String PHONE_NUMBER;
    private static String TAG = HttpUtils.class.getCanonicalName();

    private HttpUtils() {
    }

    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);

        int memClass =
            ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
    }

    public static JSONObject getJSONObject(Object obj) {
        try {
            String jsonInString = new Gson().toJson(obj);
            return new JSONObject(jsonInString);
        } catch (JSONException ex) {
            Log.e(TAG, ex.toString());
            return new JSONObject();
        }
    }


    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static String getImageUrl(String path) {
        return BuildConfig.OTP_URL + path;
    }
}