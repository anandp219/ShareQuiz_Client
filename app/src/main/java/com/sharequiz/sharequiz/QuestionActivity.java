package com.sharequiz.sharequiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.models.Game;
import com.sharequiz.sharequiz.models.Question;
import com.sharequiz.sharequiz.models.Room;
import com.sharequiz.sharequiz.utils.Constants;
import com.sharequiz.sharequiz.utils.HttpUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class QuestionActivity extends AppCompatActivity {

    private QuestionActivity questionActivity;
    public String gameId;
    public String answer;
    public String selectedAnswer;
    public CountDownTimer countDownTimer;
    private int questionNumber;
    private TextView newQuestionTextView;
    private ScrollView scrollView;
    private OptionView option1View, option2View, option3View, option4View;
    private TextView questionView, timerView, player1ScoreView, player2ScoreView;
    private String opponentId;
    private final String[] TRANSPORTS = {Constants.WEBSOCKET_PROTOCOL};
    private Socket socket;
    private int TIME_FOR_A_GAME_IN_MILLIS = 100000;
    private int TIME_FOR_A_SECOND_IN_MILLIS = 1000;
    private boolean disableClick;
    private Game game;
    private String opponentAnswer;

    @Inject
    ToastHelper toastHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_question);
        questionActivity = this;
        gameId = getIntent().getStringExtra(Constants.GAME_ID);
        newQuestionTextView = findViewById(R.id.fetching_question);
        scrollView = findViewById(R.id.question_activity);
        questionView = findViewById(R.id.textview_question);
        option1View = new OptionView(questionActivity, findViewById(R.id.textview_option1), "A");
        option2View = new OptionView(questionActivity, findViewById(R.id.textview_option2), "B");
        option3View = new OptionView(questionActivity, findViewById(R.id.textview_option3), "C");
        option4View = new OptionView(questionActivity, findViewById(R.id.textview_option4), "D");
        player1ScoreView = findViewById(R.id.player1score);
        player2ScoreView = findViewById(R.id.player2score);
        timerView = findViewById(R.id.timer);
        createGame();
    }

    public static String getScore(Game game, String playerID) {
        int totalScore = 0;
        for (Integer score : game.getScores().get(playerID)) {
            totalScore += score;
        }
        return String.valueOf(totalScore);
    }

    private void createGame() {
        initialiseSocket();
        Room room = new Room(gameId, HttpUtils.PHONE_NUMBER);
        socket.on(Constants.NEW_QUESTION_EVENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleNewQuestionEvent((String) args[0]);
            }
        });
        socket.on(Constants.NEW_ANSWER_EVENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleNewAnswerEvent((String) args[0]);
            }
        });
        socket.on(Constants.GAME_OVER_EVENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleGameOverEvent((String) args[0]);
            }
        });
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                    handleDisconnectEvent();
            }
        });
        socket.emit(Constants.JOIN_EVENT, HttpUtils.getJSONObject(room));
    }

    private void handleDisconnectEvent() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(socket.connected()) {
                    socket.close();
                }
                startGameSelectionActivity();
            }
        });
    }

    private void initialiseSocket() {
        try {
            final IO.Options options = new IO.Options();
            options.transports = TRANSPORTS;
            socket = IO.socket("http://" + BuildConfig.OTP_URL_HOST + ":8082", options);
            socket.connect();
        } catch (Exception ex) {
            Log.e("Question_Activity", "Error while connecting the socket to backend", ex);
            startGameSelectionActivity();
        }
    }

    private void handleNewQuestionEvent(String gameString) {
        Gson g = new Gson();
        game = g.fromJson(gameString, Game.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Set<String> playerIds = game.getPlayers().keySet();
                for (String playerId : playerIds) {
                    if (!HttpUtils.PHONE_NUMBER.equals(playerId)) {
                        opponentId = playerId;
                    }
                }
                showGameQuestion();
            }
        });
    }

    private void handleNewAnswerEvent(String gameString) {
        Gson g = new Gson();
        game = g.fromJson(gameString, Game.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Question question = game.getQuestions().get(questionNumber);
                opponentAnswer = question.getPlayerAnswers().get(opponentId);
                if (opponentAnswer != null && selectedAnswer != null) {
                    endRoundScreen();
                }
            }
        });
    }

    private void handleGameOverEvent(String gameString) {
        Gson g = new Gson();
        game = g.fromJson(gameString, Game.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startGameCompleteActivity();
            }
        });
    }

    private void endRoundScreen() {
        scrollView.setVisibility(View.GONE);
        newQuestionTextView.setVisibility(View.VISIBLE);
    }

    private void startGameSelectionActivity() {
        socket.close();
        Intent intent = new Intent(questionActivity, GameModeSelectionActivity.class);
        startActivity(intent);
    }

    private void startGameCompleteActivity() {
        Intent intent = new Intent(questionActivity, GameCompleteScreenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_GAME, game);
        intent.putExtra(Constants.OPPONENT_ID, opponentId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void timerEnd() {
        disableClick = true;
        timerView.setText("0");
        Question question = game.getQuestions().get(questionNumber);
        question.getPlayerAnswers().put(HttpUtils.PHONE_NUMBER, "E");
        socket.emit(Constants.TIME_OUT_EVENT, new Gson().toJson(game));
    }

    private void showGameQuestion() {
        if (game != null) {
            initialiseGameScreen();
            newQuestionTextView.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            questionNumber = game.getQuestionNumber();
            Question question = game.getQuestions().get(questionNumber);
            questionView.setText(question.getQuestionText());
            option1View.setText(question.getOptions().get(0));
            option2View.setText(question.getOptions().get(1));
            option3View.setText(question.getOptions().get(2));
            option4View.setText(question.getOptions().get(3));
            answer = question.getAnswer();
            player1ScoreView.setText(getScore(game, HttpUtils.PHONE_NUMBER));
            player2ScoreView.setText(getScore(game, opponentId));
            countDownTimer = new CountDownTimer(TIME_FOR_A_GAME_IN_MILLIS,
                TIME_FOR_A_SECOND_IN_MILLIS) {
                public void onTick(long millisUntilFinished) {
                    timerView.setText(String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    timerEnd();
                }
            }.start();
        } else {
            startGameSelectionActivity();
        }
    }

    private void initialiseGameScreen() {
        disableClick = false;
        selectedAnswer = null;
        opponentAnswer = null;
        timerView.setText(String.valueOf(TIME_FOR_A_GAME_IN_MILLIS / 1000));
        option1View.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option2View.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option3View.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option4View.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private final class OptionView extends AppCompatTextView {
        private TextView textView;
        private String answerNumber;

        OptionView(Context context, final View textView, String answerNumber) {
            super(context);
            this.textView = (TextView) textView;
            this.answerNumber = answerNumber;
            textView.setOnClickListener(optionClickListener);
        }

        void setText(String text) {
            textView.setText(text);
        }

        @Override
        public void setBackgroundColor(int color) {
            textView.setBackgroundColor(color);
        }

        private OnClickListener optionClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!disableClick) {
                    disableClick = true;
                    selectedAnswer = answerNumber;
                    if (answer.equals(answerNumber)) {
                        textView.setBackgroundColor(getResources().getColor(R.color.green));
                    } else {
                        textView.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                    optionClicked(answer.equals(answerNumber));
                }
            }
        };
    }

    private void optionClicked(boolean correctAnswerClicked) {
        countDownTimer.cancel();
        String seconds = timerView.getText().toString();
        Map<String, List<Integer>> scores = game.getScores();
        List<Integer> scoresForUser = scores.get(HttpUtils.PHONE_NUMBER);
        Question question = game.getQuestions().get(questionNumber);
        question.getPlayerAnswers().put(HttpUtils.PHONE_NUMBER, selectedAnswer);
        if (correctAnswerClicked) {
            scoresForUser.set(questionNumber,
                (Integer.parseInt(seconds) * 1000 * Constants.SCORE_FOR_CORRECT_ANSWER) / TIME_FOR_A_GAME_IN_MILLIS);
        }
        scores.put(HttpUtils.PHONE_NUMBER, scoresForUser);
        game.setScores(scores);
        updatePlayerScore();
        socket.emit(Constants.ANSWER_EVENT, new Gson().toJson(game));
    }

    private void updatePlayerScore() {
        player1ScoreView.setText(getScore(game, HttpUtils.PHONE_NUMBER));
        player2ScoreView.setText(getScore(game, opponentId));
    }
}
