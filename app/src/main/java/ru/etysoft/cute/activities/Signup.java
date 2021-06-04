package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CustomToast;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
    }

    public void back(View v) {
      onBackPressed();
    }

    public void signUpButton(View v) {
        TextView nicknameView = findViewById(R.id.nickname);
        TextView passwordView = findViewById(R.id.password);
        TextView passwordConfirmView = findViewById(R.id.password_confirm);
        TextView emailView = findViewById(R.id.email);

        final String login = nicknameView.getText().toString();
        final String password = passwordView.getText().toString();
        final String passwordConfirm = passwordConfirmView.getText().toString();
        final String email = emailView.getText().toString();

        if (Methods.hasInternet(this)) {
            if (!passwordConfirm.equals(password) | password.equals("")) {
                CustomToast.show(getString(R.string.errcode_AM001_3_1), R.drawable.icon_error, Signup.this);
                return;
            }
            if (login.equals("")) {
                CustomToast.show(getString(R.string.errcode_AM002_1), R.drawable.icon_error, Signup.this);
                return;
            }
            if (!email.contains("@")) {
                CustomToast.show(getString(R.string.errcode_AM001_2), R.drawable.icon_error, Signup.this);
                return;
            }
            APIRunnable apiRunnable = new APIRunnable() {
                @Override
                public void run() {
                    AppSettings appSettings = new AppSettings(getApplicationContext());
                    try {
                        if (isSuccess()) {
                            JSONObject jObject = new JSONObject(this.getResponse());
                            JSONObject data = jObject.getJSONObject("data");
                            String session = data.getString("session_key");
                            appSettings.setString("session", session);
                            appSettings.setString("email", email);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Signup.this, Confirmation.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, Signup.this);
                            }
                        });
                    }
                }
            };
            Methods.createAccount(login, password, email, apiRunnable, Signup.this);
        } else {
            CustomToast.show(getString(R.string.err_no_internet), R.drawable.icon_error, Signup.this);
        }
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        Intent intent = new Intent(Signup.this, Meet.class);
        startActivity(intent);
        finish();
    }
}
