package ru.etysoft.cute.media;

import static android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY;
import static android.media.AudioManager.AUDIOFOCUS_GAIN;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothHeadset;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.AudioRouting;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import java.io.IOException;

import ru.etysoft.cute.R;

public class MediaService extends Service {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    public static final String TAG = "MediaSessionService";
    public static final int NOTIFICATION_ID = 888;
    private MediaNotificationManager mediaNotificationManager;
    private MediaSessionCompat mediaSession;
    private static boolean isPaused;

    private static String artistName = "..";
    private static String trackName = ".";

    private static boolean isBuffering;

    public static boolean isStopped = true;




    @Override
    public void onCreate() {
        super.onCreate();


        isStopped = false;
        isPaused = false;
        mediaNotificationManager = new MediaNotificationManager(this);
        mediaSession = new MediaSessionCompat(this, "SOME_TAG");




        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPause() {
                super.onPause();
                pause();
            }

            @Override
            public void onPlay() {
                super.onPlay();
                play();
            }
        });




        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                updateNotification();
                isBuffering = false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {


                    updateNotification();


            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPaused = true;
                updateNotification();
            }
        });



        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                updateNotification();
                isBuffering = false;
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


                int requestResult = audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                                                                       @Override
                                                                       public void onAudioFocusChange(int focusChange) {
                                                                           switch (focusChange) {
                                                                               case AudioManager.AUDIOFOCUS_LOSS:
                                                                               case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                                                                   mediaPlayer.pause();
                                                                                   isPaused = true;
                                                                                   break;
                                                                               case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                                                                                   mediaPlayer.setVolume(0.5f, 0.5f);
                                                                                   break;
                                                                               case AUDIOFOCUS_GAIN:
                                                                                   isPaused = false;
                                                                                   if (!mediaPlayer.isPlaying())
                                                                                       isPaused = false;
                                                                                       mediaPlayer.start();
                                                                                   mediaPlayer.setVolume(1.0f, 1.0f);
                                                                                   break;
                                                                           }
                                                                           updateNotification();

                                                                       }
                                                                   },
                        AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN);
            }
        });


        mediaSession.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onSeekTo(long pos) {

                if(!isBuffering)
                {
                    mediaPlayer.seekTo((int) pos);
                    stopNotification();
                    isBuffering = true;
                }



            }


            @Override
            public void onPlay() {

                mediaPlayer.start();
                isPaused = false;
                if(!isBuffering)
                {
                    updateNotification();
                }

            }

            @Override
            public void onPause() {

                mediaPlayer.pause();
                isPaused = true;
                if(!isBuffering)
                {
                    updateNotification();
                }
            }
        });
        updateNotification();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateNotification();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(MediaActionsReceiver.BROADCAST_INTENT));



        mediaSession.setPlaybackState(getState());
    }

    public void updateNotification() {

        Notification notification =
                mediaNotificationManager.getNotification(
                        getMetadata(), getState(), mediaSession.getSessionToken());
        mediaSession.setPlaybackState(getState());
        mediaSession.setMetadata(getMetadata());
        startForeground(NOTIFICATION_ID, notification);
        if(isPaused)
        {
            stopForeground(false);
            isStopped = true;
        }

    }



    public void stopNotification() {

        Notification notification =
                mediaNotificationManager.getNotification(
                        getMetadata(), getState(), mediaSession.getSessionToken());
        mediaSession.setPlaybackState(null);
        mediaSession.setMetadata(null);
        startForeground(NOTIFICATION_ID, notification);
    }

    public MediaMetadataCompat getMetadata() {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName);
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, trackName);

        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART,
                BitmapFactory.decodeResource(getResources(), R.drawable.empty_cover));

        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration()
        );
        return builder.build();
    }


    public static void play(String artistName, String trackName, String url, Bitmap cover)
    {
        try {
            MediaService.artistName = artistName;
            MediaService.trackName = trackName;
            if(url != null)
            {
                mediaPlayer.setDataSource(url);
            }
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPaused =false;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    public static boolean isPaused() {
        return isPaused;
    }

    public static void togglePlay() {
        if (isPaused)
        {
            play();
        }
        else
        {
            pause();
        }
    }

    public static void play()
    {

        if(isPaused)
        {
            isPaused = false;

            mediaPlayer.start();
            mediaPlayer.setVolume(100, 100);
        }
        else
        {
            isPaused = true;
            pause();
        }

    }

    public static void pause()
    {
        if(mediaPlayer != null)
        {
            isPaused = true;
            mediaPlayer.pause();
        }
    }

    private PlaybackStateCompat getState() {
        // long actions = mediaPlayer.isPlaying() ? PlaybackStateCompat.ACTION_PAUSE : PlaybackStateCompat.ACTION_PLAY;
        int state = mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();

        stateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_SEEK_TO
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);




        stateBuilder.setState(state,
             mediaPlayer.getCurrentPosition(),
                1.0f);
        return stateBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
