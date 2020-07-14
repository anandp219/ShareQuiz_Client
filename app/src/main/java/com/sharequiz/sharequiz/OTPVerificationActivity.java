package com.sharequiz.sharequiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.goodiebag.pinview.Pinview;
import com.sharequiz.sharequiz.lib.SharedPrefsHelper;
import com.sharequiz.sharequiz.lib.ToastHelper;
import com.sharequiz.sharequiz.utils.HttpUtils;

import org.json.JSONObject;

import javax.inject.Inject;

public class OTPVerificationActivity extends AppCompatActivity {

    private static final String TAG = OTPVerificationActivity.class.getName();
    private String phoneNumber;
    private OTPVerificationActivity otpVerificationActivity;
    @Inject
    SharedPrefsHelper sharedPrefsHelper;
    @Inject
    ToastHelper toastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ((ShareQuizApplication) getApplication()).getAppComponent().inject(this);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra(PhoneVerificationActivity.PHONE_NUMBER);
        otpVerificationActivity = this;
    }

    private void verifyOTP(String otp) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
            BuildConfig.OTP_URL + "/api/v1/otp?otp=" + otp + "&phone_number=" + Uri.encode(PhoneVerificationActivity.getPhoneNumberForCountry(phoneNumber)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                sharedPrefsHelper.saveValue(SharedPrefsHelper.PHONE_NUMBER, phoneNumber,
                    new SharedPrefsHelper.OnEventListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Intent intent = new Intent(otpVerificationActivity,
                            LanguageSelectionActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String s) {
                        String text = "Temp Error. Please try again";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                String text = "Error while Verifying OTP";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
            0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        HttpUtils.getRequestQueue().add(jsonObjectRequest);
    }

    public void verifyOTP(View view) {
        Pinview otpPin = findViewById(R.id.otp_verification);
        String otpPinValue = otpPin.getValue();
        Log.d(TAG, "otp value is " + otpPinValue);
        verifyOTP(otpPinValue);
    }
}
