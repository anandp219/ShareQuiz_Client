package com.sharequiz.sharequiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.lib.LanguageHelper;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;

import javax.inject.Inject;

public class LanguageSelectionActivity extends AppCompatActivity implements HorizontalItemsFragment.HorizontalMenuItemClickListener {

    public static final String TAG = "LanguageSelection";
    private Language selectedLanguage = null;
    private View selectedLanguageView = null;

    @Inject
    LanguageHelper languageHelper;
    @Inject
    SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_language_selection);
        HorizontalItemsFragment languageSelector =
            (HorizontalItemsFragment) getSupportFragmentManager().findFragmentById(R.id.language_selector);
        if (languageSelector != null) {
            languageSelector.setItemType(HorizontalItemsFragment.ITEM_LANGUAGE);
        }
        Button submitButton = findViewById(R.id.submit_username_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick(v);
            }
        });
    }

    @Override
    public void itemClicked(View v) {
        Log.d(TAG, "item clicked with " + Language.values()[v.getId()]);
        selectedLanguage = Language.values()[v.getId()];
        if (selectedLanguageView != null) {
            selectedLanguageView.setBackgroundColor(getResources().getColor(R.color.colorLight));
        }
        selectedLanguageView = v;
        selectedLanguageView.setBackgroundColor(getResources().getColor(R.color.colorDark));
    }

    public void onSubmitClick(View v) {
        EditText editText = findViewById(R.id.username_box);
        final String userName = editText.getText().toString();
        if (selectedLanguage == null || userName.length() == 0) {
            String text = selectedLanguage == null ? "Please select a language" :
                "Please enter " + "a" + " username";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG,
                "start new activity with language " + selectedLanguage + " and username " + userName);
            final Intent intent = new Intent(this, GameModeSelectionActivity.class);
            sharedPrefsHelper.saveValue(SharedPrefsHelper.LANGUAGE, selectedLanguage.name(),
                new SharedPrefsHelper.OnEventListener<String>() {
                @Override
                public void onSuccess(String s) {
                    sharedPrefsHelper.saveValue(SharedPrefsHelper.USERNAME, userName,
                        new SharedPrefsHelper.OnEventListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String s) {
                            showFailureToast();
                        }
                    });
                }

                @Override
                public void onFailure(String s) {
                    showFailureToast();
                }
            });
        }
    }

    private void showFailureToast() {
        String failureText = "Failure";
        Toast.makeText(getApplicationContext(), failureText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
    }
}
