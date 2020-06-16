package com.sharequiz.sharequiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonIOException;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.models.Game;
import com.sharequiz.sharequiz.models.Question;
import com.sharequiz.sharequiz.models.Room;
import com.sharequiz.sharequiz.utils.Constants;
import com.sharequiz.sharequiz.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import javax.inject.Inject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class QuestionActivity extends AppCompatActivity {

    private QuestionActivity questionActivity;
    public static final String GAME_ID = "game_id";
    public String gameId;
    private String answer;
    private int questionNumber;
    private Question question;
    private TextView textView;
    private ScrollView scrollView;
    private OptionView option1View, option2View, option3View, option4View;
    private TextView questionView, timerView, player1ScoreView, player2ScoreView;
    private Socket socket;
    private String opponentId;
    private static ToastHelper toastHelperStatic;

    @Inject
    ToastHelper toastHelper;

    private static final class OptionView extends AppCompatTextView {
        private View textView;

        OptionView(Context context, final View textView) {
            super(context);
            this.textView = textView;
            textView.setOnClickListener(optionClickListener);
        }

        private OnClickListener optionClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionActivity.toastHelperStatic.makeToast(((TextView) textView).getText().toString());
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.toastHelperStatic = toastHelper;
        try {
            socket = IO.socket("http://localhost");
        } catch (Exception ex) {
            Log.e("Question_Activity", "Error while connecting the socket to backend", ex);
            startGameSelectionActivity();
        }
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_question);
        questionActivity = this;
        gameId = getIntent().getStringExtra(GAME_ID);
        textView = findViewById(R.id.fetching_question);
        scrollView = findViewById(R.id.question_activity);
        questionView = findViewById(R.id.textview_question);
        option1View = new OptionView(questionActivity, findViewById(R.id.textview_option1));
        option2View = new OptionView(questionActivity, findViewById(R.id.textview_option2));
        option3View = new OptionView(questionActivity, findViewById(R.id.textview_option3));
        option4View = new OptionView(questionActivity, findViewById(R.id.textview_option4));
        player1ScoreView = findViewById(R.id.player1score);
        player2ScoreView = findViewById(R.id.player2score);
        timerView = findViewById(R.id.timer);
        createGame();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit the Game?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startGameSelectionActivity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void createGame() {
        String response = "-1";
        Room room = new Room(gameId, HttpUtils.PHONE_NUMBER);
        socket.on(Constants.NEW_QUESTION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Game game = (Game) args[0];
                showGameQuestion(game);
            }
        });
        socket.connect();
        socket.emit(Constants.JOIN_EVENT, room);
    }

    private void startGameSelectionActivity() {
        socket.close();
        Intent intent = new Intent(questionActivity, GameModeSelectionActivity.class);
        startActivity(intent);
    }

    private void showGameQuestion(Game game) {
        if (game != null) {
            Set<String> playerIds = game.getPlayers().keySet();
            for (String playerId : playerIds) {
                if (!HttpUtils.PHONE_NUMBER.equals(playerId)) {
                    opponentId = playerId;
                }
            }
            textView.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            questionNumber = game.getQuestionNumber();
            question = game.getQuestions().get(questionNumber);
            questionView.setText(question.getQuestionText());
            option1View.setText(question.getOptions().get(0));
            option2View.setText(question.getOptions().get(1));
            option3View.setText(question.getOptions().get(2));
            option4View.setText(question.getOptions().get(3));
            answer = question.getAnswer();
            player1ScoreView.setText(String.valueOf(game.getScores().get(HttpUtils.PHONE_NUMBER)));
            player2ScoreView.setText(String.valueOf(game.getScores().get(opponentId)));
        } else {
            startGameSelectionActivity();
        }
    }
}
