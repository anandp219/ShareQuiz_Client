package com.sharequiz.sharequiz;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sharequiz.sharequiz.models.Game;
import com.sharequiz.sharequiz.models.Question;
import com.sharequiz.sharequiz.utils.Constants;
import com.sharequiz.sharequiz.utils.HttpUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class GameCompleteScreenActivity extends AppCompatActivity {
    private GameCompleteScreenActivity gameCompleteScreenActivity;
    private Game game;
    private TextView resultView;
    private TextView player1ScoreView;
    private TextView player2ScoreView;
    private String opponentId;
    private Integer player1Score;
    private Integer player2Score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_complete_screen);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        gameCompleteScreenActivity = this;
        resultView = findViewById(R.id.result_text);
        player1ScoreView = findViewById(R.id.your_score_text_view);
        player2ScoreView = findViewById(R.id.opponent_score_text_view);
        game = (Game) getIntent().getExtras().get(Constants.EXTRA_GAME);
        opponentId = getIntent().getStringExtra(Constants.OPPONENT_ID);
        showGameStatus();
        showPlayerScores();
    }

    private void showGameStatus() {
        player1Score = Integer.valueOf(QuestionActivity.getScore(game, HttpUtils.PHONE_NUMBER));
        player2Score = Integer.valueOf(QuestionActivity.getScore(game, opponentId));
        String resultText = getResources().getString(player1Score > player2Score ?
            R.string.you_won_string : (player1Score.equals(player2Score) ?
            R.string.match_tie_string : R.string.you_lose_string));
        resultView.setText(resultText);
    }

    private void showPlayerScores() {
        player1ScoreView.setText(getResources().getString(R.string.your_string,
            String.valueOf(player1Score)));
        player2ScoreView.setText(getResources().getString(R.string.opponent_string,
            String.valueOf(player2Score)));
    }

    public void newGame(View view) {
        Intent intent = new Intent(gameCompleteScreenActivity, GameModeSelectionActivity.class);
        startActivity(intent);
    }
}