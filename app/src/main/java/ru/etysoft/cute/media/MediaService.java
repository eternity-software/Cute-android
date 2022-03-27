package ru.etysoft.cute.media;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MediaService extends Service {
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static final String TAG = "MediaSessionService";
    public static final int NOTIFICATION_ID = 888;
    private MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaNotificationManager = new MediaNotificationManager(this);
        mediaSession = new MediaSessionCompat(this, "SOME_TAG");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                mediaPlayer.start();
            }

            @Override
            public void onPause() {
                mediaPlayer.pause();
            }
        });
        Notification notification =
                mMediaNotificationManager.getNotification(
                        getMetadata(), getState(), mediaSession.getSessionToken());

        startForeground(NOTIFICATION_ID, notification);
    }

    public MediaMetadataCompat getMetadata() {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "artist");
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "title");
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration()
        );
        return builder.build();
    }

    private PlaybackStateCompat getState() {
        long actions = mediaPlayer.isPlaying() ? PlaybackStateCompat.ACTION_PAUSE : PlaybackStateCompat.ACTION_PLAY;
        int state = mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(actions);
        stateBuilder.setState(state,
                mediaPlayer.getCurrentPosition(),
                1.0f,
                SystemClock.elapsedRealtime());
        return stateBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("android.intent.action.MEDIA_BUTTON".equals(intent.getAction())) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                mediaPlayer.pause();
            } else {
                try {
                    String url = "https://etysoft.ru/egg.mp3";
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setVolume(100, 100);
                mediaPlayer.start();

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
