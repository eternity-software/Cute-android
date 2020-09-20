package ru.etysoft.cute.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.util.TypedValue;

public class Numbers {


    // Конвертируем dp в пикселы
    public static int dpToPx(float dp, Context context) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    // Получаем локальное время из timestamp
    public static String getTimeFromTimestamp(String timestamp, Context context) {
        return DateFormat.format("HH:mm", Long.parseLong(timestamp) * 1000).toString();
    }

    public static boolean getBooleanFromInt(int i) {
        if (i == 1) {
            return true;
        } else {
            return false;
        }

    }

}
