package com.sharequiz.sharequiz.utils;

import com.sharequiz.sharequiz.enums.Language;

public class CommonUtils {

    private CommonUtils() {
    }

    public static String getLocaleString(Language language) {
        switch (language) {
            case ENGLISH:
                return "en";
            case HINDI:
                return "hi";
            default:
                return null;
        }
    }
}
