package ru.etysoft.cute.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Conversation;
import ru.etysoft.cute.utils.SendorsControl;

public class NotificationService extends Service {

    public static Runnable runnable = null;
    public static String KEY = "nservice";
    public Context context = this;
    public Handler handler = null;
    private AppSettings appSettings;
    public static boolean isLastError = false;
    public static int notifid = 42;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void log(String info) {
        String timeStamp = "[" + new SimpleDateFormat("dd.MM HH:mm:ss").format(Calendar.getInstance().getTime()) + "]";
        if (appSettings.hasKey(KEY)) {
            appSettings.setString(KEY, appSettings.getString(KEY) + "\n" + timeStamp + ": " + info);
        } else {
            appSettings.setString(KEY, timeStamp + ": " + info);
        }

    }

    public boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {

                    return true;

                }
            }
        }
        notifyBanner(context, "Status", "InBackground");
        return false;
    }

    private boolean isApplicationBroughtToBackground() {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        boolean isBack = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        if (!isBack) {
            //   notifyBanner(context, "Status", "OnTop");
        } else {
            //    notifyBanner(context, "Status", "InBackground");
        }
        return isBack;
    }

    int ts = 0;

    public void longPoll() {
        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (appSettings.getString("session") != null) {

                    }
                }
            }
        });

        waiter.start();
    }


    private static String CHANNEL_ID = "Messages";

    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLightColor(Color.BLUE);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            long[] vibrate = new long[]{200, 200, 200, 200, 200};
            String description = "SSS";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(vibrate);
            channel.setLightColor(Color.CYAN);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void notifyBannerNewMessage(Context context, String title, String text, String cid, String name) {
        // до версии Android 8.0 API 26
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        long[] vibrate = new long[]{200, 200, 200, 200, 200};
        Intent notificationIntent = new Intent(context, Conversation.class);
        notificationIntent.putExtra("cid", cid);
        notificationIntent.putExtra("isd", false);
        notificationIntent.putExtra("name", name);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);


        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setSmallIcon(R.drawable.logo_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(text))
                .setTicker("Cute")
                .setLights(Color.RED, 3000, 3000)
                .setAutoCancel(true); // автоматически закрыть уведомление после нажатия

        SendorsControl.vibrate(getApplicationContext(), 100);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Альтернативный вариант
        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        createChannelIfNeeded(notificationManager);


        //LED

        builder.getNotification().ledARGB = Color.RED;
        builder.getNotification().ledOffMS = 0;
        builder.getNotification().ledOnMS = 1;
        builder.getNotification().flags = builder.getNotification().flags | Notification.FLAG_SHOW_LIGHTS;
        builder.setLights(Color.RED, 3000, 3000);
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);

        notifid++;
        notificationManager.notify(notifid, builder.build());
    }

    public void notifyBanner(Context context, String title, String text) {
        // до версии Android 8.0 API 26
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        long[] vibrate = new long[]{1000, 1000, 1000, 1000, 1000};
        Intent notificationIntent = new Intent(context, Conversation.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setSmallIcon(R.drawable.logo_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(text))
                .setContentIntent(contentIntent)
                .setTicker("Cute")
                .setLights(Color.RED, 3000, 3000)
                .setAutoCancel(true); // автоматически закрыть уведомление после нажатия

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Альтернативный вариант
        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        createChannelIfNeeded(notificationManager);


        //LED

        builder.getNotification().ledARGB = Color.RED;
        builder.getNotification().ledOffMS = 0;
        builder.getNotification().ledOnMS = 1;
        builder.getNotification().flags = builder.getNotification().flags | Notification.FLAG_SHOW_LIGHTS;
        builder.setLights(Color.RED, 3000, 3000);
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);

        notifid++;
        notificationManager.notify(notifid, builder.build());
    }

    @Override
    public void onCreate() {
        appSettings = new AppSettings(this);
        createNotificationChannel();
        longPoll();
        log("Сервис создан.");

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                log("Сервис активен.");
             //   notifyBanner(getApplicationContext(), "хуйхуйхуй");
                handler.postDelayed(runnable, 5000);
            }
        };

        handler.postDelayed(runnable, 1500);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        log("Сервис остановлен!");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        log("Сервис стартовал.");
    }
}