package ru.etysoft.cute.activities.signup;

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
import ru.etysoft.cute.activities.confirmation.ConfirmationActivity;
import ru.etysoft.cute.activities.meet.MeetActivity;

public class SignUpActivity extends AppCompatActivity implements SignUpContract.View {

    private LinearLayout errorView;
    private TextView errorText;
    private Button nextButton;
    private LinearLayout backButton;
    private EditText loginInput;
    private EditText displayNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;
    private SignUpPresenter signUpPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        initializeViews();

        signUpPresenter = new SignUpPresenter(this, SignUpActivity.this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpPresenter.onSignUpButtonClick(String.valueOf(loginInput.getText()),
                        String.valueOf(displayNameInput.getText()),
                        String.valueOf(emailInput.getText()),
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
        displayNameInput = findViewById(R.id.edittext_displayname);
        emailInput = findViewById(R.id.edittext_email);
        passwordInput = findViewById(R.id.edittext_password);
        passwordConfirmInput = findViewById(R.id.edittext_password_confirm);
    }

    @Override
    public boolean isPasswordsCorrect() {
        String passwordFieldContent = String.valueOf(passwordInput.getText());
        String passwordConfirmFieldContent = String.valueOf(passwordConfirmInput.getText());
        return passwordConfirmFieldContent.equals(passwordFieldContent) && passwordFieldContent.length() > 4;
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        Intent intent = new Intent(SignUpActivity.this, MeetActivity.class);
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
    public void showConfirmationActivity() {
        Intent intent = new Intent(SignUpActivity.this, ConfirmationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signUpPresenter.onDestroy();
    }


    @Override
    public void setEnabledActionButton(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }
}
