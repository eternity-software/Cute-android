package ru.etysoft.cute.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateReceiver extends BroadcastReceiver {
    public static final String TAG = NetworkStateReceiver.class.getSimpleName();
    private static boolean online = true;
    public Runnable runnable;

    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();
        if (ni == null || ni.getState() != NetworkInfo.State.CONNECTED) {
            if (online)
                Logger.logReceiver("No internet connection");
            online = false;
        } else {
            if (!online)
                Logger.logReceiver("Internet connection is available");
            runnable.run();
            online = true;
        }
    }


}