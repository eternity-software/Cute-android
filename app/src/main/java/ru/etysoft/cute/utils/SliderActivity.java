package ru.etysoft.cute.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrListener;
import com.r0adkll.slidr.util.ViewDragHelper;

import java.util.Locale;

import ru.etysoft.cute.R;

public class SliderActivity {

    /**
     * Keyboard transparent issue
     * Code from https://github.com/r0adkll/Slidr/issues/72
     */

    private float percent = 0;

    public static void setAppTheme(Activity activity, int state) {


        if (state == ViewDragHelper.STATE_SETTLING) {
            return;
        }

        final @ColorRes int color = getWindowColor(state);
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, color));
      //  activity.getWindow().setBackgroundDrawable(colorDrawable);
        activity.getWindow().getDecorView().setBackgroundColor(activity.getResources().getColor(color));

    }

    @ColorRes
    private static int getWindowColor(int state) {
        switch (state) {
            case ViewDragHelper.STATE_IDLE:
                return R.color.colorBackground;
            case ViewDragHelper.STATE_DRAGGING:
                return android.R.color.transparent;
            default:
                String errorMessage = String.format(Locale.getDefault(), "Cannot resolve WindowColor for state: %d", state);
                throw new IllegalArgumentException(errorMessage);
        }
    }


    public SlidrInterface attachSlider(final Activity activity) {
        try {
            setAppTheme(activity, ViewDragHelper.STATE_IDLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        final SlidrConfig slidrConfig = new SlidrConfig.Builder().listener(new SlidrListener() {

            @Override
            public void onSlideStateChanged(int state) {


                setAppTheme(activity, state);
            }

            @Override
            public void onSlideChange(float percent) {
                SliderActivity.this.percent = percent;
            }

            @Override
            public void onSlideOpened() {
            }

            @Override
            public boolean onSlideClosed() {

                return false;
            }
        }).build();
        return Slidr.attach(activity, slidrConfig);
    }
}
