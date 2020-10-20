package ru.etysoft.cute.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import ru.etysoft.cute.R;

public class EasterEgg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg);
        final ImageView img = findViewById(R.id.eggimg);


        String url = "https://ru.muzikavsem.org/dl/604678494/Molchat_Doma_-_Toska_(ru.muzikavsem.org).mp3";

        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(url);
            mp.prepare();
            mp.setVolume(100, 100);
            mp.start();
        } catch (IOException e) {
            Log.e("EGG", "prepare() failed");
        }

    }


}