package com.sharequiz.sharequiz;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.lib.LanguageHelper;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.models.Topic;
import com.sharequiz.sharequiz.utils.HttpUtils;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalItemsFragment extends androidx.fragment.app.Fragment {

    interface HorizontalMenuItemClickListener {
        void itemClicked(View view);
    }

    private int itemToDisplay;

    public static final String TAG = "HorizontalItemsFragment";
    public static final int ITEM_LANGUAGE = 1;
    public static final int ITEM_TOPIC = 2;

    private HorizontalMenuItemClickListener horizontalMenuItemClickListener;

    public HorizontalItemsFragment() {
        // Required empty public constructor
    }

    @Inject
    LanguageHelper languageHelper;
    @Inject
    SharedPrefsHelper sharedPrefsHelper;
    @Inject
    ToastHelper toastHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_horizontal_items, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.horizontalMenuItemClickListener = (HorizontalMenuItemClickListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShareQuizApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            switch (itemToDisplay) {
                case ITEM_LANGUAGE:
                    displayLanguage(view);
                    break;
                case ITEM_TOPIC:
                    displayTopics(view);
                    break;
                default:
                    Log.d(TAG, "No horizontal items to display");
            }
        }
    }

    public void displayTopics(final View view) {
        sharedPrefsHelper.getValue(SharedPrefsHelper.LANGUAGE,
            new SharedPrefsHelper.OnEventListener<String>() {
            @Override
            public void onSuccess(String s) {
                Type listType = new TypeToken<ArrayList<Topic>>() {
                }.getType();
                String jsonFilePath = "json-files/topics/" + s.toLowerCase() + ".json";
                try {
                    BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(getActivity().getAssets().open(jsonFilePath)));
                    List<Topic> topics = new Gson().fromJson(bufferedReader, listType);
                    LinearLayout linearLayout = view.findViewById(R.id.select_listview);
                    linearLayout.removeAllViews();
                    for (Topic topic : topics) {
                        View menuItem =
                            LayoutInflater.from(getContext()).inflate(R.layout.horizontal_menu_item_topic, linearLayout, false);
                        menuItem.setId(topic.getId());
                        final ImageButton button = menuItem.findViewById(R.id.image_of_item);
                        TextView topicText = menuItem.findViewById(R.id.name_of_item);
                        topicText.setText(topic.getName());
                        Glide.with(getContext()).load(HttpUtils.getImageUrl(topic.getImageUrlSmall())).into(button);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                horizontalMenuItemClickListener.itemClicked(v);
                            }
                        });
                        linearLayout.addView(menuItem);
                    }
                } catch (Exception ex) {
                    toastHelper.makeToast("Error while fetching topics for selected language");
                }
            }

            @Override
            public void onFailure(String s) {
                toastHelper.makeToast("Error while fetching selected language");
            }
        });
    }

    public void displayLanguage(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.select_listview);
        linearLayout.removeAllViews();
        List<Language> languagesSupported = languageHelper.LANGUAGES_SUPPORTED;
        for (Language language : languagesSupported) {
            switch (language) {
                case ODIA:
                    addHorizontalMenuItem(R.drawable.odia, linearLayout, Language.ODIA.ordinal());
                    break;
                case HINDI:
                    addHorizontalMenuItem(R.drawable.hindi, linearLayout, Language.HINDI.ordinal());
                    break;
                case TAMIL:
                    addHorizontalMenuItem(R.drawable.tamil, linearLayout, Language.TAMIL.ordinal());
                    break;
                case BENGALI:
                    addHorizontalMenuItem(R.drawable.bengali, linearLayout,
                        Language.BENGALI.ordinal());
                    break;
                case ENGLISH:
                    addHorizontalMenuItem(R.drawable.english, linearLayout,
                        Language.ENGLISH.ordinal());
                    break;
            }
        }
    }

    public void addHorizontalMenuItem(int imageResource, ViewGroup linearLayout, int id) {
        View menuItem = LayoutInflater.from(getContext()).inflate(R.layout.horizontal_menu_item,
            linearLayout, false);
        ImageButton button = menuItem.findViewById(R.id.horizontal_menu_item);
        button.setId(id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalMenuItemClickListener.itemClicked(v);
            }
        });
        button.setImageResource(imageResource);
        linearLayout.addView(menuItem);
    }

    public void setItemType(int itemType) {
        this.itemToDisplay = itemType;
    }
}
