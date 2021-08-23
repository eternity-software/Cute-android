package ru.etysoft.cute.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
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

    private static void setAppTheme(Activity activity, int state) {

        if (state == ViewDragHelper.STATE_SETTLING) {
            return;
        }

        final @ColorRes int color = getWindowColor(state);
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, color));
        activity.getWindow().setBackgroundDrawable(colorDrawable);
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

    public void attachSlider(final Activity activity) {
        final SlidrConfig slidrConfig = new SlidrConfig.Builder().listener(new SlidrListener() {
            @Override
            public void onSlideStateChanged(int state) {
                if (percent == 0 && state == ViewDragHelper.STATE_IDLE) {
                    return;
                }
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
        Slidr.attach(activity, slidrConfig);
    }
}