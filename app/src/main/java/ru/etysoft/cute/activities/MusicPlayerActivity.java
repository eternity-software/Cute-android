package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.concurrent.TimeUnit;

import ru.etysoft.cute.R;
import ru.etysoft.cute.media.MediaActions;
import ru.etysoft.cute.media.MediaService;
import ru.etysoft.cute.themes.Theme;

public class MusicPlayerActivity extends AppCompatActivity implements MediaService.MediaServiceCallback {

    private boolean isDestroyed = false;
    private boolean isSeeking = false;
    private SeekBar seekBar;
    private ImageView playButton;
    private TextView artistTextView;
    private TextView trackNameTextView;
    private TextView timeCurrent;
    private TextView timeDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        seekBar = findViewById(R.id.progressSeekBar);
        artistTextView = findViewById(R.id.artistView);
        trackNameTextView = findViewById(R.id.trackNameView);
        playButton = findViewById(R.id.playButton);
        timeCurrent = findViewById(R.id.timeCurrent);
        timeDuration = findViewById(R.id.timeDuration);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);


        Theme.applyBackground(findViewById(R.id.rootView));

        onTrackChanged(MediaService.getTrackName(), MediaService.getArtistName());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                MediaService.setProgress(seekBar.getProgress());
            }
        });



        Thread progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isDestroyed)
                {
                    try {
                        Thread.sleep(200);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!isSeeking) seekBar.setProgress(MediaService.getProgress());
                                if(MediaService.isPaused())
                                {
                                    playButton.setImageResource(R.drawable.icon_play);
                                }
                                else
                                {
                                    playButton.setImageResource(R.drawable.icon_pause_mini);
                                }
                                timeCurrent.setText(getDuration(MediaService.getProgressMillis()));
                                timeDuration.setText(getDuration(MediaService.getDuration()));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        progressThread.start();
    }

    private static String getDuration(int millis)
    {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public void onPlayButtonClick(View v)
    {
        if(MediaService.isPaused())
        {

            playButton.setImageResource(R.drawable.icon_pause_mini);
            MediaService.play();
            MediaService.getMediaServiceInstance().updateNotification();
        }
        else
        {
            playButton.setImageResource(R.drawable.icon_play);
            MediaService.pause();

            MediaService.getMediaServiceInstance().updateNotification();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }

    @Override
    public void onTrackChanged(String name, String artist) {
        trackNameTextView.setText(name);
        artistTextView.setText(artist);
    }

    @Override
    public void onServiceStopped() {

    }
}