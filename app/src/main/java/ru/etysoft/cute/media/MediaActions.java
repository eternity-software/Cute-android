package ru.etysoft.cute.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaActions extends BroadcastReceiver {

    public final static int ACTION_PLAY = 0;
    public final static int ACTION_PAUSE = 1;
    public final static int ACTION_NEXT = 2;
    public final static int ACTION_PREV = 3;
    public final static String BROADCAST_INTENT = "media_action_broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("action", 1);
        sendAction(action, context);

    }

    public static void sendAction(int action, Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                context.sendBroadcast(new Intent(BROADCAST_INTENT)
                        .putExtra("action", action));
            }
        });
        thread.start();

    }

    public static void processAction(int action) {
        if (action == ACTION_PLAY) {
            MediaService.play();
        } else if (action == ACTION_PAUSE) {
            MediaService.pause();
        } else if (action == ACTION_NEXT) {
            MediaService.next();
        } else if (action == ACTION_PREV) {
            MediaService.previous();
        }


    }


    public static Intent createActionIntent(Context context, int action) {
        Intent intentAction = new Intent(context, MediaActions.class);
        intentAction.putExtra("action", action);
        return intentAction;
    }
    public static Intent createPauseAction(Context context) {
        Intent intentAction = new Intent(context, MediaActions.class);
        intentAction.putExtra("action", ACTION_PAUSE);
        return intentAction;
    }

    public static Intent createPlayAction(Context context) {
        Intent intentAction = new Intent(context, MediaActions.class);
        intentAction.putExtra("action", ACTION_PLAY);
        return intentAction;
    }
}
