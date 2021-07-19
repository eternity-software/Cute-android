package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

import ru.etysoft.cute.R;

public class ChatsSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_search);
        Slidr.attach(this);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);


        final EditText searchBox = findViewById(R.id.searchBox);
        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                search(String.valueOf(searchBox.getText()));
                return false;
            }
        });

    }


    public void search(String query) {
       // TODO: search
    }

    public void back(View v) {
        onBackPressed();
    }
}