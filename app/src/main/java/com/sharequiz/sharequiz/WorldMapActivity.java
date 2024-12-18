package com.sharequiz.sharequiz;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.enums.Topic;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.models.GameData;
import com.sharequiz.sharequiz.models.GameRoom;
import com.sharequiz.sharequiz.utils.Constants;
import com.sharequiz.sharequiz.utils.HttpUtils;
import javax.inject.Inject;

public class WorldMapActivity extends AppCompatActivity {

    private WorldMapActivity worldMapActivity;
    private HorizontalScrollView scroll1;
    private HorizontalScrollView scroll2;
    private int topicId;
    private String roomID;
    private Socket socket;
    private final String[] TRANSPORTS = {Constants.WEBSOCKET_PROTOCOL};
    private boolean gameStarted;

    @Inject
    SharedPrefsHelper sharedPrefsHelper;
    @Inject
    ToastHelper toastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameStarted = false;
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_world_map);
        topicId = getIntent().getIntExtra(GameModeSelectionActivity.TOPIC_ID, -1);
        roomID = getIntent().getStringExtra(GameModeSelectionActivity.ROOM_ID);
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

    @Override
    protected void onDestroy() {
        Log.d("socket", "on destroy");
        super.onDestroy();
        destroySocket();
    }

    private void checkForOpponent() {
        sharedPrefsHelper.getValue(SharedPrefsHelper.LANGUAGE,
            new SharedPrefsHelper.OnEventListener<String>() {
            @Override
            public void onSuccess(String s) {
                joinGame(s);
            }

            @Override
            public void onFailure(String s) {
                toastHelper.makeToast("Error while starting game for the topic");
            }
        });
    }

    void joinGame(String language) {
        initialiseSocket();
        socket.once(Constants.GAME_EVENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                gameStarted = true;
                Log.d(HttpUtils.PHONE_NUMBER, Constants.GAME_EVENT);
                handleGameEvent((String) args[0]);
            }
        });
        socket.once(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(HttpUtils.PHONE_NUMBER, Socket.EVENT_DISCONNECT);
                if(!gameStarted) {
                    startGameSelectionActivity();
                }
            }
        });
        if(roomID == null) {
            GameData gameData = new GameData(Language.valueOf(language), Topic.values()[topicId]);
            socket.emit(Constants.JOIN_EVENT, HttpUtils.getJSONObject(gameData));
        } else {
            GameRoom gameRoom = new GameRoom(Language.valueOf(language), Topic.values()[topicId], roomID);
            socket.emit(Constants.JOIN_WITH_ROOM_EVENT, HttpUtils.getJSONObject(gameRoom));
        }
    }

    private void handleGameEvent(final String gameID) {
        runOnUiThread(
            new Runnable() {
                @Override
                public void run() {
                    startQuestionActivity(gameID);
                }
            });
    }

    private void initialiseSocket() {
        try {
            final IO.Options options = new IO.Options();
            options.path = "/socket.io/join_game/";
            options.transports = TRANSPORTS;
            socket = IO.socket("http://" + BuildConfig.OTP_URL_HOST + ":8082", options);
            socket.connect();
        } catch (Exception ex) {
            Log.e("World_Map_Activity", "Error while connecting the socket for world map activity", ex);
            startGameSelectionActivity();
        }
    }

    private void startGameSelectionActivity() {
        destroySocket();
        Intent intent = new Intent(this, GameModeSelectionActivity.class);
        startActivity(intent);
    }

    private void startQuestionActivity(String gameID) {
        if(socket != null) {
            socket.close();
        }
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(Constants.GAME_ID, gameID);
        startActivity(intent);
    }

    static class TouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        startGameSelectionActivity();
    }

    private void destroySocket() {
        Log.d("socket", "destroy socket");
        if(socket != null) {
            socket.close();
        }
    }
}
