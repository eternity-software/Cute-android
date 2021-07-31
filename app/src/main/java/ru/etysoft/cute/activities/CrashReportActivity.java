package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.etysoft.cute.R;

public class CrashReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_report);
        TextView textView = findViewById(R.id.stacktrace_text);
        textView.setText(getIntent().getExtras().getString("stacktrace"));
    }

    @Override
    public void onBackPressed() {

    }
}