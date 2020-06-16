package com.sharequiz.sharequiz.utils;


import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sharequiz.sharequiz.BuildConfig;


public class HttpUtils {
    private static RequestQueue mRequestQueue;
    public static String PHONE_NUMBER;

    private HttpUtils() {
    }

    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);

        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
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