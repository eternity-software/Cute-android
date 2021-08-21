package ru.etysoft.cute.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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

    public static int getColorById(int id, Context context) {
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
        System.out.println(secondNum + "" + firstNum);

        return context.getResources().getColor(context.getResources().getIdentifier("avatar" + secondNum + "" + firstNum, "color", context.getPackageName()));
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
