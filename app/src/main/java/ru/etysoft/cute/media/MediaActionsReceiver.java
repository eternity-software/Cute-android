package ru.etysoft.cute.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaActionsReceiver extends BroadcastReceiver {

    public final static int ACTION_PLAY = 0;
    public final static int ACTION_PAUSE = 1;
    public final static int ACTION_TOGGLE_PLAY = 2;
    public final static String BROADCAST_INTENT = "media_action_broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("action", 1);

        context.sendBroadcast(new Intent(BROADCAST_INTENT)
                .putExtra("action", action));





    }

    public static void processAction(int action)
    {
        if (action == ACTION_PLAY)
        {
            MediaService.play();
        }
        else if(action == ACTION_PAUSE)
        {
            MediaService.pause();
        }
        else
        {
            MediaService.togglePlay();
        }
    }

    public static Intent createToggleAction(Context context)
    {
        Intent intentAction = new Intent(context,MediaActionsReceiver.class);
        intentAction.putExtra("action", ACTION_TOGGLE_PLAY);
        return intentAction;
    }

    public static Intent createPauseAction(Context context)
    {
        Intent intentAction = new Intent(context,MediaActionsReceiver.class);
        intentAction.putExtra("action", ACTION_PAUSE);
        return intentAction;
    }

    public static Intent createPlayAction(Context context)
    {
        Intent intentAction = new Intent(context,MediaActionsReceiver.class);
        intentAction.putExtra("action", ACTION_PLAY);
        return intentAction;
    }
}
