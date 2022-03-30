package ru.etysoft.cute.activities;

import android.graphics.Bitmap;
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
import ru.etysoft.cute.components.dynamic.ThemeImageView;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.media.MediaActions;
import ru.etysoft.cute.media.MediaService;
import ru.etysoft.cute.themes.Theme;

public class MusicPlayerActivity extends AppCompatActivity implements MediaService.MediaServiceCallback {

    private boolean isDestroyed = false;
    private boolean isSeeking = false;
    private SeekBar seekBar;
    private ImageView playButton;
    private ThemeImageView prevButton;
    private ThemeImageView nextButton;
    private ImageView coverView;
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
        coverView = findViewById(R.id.coverView);
        trackNameTextView = findViewById(R.id.trackNameView);
        playButton = findViewById(R.id.playButton);
        timeCurrent = findViewById(R.id.timeCurrent);
        timeDuration = findViewById(R.id.timeDuration);
        prevButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 MediaService.previous();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaService.next();
            }
        });


        Theme.applyBackground(findViewById(R.id.rootView));

        onTrackChanged(MediaService.getTrackName(), MediaService.getArtistName(), MediaService.getBitmap());

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
                while (!isDestroyed) {
                    try {
                        Thread.sleep(200);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isSeeking) seekBar.setProgress(MediaService.getProgress());
                                if (MediaService.isPaused()) {
                                    playButton.setImageResource(R.drawable.icon_play);
                                } else {
                                    playButton.setImageResource(R.drawable.icon_pause_mini);
                                }

                                if(MediaService.canSkipNext())
                                {
                                    try {
                                        nextButton.setColorFilter(Theme.getColor("colorMainLight"));
                                    } catch (NoSuchValueException e) {
                                        nextButton.setColorFilter(Theme.getColor(getApplicationContext(), Theme.getResId(String.valueOf("colorMainLight"), R.color.class)));
                                    }
                                }
                                else
                                {
                                    try {
                                        nextButton.setColorFilter(Theme.getColor("colorIconSupportTint"));
                                    } catch (NoSuchValueException e) {
                                        nextButton.setColorFilter(Theme.getColor(getApplicationContext(), Theme.getResId(String.valueOf("colorIconSupportTint"), R.color.class)));
                                    }
                                }

                                if(MediaService.canSkipPrevious())
                                {
                                    try {
                                        prevButton.setColorFilter(Theme.getColor("colorMainLight"));
                                    } catch (NoSuchValueException e) {
                                        prevButton.setColorFilter(Theme.getColor(getApplicationContext(), Theme.getResId(String.valueOf("colorMainLight"), R.color.class)));
                                    }
                                }
                                else
                                {
                                    try {
                                        prevButton.setColorFilter(Theme.getColor("colorIconSupportTint"));
                                    } catch (NoSuchValueException e) {
                                        prevButton.setColorFilter(Theme.getColor(getApplicationContext(), Theme.getResId(String.valueOf("colorIconSupportTint"), R.color.class)));
                                    }
                                }

                                timeCurrent.setText(getDuration(MediaService.getProgressMillis()));
                                if(!MediaService.isPreparing)
                                {
                                    timeDuration.setText(getDuration(MediaService.getDuration()));
                                }

                                trackNameTextView.setText(MediaService.getTrackName());
                                artistTextView.setText(MediaService.getArtistName());
                                coverView.setImageBitmap(MediaService.getBitmap());
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

    private static String getDuration(int millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public void onPlayButtonClick(View v) {
        if (MediaService.isPaused()) {

            playButton.setImageResource(R.drawable.icon_pause_mini);
            MediaService.play();

        } else {
            playButton.setImageResource(R.drawable.icon_play);
            MediaService.pause();


        }
        MediaService.getMediaServiceInstance().updateNotification("onPlayButtonClick");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }

    @Override
    public void onTrackChanged(String name, String artist, Bitmap bitmap) {


    }

    @Override
    public void onServiceStopped() {

    }
}