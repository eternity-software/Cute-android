package ru.etysoft.cute.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MusicPlayerActivity;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.media.MediaService;
import ru.etysoft.cute.themes.Theme;

public class SmallMusicPlayer extends RelativeLayout implements MediaService.MediaServiceCallback {

    private TextView artistView;
    private TextView trackNameView;
    private ImageView playButton;
    private ImageView coverView;
    private ImageView nextButton;
    private ImageView prevButton;
    private View rootView;
    private boolean isDestroyed;
    private boolean isHandling = true;


    public SmallMusicPlayer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initComponent(context);
    }

    public SmallMusicPlayer(Context context) {
        super(context);
        initComponent(context);
    }

    public void onDestroyed() {
        isDestroyed = true;
    }

    public void setHandling(boolean handling) {
        isHandling = handling;
    }

    private void initComponent(Context context) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.small_player, this);
        isDestroyed = false;

        artistView = rootView.findViewById(R.id.artistNameView);
        trackNameView = rootView.findViewById(R.id.trackNameView);
        prevButton = rootView.findViewById(R.id.prevButton);
        nextButton = rootView.findViewById(R.id.nextButton);
        playButton = rootView.findViewById(R.id.playButton);
        coverView = rootView.findViewById(R.id.coverView);
        Theme.applyBackground(rootView);

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                context.startActivity(intent);

            }
        });

        MediaService.addCallback(this);

        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MediaService.isPaused()) {
                    MediaService.play();
                } else {
                    MediaService.pause();
                }
            }
        });

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaService.next();
            }
        });

        prevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaService.previous();
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isDestroyed) {
                    try {
                        Thread.sleep(50);
                        if (isHandling) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {

                                    if (MediaService.isPaused()) {
                                        playButton.setImageResource(R.drawable.icon_play);
                                    } else {
                                        playButton.setImageResource(R.drawable.icon_pause_mini);
                                    }

                                    if (MediaService.canSkipNext()) {
                                        try {
                                            nextButton.setColorFilter(Theme.getColor("colorMainLight"));
                                        } catch (NoSuchValueException e) {
                                            nextButton.setColorFilter(Theme.getColor(context, Theme.getResId(String.valueOf("colorMainLight"), R.color.class)));
                                        }
                                    } else {
                                        try {
                                            nextButton.setColorFilter(Theme.getColor("colorIconSupportTint"));
                                        } catch (NoSuchValueException e) {
                                            nextButton.setColorFilter(Theme.getColor(context, Theme.getResId(String.valueOf("colorIconSupportTint"), R.color.class)));
                                        }
                                    }

                                    if (MediaService.canSkipPrevious()) {
                                        try {
                                            prevButton.setColorFilter(Theme.getColor("colorMainLight"));
                                        } catch (NoSuchValueException e) {
                                            prevButton.setColorFilter(Theme.getColor(context, Theme.getResId(String.valueOf("colorMainLight"), R.color.class)));
                                        }
                                    } else {
                                        try {
                                            prevButton.setColorFilter(Theme.getColor("colorIconSupportTint"));
                                        } catch (NoSuchValueException e) {
                                            prevButton.setColorFilter(Theme.getColor(context, Theme.getResId(String.valueOf("colorIconSupportTint"), R.color.class)));
                                        }
                                    }

                                    trackNameView.setText(MediaService.getTrackName());
                                    artistView.setText(MediaService.getArtistName());
                                    coverView.setImageBitmap(MediaService.getBitmap());

                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();


    }


    @Override
    public View getRootView() {
        return rootView;
    }


    @Override
    public void onTrackChanged(String name, String artist, Bitmap bitmap) {
        trackNameView.setText(name);
        artistView.setText(artist);
        coverView.setImageBitmap(bitmap);
        setVisibility(VISIBLE);
    }

    @Override
    public void onServiceStopped() {

    }

    @Override
    public void onPlay() {
        setVisibility(VISIBLE);
    }
}
