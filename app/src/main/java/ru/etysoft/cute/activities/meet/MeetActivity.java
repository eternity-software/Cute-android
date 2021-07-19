package ru.etysoft.cute.activities.meet;

import android.os.Bundle;
import android.view.View;
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
    private MeetPresenter meetPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        initializeComponents();
    }

    @Override
    public void setEasterText() {
        Random random = new Random();
        int i = random.nextInt(10);
        if (i + 1 == 5) {
            subtitleView.setText("IM UR TOVARISH");
        }
    }

    @Override
    public void initializeComponents() {
        subtitleView = findViewById(R.id.subTextView);
        buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignUp = findViewById(R.id.button_sign_up);

        meetPresenter = new MeetPresenter(this);

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
    }

}
