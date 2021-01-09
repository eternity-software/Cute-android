package ru.etysoft.cute.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.etysoft.cute.AppSettings;

public class NotificationService extends Service {

    public static Runnable runnable = null;
    public static String KEY = "nservice";
    public Context context = this;
    public Handler handler = null;
    private AppSettings appSettings;

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

    @Override
    public void onCreate() {
        appSettings = new AppSettings(this);

        log("Сервис создан.");

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                log("Сервис активен.");
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);
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