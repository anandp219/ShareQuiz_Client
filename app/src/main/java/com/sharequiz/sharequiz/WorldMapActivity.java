package com.sharequiz.sharequiz;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;
import com.sharequiz.sharequiz.lib.ToastHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Base64;

import javax.inject.Inject;

public class WorldMapActivity extends AppCompatActivity {

    private WorldMapActivity worldMapActivity;
    private HorizontalScrollView scroll1;
    private HorizontalScrollView scroll2;
    private int topicId;

    @Inject
    SharedPrefsHelper sharedPrefsHelper;
    @Inject
    ToastHelper toastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_world_map);
        topicId = getIntent().getIntExtra(GameModeSelectionActivity.TOPIC_ID, -1);
        worldMapActivity = this;
        scroll1 = findViewById(R.id.scroll1);
        scroll2 = findViewById(R.id.scroll2);
        scroll1.setOnTouchListener(new TouchListener());
        scroll2.setOnTouchListener(new TouchListener());
        final ImageView backgroundOne = findViewById(R.id.background_one);
        final ImageView backgroundTwo = findViewById(R.id.background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();
        checkForOpponent();
    }

    private void checkForOpponent() {
        sharedPrefsHelper.getValue(SharedPrefsHelper.LANGUAGE,
            new SharedPrefsHelper.OnEventListener<String>() {
            @Override
            public void onSuccess(String s) {
                MyClientTask myClientTask = new MyClientTask(BuildConfig.OTP_URL_HOST, 8081, s,
                    topicId, worldMapActivity, toastHelper);
                myClientTask.execute();
            }

            @Override
            public void onFailure(String s) {
                toastHelper.makeToast("Error while starting game for the topic");
            }
        });
    }

    static class TouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            return true;
        }
    }

    public static class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        String language;
        String response = "-1";
        Activity activity;
        int topic;
        int dstPort;
        ToastHelper toastHelper;

        MyClientTask(String address, int port, String language, int topic, Activity activity,
                     ToastHelper toastHelper) {
            this.dstAddress = address;
            this.dstPort = port;
            this.topic = topic;
            this.language = language;
            this.activity = activity;
            this.toastHelper = toastHelper;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(GameModeSelectionActivity.TOPIC_ID,
                    Integer.toString(this.topic));
                jsonObject.addProperty(SharedPrefsHelper.LANGUAGE,
                    String.valueOf(Language.valueOf(this.language).ordinal()));
                socket = new Socket(dstAddress, dstPort);

                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.write(jsonObject.toString().getBytes());
                dataOutputStream.flush();

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                byte[] buffer = new byte[4096];
                int n = dataInputStream.read(buffer);
                response = new String(Arrays.copyOf(buffer, n));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent intent;
            if (!response.equals("-1")) {
                intent = new Intent(activity, QuestionActivity.class);
                intent.putExtra(QuestionActivity.GAME_ID, response);
            } else {
                toastHelper.makeToast("Error while starting the game for the user");
                intent = new Intent(activity, GameModeSelectionActivity.class);
            }
            activity.startActivity(intent);
        }
    }
}
