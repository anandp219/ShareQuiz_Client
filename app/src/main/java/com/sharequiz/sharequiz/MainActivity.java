package com.sharequiz.sharequiz;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.lib.LanguageHelper;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;
import com.sharequiz.sharequiz.utils.HttpUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    SharedPrefsHelper sharedPrefsHelper;
    @Inject
    LanguageHelper languageHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        final Intent phoneActivity = new Intent(this, PhoneVerificationActivity.class);
        final Intent gameModeSelectionActivity = new Intent(this, GameModeSelectionActivity.class);
        final Intent languageSelectionActivity = new Intent(this, LanguageSelectionActivity.class);
        final Map<String, String> valueMap = new HashMap<>();
        sharedPrefsHelper.getValue(SharedPrefsHelper.PHONE_NUMBER,
            new SharedPrefsHelper.OnEventListener<String>() {
            @Override
            public void onSuccess(String s) {
                HttpUtils.PHONE_NUMBER = s;
                valueMap.put(SharedPrefsHelper.PHONE_NUMBER, s);
                sharedPrefsHelper.getValue(SharedPrefsHelper.USERNAME,
                    new SharedPrefsHelper.OnEventListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        valueMap.put(SharedPrefsHelper.USERNAME, s);
                        sharedPrefsHelper.getValue(SharedPrefsHelper.LANGUAGE,
                            new SharedPrefsHelper.OnEventListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                valueMap.put(SharedPrefsHelper.LANGUAGE, s);
                                Locale locale = new Locale(s.equals(Language.HINDI.name()) ? "hi" : "en");
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                                gameModeSelectionActivity.putExtra("extra",
                                    (Serializable) valueMap);
                                startActivity(gameModeSelectionActivity);
                            }

                            @Override
                            public void onFailure(String s) {
                                startActivity(languageSelectionActivity);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String s) {
                        startActivity(languageSelectionActivity);
                    }
                });
            }

            @Override
            public void onFailure(String s) {
                startActivity(phoneActivity);
            }
        });
    }

}
