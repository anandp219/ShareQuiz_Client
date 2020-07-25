package com.sharequiz.sharequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.sharequiz.sharequiz.enums.Language;
import com.sharequiz.sharequiz.enums.Topic;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.models.GameRoom;
import com.sharequiz.sharequiz.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class RoomActivity extends AppCompatActivity {
    private String roomID;
    private TextView roomIDBox;
    private Language language;
    private Topic topic;
    private Button joinRoomButton;
    private TextView roomIDText;
    @Inject
    ToastHelper toastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_room);
        String action = getIntent().getStringExtra(GameModeSelectionActivity.ACTION);
        topic = Topic.values()[getIntent().getIntExtra(GameModeSelectionActivity.TOPIC_ID, 0)];
        language = (Language) getIntent().getSerializableExtra(GameModeSelectionActivity.LANGUAGE);
        roomIDBox = findViewById(R.id.room_id_box);
        roomIDText = findViewById(R.id.room_id_text);
        joinRoomButton = findViewById(R.id.join_room_button);
        GameRoom gameRoom = new GameRoom(language, topic);
        if (action.equals(GameModeSelectionActivity.JOIN)) {
            joinRoomButton.setVisibility(View.VISIBLE);
            roomIDBox.setVisibility(View.VISIBLE);
            roomIDText.setVisibility(View.GONE);
        } else {
            joinRoomButton.setVisibility(View.GONE);
            roomIDBox.setVisibility(View.GONE);
            roomIDText.setVisibility(View.VISIBLE);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                BuildConfig.OTP_URL + "/api/v1/room?phone_number=" + HttpUtils.PHONE_NUMBER +
                    "&room=" + new Gson().toJson(gameRoom), null,
                new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        joinRoomButton.setVisibility(View.VISIBLE);
                        roomID = response.getString("roomID");
                        roomIDText.setText(roomID);
                    } catch (JSONException ex) {
                        roomIDText.setText(R.string.creating_room_error);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    roomIDText.setText(R.string.creating_room_error);
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(50), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            HttpUtils.getRequestQueue().add(jsonObjectRequest);
        }
    }

    @Override
    public void onBackPressed() {
        startGameSelectionActivity();
    }

    public void joinRoom(View view) {
        if (roomIDBox.getVisibility() == View.VISIBLE) {
            roomID = roomIDBox.getText().toString();
        }
        if (roomID != null && roomID.length() <= 0) {
            toastHelper.makeToast("Incorrect roomID");
            return;
        }
        GameRoom gameRoom = new GameRoom(language, topic);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
            BuildConfig.OTP_URL + "/api/v1/join_room?phone_number=" + HttpUtils.PHONE_NUMBER +
                "&roomID=" + roomID + "&room=" + new Gson().toJson(gameRoom), null,
            new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                startWorldMapActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                joinRoomButton.setVisibility(View.GONE);
                roomIDBox.setVisibility(View.GONE);
                roomIDText.setVisibility(View.VISIBLE);
                roomIDText.setText(R.string.joining_room_error);
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(50), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        HttpUtils.getRequestQueue().add(jsonObjectRequest);
    }

    private void startGameSelectionActivity() {
        Intent intent = new Intent(this, GameModeSelectionActivity.class);
        startActivity(intent);
    }

    private void startWorldMapActivity() {
        Intent intent = new Intent(this, WorldMapActivity.class);
        intent.putExtra(GameModeSelectionActivity.TOPIC_ID, topic.ordinal());
        intent.putExtra(GameModeSelectionActivity.ROOM_ID, roomID);
        startActivity(intent);
    }
}