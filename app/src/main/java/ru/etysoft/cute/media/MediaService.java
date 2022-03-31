package ru.etysoft.cute.media;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.music.MusicAdapter;
import ru.etysoft.cuteframework.methods.music.MusicGetTrackRequest;
import ru.etysoft.cuteframework.models.TrackInfo;

public class MediaService extends Service {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    public static final String TAG = "MediaSessionService";
    public static final int NOTIFICATION_ID = 888;
    private MediaNotificationManager mediaNotificationManager;
    private MediaSessionCompat mediaSession;
    private static boolean isPaused = false;

    private static String artistName;
    private static String trackName;
    private static Bitmap bitmap;
    private static List<TrackInfo> trackList = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private static boolean isBuffering;
    private static MediaService mediaService;
    private static TrackInfo track;

    private static List<MediaServiceCallback> mediaServiceCallbackList = new ArrayList<>();
    public static boolean isStopped = true;
    public static boolean isPreparing = false;
    public static boolean isBackgrounded = false;

    private static boolean requestedPrepared = true;


    public static int getDuration() {

        if(isPreparing) return 0;
        return mediaPlayer.getDuration();
    }

    public static int getProgress() {
        float progress = (float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        return (int) (progress * 100);
    }


    public static int getProgressMillis() {
        return mediaPlayer.getCurrentPosition();
    }


    public static void setProgress(int progress) {

        float progressFloat = (progress / 100f);
        mediaPlayer.seekTo((int) (progressFloat * (float) getDuration()));
    }

    public static void addCallback(MediaServiceCallback mediaServiceCallback) {
        mediaServiceCallbackList.add(mediaServiceCallback);
    }

    public static void removeCallback(MediaServiceCallback mediaServiceCallback) {
        mediaServiceCallbackList.remove(mediaServiceCallback);
    }

    public static void play() {

        if (mediaService == null) return;
        if (isPreparing) return;
        if (isPaused) {
            isPaused = false;
        }
        for (MediaServiceCallback mediaServiceCallback : mediaServiceCallbackList) {
            mediaServiceCallback.onPlay();
        }
        isBackgrounded = false;
        mediaPlayer.start();
        mediaPlayer.setVolume(100, 100);
        getMediaServiceInstance().updateNotification("OnPlay");


    }

    public static boolean canSkipNext() {
        return trackList.indexOf(track) != trackList.size() - 1;
    }

    public static boolean canSkipPrevious()
    {
        return trackList.indexOf(track) > 0;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static String getArtistName() {
        return artistName;
    }


    public static MediaService getMediaServiceInstance() {
        return mediaService;
    }

    public static String getTrackName() {
        return trackName;
    }

    private BroadcastReceiver becomingNoisyReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("MediaServicer", "onCreate");


        mediaService = this;

        isStopped = false;


        mediaNotificationManager = new MediaNotificationManager(this);
        mediaSession = new MediaSessionCompat(this, "SOME_TAG");
        becomingNoisyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MediaService.pause();
                updateNotification("noisy receiver");
            }
        };
        registerReceiver(
                becomingNoisyReceiver,
                new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        mediaSession.setActive(true);

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

        mediaPlayer = new MediaPlayer();
        updateNotification("onCreate");
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                updateNotification("OnSeek complete");
                isBuffering = false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //   updateNotification();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if(!isPreparing)
                {
                    if (mediaPlayer.getDuration() <= mediaPlayer.getCurrentPosition() + 100) {
                        Log.d("MediaService", "OnCompletion");
                         next();
                    }
                    else
                    {
                        Log.d("MediaService", "COMPLETION ERROR dur: " + (mediaPlayer.getDuration() + ", curr: " + mediaPlayer.getCurrentPosition()));
                    }
                }



            }
        });


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                isBuffering = false;
                mediaPlayer.start();
                requestedPrepared = true;
                isPreparing = false;
                isPaused = false;
                updateNotification("OnPrepared");
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


                audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                                                   @Override
                                                   public void onAudioFocusChange(int focusChange) {
                                                       if (!requestedPrepared) {

                                                           switch (focusChange) {
                                                               case AudioManager.AUDIOFOCUS_LOSS:
                                                               case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                                                   mediaPlayer.pause();
                                                                   isPaused = true;
                                                                   isBackgrounded = true;
                                                                   updateNotification("OnAudioFocusChange");
                                                                   break;
                                                               case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                                                                   mediaPlayer.setVolume(0.5f, 0.5f);
                                                                   updateNotification("OnAudioFocusChange");
                                                                   break;
                                                               case AUDIOFOCUS_GAIN:
                                                                   isPaused = false;
                                                                   if (!mediaPlayer.isPlaying())
                                                                       isPaused = false;
                                                                   isBackgrounded = false;
                                                                   mediaPlayer.start();
                                                                   updateNotification("OnAudioFocusChange");
                                                                   mediaPlayer.setVolume(1.0f, 1.0f);
                                                                   break;
                                                           }
                                                       }
                                                       else
                                                       {
                                                           requestedPrepared = false;
                                                       }




                                                   }
                                               },
                        AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN);
            }
        });


        mediaSession.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onSeekTo(long pos) {

                if (!isBuffering) {
                    mediaPlayer.seekTo((int) pos);
                    stopNotification();
                    isBuffering = true;
                }


            }


            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                previous();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                next();
            }

            @Override
            public void onPlay() {

                mediaPlayer.start();
                isPaused = false;
                if (!isBuffering) {
                    updateNotification("OnPlay session");
                }

            }

            @Override
            public void onPause() {

                mediaPlayer.pause();
                isPaused = true;
                if (!isBuffering) {
                    updateNotification("OnPause session");
                }
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateNotification("broadcastReceiver");
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(MediaActions.BROADCAST_INTENT));




        mediaSession.setPlaybackState(getState());
    }

    public void updateNotification(String request) {
        Log.d("MEDIA SER", "Notification updated by" + request );
        registerReceiver(broadcastReceiver, new IntentFilter(MediaActions.BROADCAST_INTENT));
        Notification notification =
                mediaNotificationManager.getNotification(
                        getMetadata(), getState(), mediaSession.getSessionToken());
        mediaSession.setPlaybackState(getState());
        mediaSession.setMetadata(getMetadata());
        startForeground(NOTIFICATION_ID, notification);
        if (isPaused && !isBackgrounded) {
            isStopped = true;
            try {
                unregisterReceiver(broadcastReceiver);
                stopForeground(false);
            } catch (Exception ignored) {

            }


        } else {


        }

    }

    @Override
    public void onDestroy() {
       // super.onDestroy();
        unregisterReceiver(becomingNoisyReceiver);
       // updateNotification("OnDestroy");
        //mediaPlayer.release();
    }

    public void stopNotification() {
        Log.d("MEDIA SER", "Stop Notification updated");
        Notification notification =
                mediaNotificationManager.getNotification(
                        getMetadata(), getState(), mediaSession.getSessionToken());
        mediaSession.setPlaybackState(null);
        mediaSession.setMetadata(null);
        startForeground(NOTIFICATION_ID, notification);
    }

    public static void setTrackList(List<TrackInfo> trackList) {
        MediaService.trackList = trackList;
    }

    public MediaMetadataCompat getMetadata() {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName);
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, trackName);




        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART,
                bitmap);

        if(!isPreparing)
        {
            builder.putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration()
            );
        }

        return builder.build();
    }


    public static void play(TrackInfo track, Bitmap cover) {

        isPreparing = true;


        if(mediaService != null)
        {
            getMediaServiceInstance().updateNotification("play music");
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(mediaService != null) {


                        try {

                            TrackInfo trackInfo = track;
                            if(track.getPath() == null)
                            {
                                MusicGetTrackRequest.MusicGetTrackResponse getTrackResponse = new MusicGetTrackRequest(track.getId()).execute();
                                trackInfo = getTrackResponse.getTrackInfo();

                            }

                            mediaPlayer.reset();
                            MediaService.artistName = trackInfo.getArtist();
                            MediaService.trackName = trackInfo.getName();
                            MediaService.track = track;

                            if (cover == null) {
                                if((track.getPath() == null))
                                {
                                    bitmap = MusicAdapter.getBitmapFromURL(trackInfo.getCover());
                                }
                                else
                                {
                                    bitmap = BitmapFactory.decodeResource(getMediaServiceInstance().getResources(), R.drawable.empty_cover);
                                }

                            } else {
                                bitmap = cover;
                            }

                            if (trackInfo.getPath() != null) {
                                mediaPlayer.setDataSource(trackInfo.getPath());
                            }


                            mediaPlayer.prepareAsync();



                            for (MediaServiceCallback mediaServiceCallback : mediaServiceCallbackList) {
                                mediaServiceCallback.onTrackChanged(trackName, artistName, bitmap);
                            }


                        } catch (Exception e) {

                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        });
        thread.start();

    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    public static boolean isPaused() {
        return isPaused;
    }


    public interface MediaServiceCallback {
        void onTrackChanged(String name, String artist, Bitmap bitmap);

        void onServiceStopped();

        void onPlay();
    }

    public static void next() {
        if (mediaService == null) return;
        if (trackList.contains(track)) {
            int pos = trackList.indexOf(track);
            if (pos != trackList.size() - 1)
            {
                pos++;
                play(trackList.get(pos), null);
                if (isPaused) {
                    isPaused = false;
                }

            }
        }


    }



    public static void previous() {
        if(mediaService == null) return;

        if(trackList.contains(track)) {
            int pos = trackList.indexOf(track);
            if (pos > 0)
            {
                pos -= 1;
                play(trackList.get(pos), null);
                if (isPaused) {
                    isPaused = false;
                }

            }
        }


    }


    public static void pause() {
        if(isPreparing)return;
        if(mediaService == null) return;
        if (mediaPlayer != null) {
            isPaused = true;
            mediaPlayer.pause();
            getMediaServiceInstance().updateNotification("OnPause");
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
        Log.d("MediaService", "onStartCommand");
        updateNotification("onCreate");
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
