package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

import ru.etysoft.cute.R;

public class Profile extends AppCompatActivity {

    private int id;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        id = getIntent().getIntExtra("id", -1);
        Slidr.attach(this);
    }

    public void openImage(View v) {
        Intent intent = new Intent(Profile.this, ImagePreview.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

}