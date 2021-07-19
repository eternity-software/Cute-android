package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.meet.MeetActivity;

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

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        Intent intent = new Intent(Signup.this, MeetActivity.class);
        startActivity(intent);
        finish();
    }
}
