package ru.etysoft.cute.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

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

}
