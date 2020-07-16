package com.sharequiz.sharequiz.lib;

import android.app.Application;
import com.sharequiz.sharequiz.enums.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LanguageHelper {

    Application mApplication;

    public List<Language> LANGUAGES_SUPPORTED = new ArrayList<>(Arrays.asList(Language.HINDI,
        Language.ENGLISH, Language.TAMIL, Language.BENGALI));

    @Inject
    public LanguageHelper(Application application) {
        this.mApplication = application;
    }

}
