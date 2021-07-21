package ru.etysoft.cute.activities.confirmation;

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
import ru.etysoft.cute.activities.MainActivity;

public class ConfirmationActivity extends AppCompatActivity implements ConfirmationContract.View {

    private LinearLayout errorView;
    private TextView errorText;
    private TextView emailText;
    private Button nextButton;
    private LinearLayout backButton;
    private EditText confirmationCodeInput;
    private ConfirmationPresenter confirmationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        initializeViews();
        confirmationPresenter = new ConfirmationPresenter(this, this);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationPresenter.onConfirmButtonClick(String.valueOf(confirmationCodeInput.getText()));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void initializeViews() {
        errorView = findViewById(R.id.error_view);
        errorText = findViewById(R.id.error_text);
        backButton = findViewById(R.id.button_back);
        nextButton = findViewById(R.id.button_next);
        emailText = findViewById(R.id.email);
        confirmationCodeInput = findViewById(R.id.edittext_code);
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
        Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void setEmail(String email) {
        emailText.setText(email);
    }


    @Override
    public void setEnabledActionButton(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }
}
