package ru.etysoft.cute;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

public class EditChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chat);
        Slidr.attach(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}