package ru.etysoft.cute.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import ru.etysoft.cute.R;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;

public class Meet extends AppCompatActivity implements FloatingBottomSheet.BottomSheetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        setContentView(R.layout.activity_meet);

        TextView subtext = findViewById(R.id.subTextView);
        int min = 1;
        int max = 10;
        int diff = max - min;
        Random random = new Random();
        int i = random.nextInt(diff + 1);
        i += min;
        if (i == 5) {
            subtext.setText("IM UR TOVARISH");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    public void goSignIn(View v) {
        Intent intent = new Intent(Meet.this, Signin.class);
        startActivity(intent);
        finish();
    }

    public void goSignUp(View v) {
        Intent intent = new Intent(Meet.this, Signup.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
