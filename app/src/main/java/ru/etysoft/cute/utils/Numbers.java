package ru.etysoft.cute.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Numbers {

    public static int dpToPx(float dp, Context context) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return (int) px;
    }


    public static String getTimeFromTimestamp(String timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(new Date(Long.parseLong(timestamp)));
    }

}
