package ru.etysoft.cute.media;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MusicPlayerActivity;
import ru.etysoft.cute.activities.main.MainActivity;


/**
 * Keeps track of a notification and updates it automatically for a given MediaSession. This is
 * required so that the music service don't get killed during playback.
 */
public class MediaNotificationManager {

    public static final int NOTIFICATION_ID = 412;

    private static final String TAG = MediaNotificationManager.class.getSimpleName();
    private static final String CHANNEL_ID = "cute_channel";
    private static final int REQUEST_CODE = 501;

    private final MediaService mediaService;

    private final PendingIntent playAction;
    private final PendingIntent pauseAction;

    private final NotificationManager notificationManager;

    public MediaNotificationManager(MediaService musicContext) {
        mediaService = musicContext;

        notificationManager =
                (NotificationManager) mediaService.getSystemService(Service.NOTIFICATION_SERVICE);



        playAction = PendingIntent.getBroadcast(musicContext,1,
               MediaActions.createPlayAction(musicContext)
               ,PendingIntent.FLAG_UPDATE_CURRENT);

        pauseAction = PendingIntent.getBroadcast(musicContext,2,
                MediaActions.createPauseAction(musicContext)
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        notificationManager.cancelAll();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public Notification getNotification(MediaMetadataCompat metadata,
                                        @NonNull PlaybackStateCompat state,
                                        MediaSessionCompat.Token token) {
        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
        MediaDescriptionCompat description = metadata.getDescription();

        NotificationCompat.Builder builder = buildNotification(state, token, isPlaying, description);

        return builder.build();
    }

    private NotificationCompat.Builder buildNotification(@NonNull PlaybackStateCompat state,
                                                         MediaSessionCompat.Token token,
                                                         boolean isPlaying,
                                                         MediaDescriptionCompat mediaDescriptionCompat) {

        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOOrHigher()) {
            createChannel();
        }

        Intent intent = new Intent(mediaService, MusicPlayerActivity.class);
        intent.putExtra("NotiClick",true);
        PendingIntent pIntent = PendingIntent.getActivity(mediaService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mediaService, CHANNEL_ID);
        builder
                .setColor(ContextCompat.getColor(mediaService, R.color.white))
                .setSmallIcon(R.drawable.icon_music)
                // Pending intent that is fired when user clicks on notification.
                .setContentIntent(createContentIntent())
                // Title - Usually Song name.
                .setContentTitle(mediaDescriptionCompat.getTitle())
                .setContentText(mediaDescriptionCompat.getSubtitle())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaService.getMediaSession().getSessionToken()))
                .setLargeIcon(mediaDescriptionCompat.getIconBitmap())
                .setContentIntent(pIntent)
                // When notification is deleted (when playback is paused and notification can be
                // deleted) fire MediaButtonPendingIntent with ACTION_PAUSE.
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mediaService, PlaybackStateCompat.ACTION_PAUSE));


        builder.addAction(R.drawable.icon_skip_previous, "Skip previous", pauseAction);

        if (MediaService.isPaused())
        {
            builder.addAction(R.drawable.icon_play, "Play button", playAction);

        }
        else
        {
            builder.addAction(R.drawable.icon_pause, "Pause button", pauseAction);
        }
        builder.addAction(R.drawable.icon_skip_next, "Skip next", pauseAction);

        return builder;
    }

    // Does nothing on versions of Android earlier than O.
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = "Music";
            // The user-visible description of the channel.
            String description = "Cute music";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);

            mChannel.setVibrationPattern(
                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
            Log.d(TAG, "createChannel: New channel created");
        } else {

            Log.d(TAG, "createChannel: Existing channel reused");
        }
    }

    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mediaService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                mediaService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}