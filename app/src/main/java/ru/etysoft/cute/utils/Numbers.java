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

    public static int[] getGradientById(int id) {
        int color1 = Color.parseColor("#B83ADE");
        int color2 = Color.parseColor("#3a8fde");
        String sid = String.valueOf(id);


        color1 = Color.parseColor("#3a8fde");
        switch (String.valueOf(sid.charAt(sid.length() - 1))) {
            case ("1"):
                color1 = Color.parseColor("#21a2b0");
                break;
            case ("2"):
                color1 = Color.parseColor("#537eb0");
                break;
            case ("3"):
                color1 = Color.parseColor("#a853b0");
                break;
            case ("4"):
                color1 = Color.parseColor("#2d8ca8");
                break;
            case ("5"):
                color1 = Color.parseColor("#701ba6");
                break;
            case ("6"):
                color1 = Color.parseColor("#6e3dd1");
                break;
            case ("7"):
                color1 = Color.parseColor("#3d4bd1");
                break;
            case ("8"):
                color1 = Color.parseColor("#913dd1");
                break;
            case ("9"):
                color1 = Color.parseColor("#3dd1c0");
                break;
            case ("0"):
                color1 = Color.parseColor("#3a4ade");
                break;
        }

        if (id > 9) {
            switch (String.valueOf(sid.charAt(sid.length() - 2))) {
                case ("1"):
                    color2 = Color.parseColor("#21a2b0");
                    break;
                case ("2"):
                    color2 = Color.parseColor("#53b0a5");
                    break;
                case ("3"):
                    color2 = Color.parseColor("#60016D");
                    break;
                case ("4"):
                    color2 = Color.parseColor("#a82d9c");
                    break;
                case ("5"):
                    color2 = Color.parseColor("#1b7ca6");
                    break;
                case ("6"):
                    color2 = Color.parseColor("#6e3dd1");
                    break;
                case ("7"):
                    color2 = Color.parseColor("#3d82d1");
                    break;
                case ("8"):
                    color2 = Color.parseColor("#d13d7a");
                    break;
                case ("9"):
                    color2 = Color.parseColor("#B83ADE");
                    break;
                case ("0"):
                    color2 = Color.parseColor("#7474d6");
                    break;
            }
        }
        if (color1 == color2) {
            color1 = Color.parseColor("#6f299e");
        }
        return new int[]{color1, color2};
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
