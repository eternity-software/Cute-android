package ru.etysoft.cute.activities.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MainActivity;
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
        overridePendingTransition(R.anim.side_from_top, R.anim.slide_down);

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
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        Intent intent = new Intent(SignInActivity.this, MeetActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String text) {
        errorView.setVisibility(View.VISIBLE);
        errorText.setText(text);
        Animation appearAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        appearAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        appearAnimation.setFillAfter(true);
        errorView.startAnimation(appearAnimation);
    }

    @Override
    public void hideError() {
        Animation hideAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        hideAnimation.setFillAfter(true);
        hideAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        errorView.startAnimation(hideAnimation);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                errorView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        errorView.startAnimation(hideAnimation);
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
