package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ErrorCodes;

public class PasswordChange extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordchaange);
    }

    public void changePassword(View v) {
        TextView oldpassword = findViewById(R.id.oldpass);
        TextView password = findViewById(R.id.password);
        TextView confPassword = findViewById(R.id.passonf);
        AppSettings appSettings = new AppSettings(this);
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess()) {
                            CustomToast.show(getString(R.string.pass_success), R.drawable.icon_success, PasswordChange.this);
                            finish();
                        } else {
                            CustomToast.show(ErrorCodes.getError(getErrorCode()), R.drawable.icon_error, PasswordChange.this);
                        }
                    }
                });
            }
        };
        if (!password.getText().toString().equals(confPassword.getText().toString())) {
            CustomToast.show(getString(R.string.pass_dmatch), R.drawable.icon_error, PasswordChange.this);
        } else {
            Methods.changePassword(appSettings.getString("session"), oldpassword.getText().toString(), password.getText().toString(), apiRunnable, this);
        }
    }
}