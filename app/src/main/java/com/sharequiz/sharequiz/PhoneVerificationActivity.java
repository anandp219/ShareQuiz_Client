package com.sharequiz.sharequiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.utils.HttpUtils;

import org.json.JSONObject;

import java.util.regex.Pattern;

import javax.inject.Inject;

public class PhoneVerificationActivity extends AppCompatActivity {

    public static final String PHONE_NUMBER = "phoneNumber";
    private PhoneVerificationActivity phoneVerificationActivity;

    @Inject
    SharedPrefsHelper sharedPrefsHelper;
    @Inject
    ToastHelper toastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_phone_verification);
        phoneVerificationActivity = this;
    }

    public void getOTP(View view) {
        EditText editText = findViewById(R.id.phone_edit);
        String phoneNumber = editText.getText().toString();
        if (!validatePhoneNumber(phoneNumber)) {
            toastHelper.makeToast("Enter valid Phone Number");
            return;
        }
        requestOtp(phoneNumber);

    }

    private void requestOtp(final String phoneNumber) {
        HttpUtils.PHONE_NUMBER = phoneNumber;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
            BuildConfig.OTP_URL + "/api/v1/otp?phone_number=" + phoneNumber, null,
            new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(phoneVerificationActivity,
                    OTPVerificationActivity.class);
                intent.putExtra(PHONE_NUMBER, phoneNumber);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                toastHelper.makeToast("Error while sending OTP");
            }
        });
        HttpUtils.getRequestQueue().add(jsonObjectRequest);
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        return Pattern.compile("^[1-9]\\d{9}$").matcher(phoneNumber).matches();
    }
}