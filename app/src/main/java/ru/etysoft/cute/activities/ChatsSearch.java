package ru.etysoft.cute.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

import ru.etysoft.cute.R;

public class ChatsSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_search);
        Slidr.attach(this);
    }
}