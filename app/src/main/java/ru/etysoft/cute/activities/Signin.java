package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Meet.MeetActivity;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.api.response.ResponseHandler;
import ru.etysoft.cute.utils.CustomToast;

public class Signin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);


    }

    public void back(View v) {
        onBackPressed();
    }

    public void signInButton(View v) {
        TextView loginView = findViewById(R.id.login);
        TextView passwordView = findViewById(R.id.password);

        final String login = loginView.getText().toString();
        final String password = passwordView.getText().toString();

        if (Methods.hasInternet(this)) {
            APIRunnable apiRunnable = new APIRunnable() {

                @Override
                public void run() {


                    try {
                        ResponseHandler responseHandler = new ResponseHandler(getResponse());
                        if (responseHandler.isSuccess()) {
                            JSONObject jObject = new JSONObject(this.getResponse());
                            AppSettings appSettings = new AppSettings(getApplicationContext());
                            JSONObject data = jObject.getJSONObject("data");
                            String session = data.getString("session_key");
                            appSettings.setString("session", session);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Signin.this, MainActivity.class);
                                    startActivity(intent);
                                    CustomToast.show(getString(R.string.sign_in_success), R.drawable.icon_success, Signin.this);
                                }
                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, Signin.this);
                            }
                        });
                        e.printStackTrace();
                    }


                }

            };

            Methods.login(login, password, this, apiRunnable);
        } else {
            CustomToast.show(getString(R.string.err_no_internet), R.drawable.icon_error, Signin.this);
        }


    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        Intent intent = new Intent(Signin.this, MeetActivity.class);
        startActivity(intent);
        finish();
    }
}
