package ru.etysoft.cute.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Meet.MeetActivity;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ErrorCodes;

public class Confirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        AppSettings appSettings = new AppSettings(this);

        TextView email = findViewById(R.id.emailto);
        email.setText(appSettings.getString("email"));
    }

    public void back(View v) {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        Intent intent = new Intent(Confirmation.this, MeetActivity.class);
        startActivity(intent);
    }

    public void confirmButton(View v) {
        TextView codeView = findViewById(R.id.confirmationCode);

        final AppSettings appSettings = new AppSettings(getApplicationContext());
        final String code = codeView.getText().toString();


        if (Methods.hasInternet(this)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final String response = Methods.sendConfirmationCode(appSettings.getString("session"), code);
                    try {
                        final JSONObject jObject = new JSONObject(response);
                        String type = jObject.getString("type");
                        if (type.equals("success")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Confirmation.this, MainActivity.class);
                                    startActivity(intent);
                                    CustomToast.show(getString(R.string.sign_up_success), R.drawable.icon_success, Confirmation.this);
                                }
                            });
                        } else if (type.equals("error")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        CustomToast.show(ErrorCodes.getError(jObject.getString("code")), R.drawable.icon_error, Confirmation.this);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, Confirmation.this);
                            }
                        });
                    }
                }
            });
            thread.start();
        } else {
            CustomToast.show(getString(R.string.err_no_internet), R.drawable.icon_error, Confirmation.this);
        }

    }

    public void sendAnotherConfirmationCode(View v) {
        if (Methods.hasInternet(this)) {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AppSettings appSettings = new AppSettings(getApplicationContext());
                    String session = appSettings.getString("session");
                    if (session != null) {
                        String response = Methods.sendNewConfirmationCode(session);
                        try {
                            final JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("type").equals("success")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CustomToast.show(getString(R.string.confirmation_send), R.drawable.icon_success, Confirmation.this);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            CustomToast.show(ErrorCodes.getError(jsonObject.getString("code")), R.drawable.icon_error, Confirmation.this);
                                        } catch (JSONException e) {
                                            CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, Confirmation.this);
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, Confirmation.this);
                                }
                            });
                        }

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomToast.show(getString(R.string.errcode_AM003_1), R.drawable.icon_error, Confirmation.this);
                            }
                        });
                    }
                }
            });
            thread.start();
        } else {
            CustomToast.show(getString(R.string.err_no_internet), R.drawable.icon_error, Confirmation.this);
        }
    }


    public void cantReceive(View v) {
        Uri address = Uri.parse("https://pony.town/");
        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);

        if (openLinkIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openLinkIntent);
        } else {
            CustomToast.show("Error!", R.drawable.icon_error, Confirmation.this);
        }


    }

    @Override
    public void onBackPressed() {


    }
}
