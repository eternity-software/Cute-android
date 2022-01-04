package ru.etysoft.cute.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.utils.Numbers;

public class BorderScaleView extends RelativeLayout {

    private int borderDp = 20;
    private int borderPx;
    private int containerWidth = -1;
    private int containerHeight = -1;
    private View rootView;
    private String TAG = "BorderScaleView";
    private BorderScaleViewListener borderScaleViewListener;

    public void setBorderScaleViewListener(BorderScaleViewListener borderScaleViewListener) {
        this.borderScaleViewListener = borderScaleViewListener;
    }

    public interface BorderScaleViewListener {
        void onCordsChanged(float x, float y);

        void onSizeChanged(int width, int height);
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }

    public void setContainerHeight(int containerHeight) {
        this.containerHeight = containerHeight;
    }

    public BorderScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BorderScaleView(Context context) {
        super(context);
        init();
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.crop_view, this);
        borderPx = Numbers.dpToPx(borderDp, getContext());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onTouchDown(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        onTouchMove(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        onTouchUp(event);
                        break;
                }

                return true;
            }
        });
    }

    private float downX;
    private float downY;
    private float downRawX;
    private float downRawY;
    private int startHeight;
    private int startWidth;
    private float startX;
    private float startY;
    private boolean isBorderTouch;
    private List<Integer> borderTouchType = new ArrayList<>();


    @Override
    public void requestLayout() {
        super.requestLayout();
        if (borderScaleViewListener != null)
            borderScaleViewListener.onSizeChanged(getLayoutParams().width, getLayoutParams().height);
        if (borderScaleViewListener != null) borderScaleViewListener.onCordsChanged(getX(), getY());
    }

    public void setHeight(int height) {

            if (containerHeight != -1 && containerHeight < height) return;
            getLayoutParams().height = height;
            requestLayout();

    }

    public void setWidth(int width) {

            if (containerWidth != -1 && containerWidth < width) return;
            getLayoutParams().width = width;
            requestLayout();

    }

    @Override
    public void setX(float x) {

            super.setX(x);
            if (borderScaleViewListener != null)
                borderScaleViewListener.onSizeChanged(getLayoutParams().width, getLayoutParams().height);
            if (borderScaleViewListener != null) borderScaleViewListener.onCordsChanged(x, getY());

    }

    @Override
    public void setY(float y) {


            super.setY(y);
            if (borderScaleViewListener != null)
                borderScaleViewListener.onSizeChanged(getLayoutParams().width, getLayoutParams().height);
            if (borderScaleViewListener != null) borderScaleViewListener.onCordsChanged(getX(), y);

    }

    public boolean canTranslateX(float x)
    {
        return canTranslate(x, getY(), getLayoutParams().height, getLayoutParams().width);
    }

    public boolean canTranslateY(float y)
    {
        return canTranslate(getX(), y, getLayoutParams().height, getLayoutParams().width);
    }

    public boolean canTranslate(float x, float y, int height, int width) {
        if (y > containerHeight - height) return false;
        if (x > containerWidth - width) return false;
        if (x < 0) return false;
        if (y < 0) return false;
        return true;
    }

    public void center()
    {
        if(containerHeight > 0 && containerWidth > 0)
        {
            setX(containerWidth / 2 - getWidth() / 2);
            setY(containerHeight / 2 - getHeight() / 2);
        }
    }


    private void onTouchDown(MotionEvent event) {
        downX = event.getX();
        downY = event.getY();
        downRawX = event.getRawX();
        downRawY = event.getRawY();
        startX = getX();
        startY = getY();
        startHeight = getHeight();
        startWidth = getWidth();
        Log.d(TAG, "onTouchDown downX=" + downX
                + " downY=" + downY);

        isBorderTouch = false;
        borderTouchType.clear();
        if (downX > getWidth() - borderPx) {
            isBorderTouch = true;
            borderTouchType.add(RIGHT_BORDER);
        } else if (downX < borderPx) {
            isBorderTouch = true;
            borderTouchType.add(LEFT_BORDER);
        }

        if (downY > getHeight() - borderPx) {
            isBorderTouch = true;
            borderTouchType.add(BOTTOM_BORDER);
        } else if (downY < borderPx) {
            isBorderTouch = true;
            borderTouchType.add(TOP_BORDER);
        }
    }

    private void onTouchMove(MotionEvent event) {
        float deltaX = event.getX() - downX;
        float deltaY = event.getY() - downY;
        float deltaRawX = event.getRawX() - downRawX;
        float deltaRawY = event.getRawY() - downRawY;
        Log.d(TAG, "onTouchMove");
        if (isBorderTouch) {

            if (borderTouchType.contains(RIGHT_BORDER)) {
                int newWidth = (int) (startWidth + deltaX);
                Log.d(TAG, "RIGHT_BORDER_TOUCH " + newWidth);
                if (newWidth > borderPx * 2) {
                    setWidth(newWidth);
                }
            } else if (borderTouchType.contains(LEFT_BORDER)) {
                int newWidth = (int) (startWidth - deltaRawX);
                Log.d(TAG, "LEFT_BORDER_TOUCH " + newWidth);

                if (newWidth > borderPx * 2) {
                    setWidth(newWidth);
                    setX(deltaRawX + startX);


                }
            }

            if (borderTouchType.contains(BOTTOM_BORDER)) {
                int newHeight = (int) (startHeight + deltaY);
                Log.d(TAG, "BOTTOM_BORDER_TOUCH " + newHeight);
                if (newHeight > borderPx * 2) {
                    setHeight(newHeight);
                }
            } else if (borderTouchType.contains(TOP_BORDER)) {
                int newHeight = (int) (startHeight - deltaRawY);
                Log.d(TAG, "LEFT_BORDER_TOUCH " + newHeight);

                if (newHeight > borderPx * 2) {
                    setHeight(newHeight);
                    setY(deltaRawY + startY);
                }
            }
        } else {
            float x = deltaRawX + startX;
            float y = deltaRawY + startY;



            if(canTranslateX(x))
            {
                setX(x);
            }

            if(canTranslateY(y))
            {
                setY(y);
            }


        }
    }

    private void onTouchUp(MotionEvent event) {

    }

    public final int RIGHT_BORDER = 0;
    public final int LEFT_BORDER = 1;
    public final int TOP_BORDER = 2;
    public final int BOTTOM_BORDER = 3;
}
