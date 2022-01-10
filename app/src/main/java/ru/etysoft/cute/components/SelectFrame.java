package ru.etysoft.cute.components;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import ru.etysoft.cute.R;

public class SelectFrame extends View {

    private float scaleFrom = 0.9f;
    private float scaleTo = 1f;
    private View selectedView;
    private long durationSelect = 600;
    private long durationUnselect = 200;

    public SelectFrame(Context context) {
        super(context);
        init();
    }

    public SelectFrame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectFrame(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setEnabled(false);
        setAlpha(0f);
        setBackground(getResources().getDrawable(R.drawable.select_frame));
    }

    public void unselect(Animator.AnimatorListener callback) {
        if (selectedView == null) return;
        animate().alpha(0f).scaleX(scaleFrom).scaleY(scaleFrom).setInterpolator(new DecelerateInterpolator(1f)).setListener(callback).setDuration(durationUnselect).start();
    }

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    public void select(View v) {

        if (selectedView == null) {
            getLayoutParams().width = v.getWidth();
            getLayoutParams().height = v.getHeight();
            requestLayout();
            setScaleX(scaleFrom);
            setScaleY(scaleFrom);
            setAlpha(0f);

            setX(v.getX());
            setY(v.getY());
            animate().alpha(1f).scaleX(scaleTo).scaleY(scaleTo).setInterpolator(new DecelerateInterpolator(3f)).setListener(null).setDuration(durationSelect).start();
            selectedView = v;
        } else {
            if (v == selectedView) return;
            unselect(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    selectedView = null;
                    select(v);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

    }
}
