package ru.etysoft.cute.activities.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ErrorViewUtils;
import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.activities.meet.MeetActivity;

public class SignInActivity extends AppCompatActivity implements SignInContract.View {

    private LinearLayout errorView;
    private TextView errorText;
    private Button nextButton;
    private LinearLayout backButton;
    private EditText loginInput;
    private EditText passwordInput;
    private SignInPresenter signInPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        initializeViews();

        signInPresenter = new SignInPresenter(this, this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInPresenter.onSignInButtonClick(String.valueOf(loginInput.getText()),
                        String.valueOf(passwordInput.getText()));
            }
        });

    }

    @Override
    public void initializeViews() {
        errorView = findViewById(R.id.error_view);
        errorText = findViewById(R.id.error_text);
        backButton = findViewById(R.id.button_back);
        nextButton = findViewById(R.id.button_next);
        loginInput = findViewById(R.id.edittext_login);
        passwordInput = findViewById(R.id.edittext_password);
    }

    @Override
    public void setEnabledActionButton(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        Intent intent = new Intent(SignInActivity.this, MeetActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String text) {
        ErrorViewUtils.show(text, errorView, errorText);
    }

    @Override
    public void hideError() {
        ErrorViewUtils.hide(errorView);
    }


    @Override
    public void showMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signInPresenter.onDestroy();
    }
}
