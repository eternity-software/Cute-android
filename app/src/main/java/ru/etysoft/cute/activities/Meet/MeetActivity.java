package ru.etysoft.cute.activities.Meet;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import ru.etysoft.cute.R;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;

public class MeetActivity extends AppCompatActivity implements FloatingBottomSheet.BottomSheetListener, MeetContract.View {

    private TextView subtitleView;
    private LinearLayout buttonSignUp;
    private LinearLayout buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);

        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        final MeetPresenter meetPresenter = new MeetPresenter(this);

        subtitleView = findViewById(R.id.subTextView);
        buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignUp = findViewById(R.id.button_sign_up);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetPresenter.onSignInButtonClick(MeetActivity.this);
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetPresenter.onSignUpButtonClick(MeetActivity.this);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
        }

    }

    @Override
    public void setEasterText() {
        Random random = new Random();
        int i = random.nextInt(10);
        if (i + 1 == 5) {
            subtitleView.setText("IM UR TOVARISH");
        }
    }

}
