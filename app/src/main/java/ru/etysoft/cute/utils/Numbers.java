package ru.etysoft.cute.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.TypedValue;

import ru.etysoft.cute.R;
import ru.etysoft.cute.themes.Theme;

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

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int getColorById(long id, Context context) {
        int color1 = Color.parseColor("#B83ADE");
        int color2 = Color.parseColor("#3a8fde");
        String sid = String.valueOf(id);

        int firstNum;
        int secondNum = 0;


        color1 = Color.parseColor("#3a8fde");
        firstNum = Integer.parseInt(String.valueOf(sid.charAt(sid.length() - 1)));

        if (id > 9) {
            secondNum = 1;

        }


        try {
            return Theme.getColor(context, context.getResources().getIdentifier("avatar" + secondNum + "" + firstNum, "color", context.getPackageName()));

        } catch (Exception e) {
            return Theme.getColor(context, R.color.avatar05);
        }
    }


    // Получаем локальное время из timestamp
    public static String getTimeFromTimestamp(long timestamp, Context context) {
        return DateFormat.format("HH:mm", timestamp).toString();
    }

    public static String getDateFromTimestamp(String timestamp, Context context) {
        return DateFormat.format("EEEE, d MMMM", Long.parseLong(timestamp)).toString();
    }

    public static boolean getBooleanFromInt(int i) {
        return i == 1;

    }

}
