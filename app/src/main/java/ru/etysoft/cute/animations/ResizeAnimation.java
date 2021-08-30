package ru.etysoft.cute.animations;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
    private final View view;
    private final float toHeight;
    private final float fromHeight;

    private final float toWidth;
    private final float fromWidth;

    private final float toScale = 0f;

    public ResizeAnimation(View view, float fromWidth, float fromHeight, float toWidth, float toHeight) {
        this.toHeight = toHeight;
        this.toWidth = toWidth;
        this.fromHeight = fromHeight;
        this.fromWidth = fromWidth;
        this.view = view;
        setDuration(300);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float height =
                (toHeight - fromHeight) * interpolatedTime + fromHeight;
        float width = (toWidth - fromWidth) * interpolatedTime + fromWidth;
        ViewGroup.LayoutParams p = view.getLayoutParams();
        p.height = (int) height;
        p.width = (int) width;
        view.requestLayout();
    }
}
