package ru.etysoft.cute.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateReceiver extends BroadcastReceiver {

    private boolean online = true;

    private Runnable onlineRunnable;
    private Runnable offlineRunnable;


    public NetworkStateReceiver(Runnable onlineRunnable, Runnable offlineRunnable) {
        this.offlineRunnable = offlineRunnable;
        this.onlineRunnable = onlineRunnable;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();
        if (ni == null || ni.getState() != NetworkInfo.State.CONNECTED) {
            if (online) {
                try {
                    offlineRunnable.run();
                } catch (Exception e) {
                    Logger.logReceiver("Error running runnable!");
                }
                online = false;
            }
        } else {
            if (!online) {
                try {
                    onlineRunnable.run();
                } catch (Exception e) {
                    Logger.logReceiver("Error running runnable!");
                }
                online = true;
            }
        }
    }


}