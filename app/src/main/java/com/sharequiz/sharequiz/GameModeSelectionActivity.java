package com.sharequiz.sharequiz;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.utils.CommonUtils;

import java.util.Locale;

import javax.inject.Inject;

public class GameModeSelectionActivity extends AppCompatActivity implements HorizontalItemsFragment.HorizontalMenuItemClickListener {

    private final String TAG = "GameModeSelection";
    private View selectedTopicView = null;
    private int selectedTopic = -1;
    HorizontalItemsFragment topicSelector;
    public static final String TOPIC_ID = "topicId";

    @Inject
    SharedPrefsHelper sharedPrefsHelper;
    @Inject
    ToastHelper toastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode_selection);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTopicInFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedTopic = -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_english:
                changeLanguage(Language.ENGLISH.name());
                return true;
            case R.id.action_hindi:
                changeLanguage(Language.HINDI.name());
                return true;
            case R.id.action_tamil:
                changeLanguage(Language.TAMIL.name());
                return true;
            case R.id.action_bengali:
                changeLanguage(Language.BENGALI.name());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeLanguage(final String language) {
        sharedPrefsHelper.saveValue(SharedPrefsHelper.LANGUAGE, language,
            new SharedPrefsHelper.OnEventListener<String>() {
            @Override
            public void onSuccess(String s) {
                setLocale(CommonUtils.getLocaleString(Language.valueOf(language)));
            }

            @Override
            public void onFailure(String s) {
                toastHelper.makeToast("Error while setting language to English");
            }
        });
    }

    @Override
    public void itemClicked(View v) {
        Log.d(TAG, "item clicked with " + ((View) v.getParent()).getId());
        selectedTopic = ((View) v.getParent()).getId();
        if (selectedTopicView != null) {
            selectedTopicView.setBackgroundColor(getResources().getColor(R.color.colorLight));
        }
        selectedTopicView = v;
        selectedTopicView.setBackgroundColor(getResources().getColor(R.color.colorDark));
    }

    @Override
    public void onBackPressed() {
    }

    public void startQuiz(View v) {
        if(selectedTopic == -1) {
            return;
        }
        Intent intent = new Intent(this, WorldMapActivity.class);
        intent.putExtra(TOPIC_ID, selectedTopic);
        startActivity(intent);
    }

    private void setTopicInFragment() {
        HorizontalItemsFragment topics = new HorizontalItemsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        topics.setItemType(HorizontalItemsFragment.ITEM_TOPIC);
        ft.replace(R.id.topic_selector, topics);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        Intent intent = new Intent(this, GameModeSelectionActivity.class);
        startActivity(intent);
    }
}