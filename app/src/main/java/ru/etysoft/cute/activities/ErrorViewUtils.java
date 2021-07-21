package ru.etysoft.cute.activities;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import ru.etysoft.cute.R;

public class ErrorViewUtils {
    public static void hide(final View errorView) {
        Animation hideAnimation = AnimationUtils.loadAnimation(errorView.getContext(), R.anim.zoom_out);
        hideAnimation.setFillAfter(true);
        hideAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        errorView.startAnimation(hideAnimation);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                errorView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        errorView.startAnimation(hideAnimation);
    }

    public static void show(String text, View errorView, TextView errorText) {
        errorView.setVisibility(View.VISIBLE);
        errorText.setText(text);
        Animation appearAnimation = AnimationUtils.loadAnimation(errorView.getContext(), R.anim.zoom_in);
        appearAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        appearAnimation.setFillAfter(true);
        errorView.startAnimation(appearAnimation);
    }
}
