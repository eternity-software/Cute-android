package ru.etysoft.cute.components;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * Easy-to-implement zoomable and slidable ImageView
 *
 * For implementation you must create imageActionListener and a parent view
 * of image (next "ImageContainer")
 *
 * That's all!
 *
 * @author Mikahil Karlov
 */
public class PreviewImageView extends androidx.appcompat.widget.AppCompatImageView {

    private View imageContainer;
    private boolean isZoomed = false;
    private ImageActionsListener actionsListener;

    private float downY = 0;
    private float downX = 0;
    private float zoomIncrease = 1f;
    private float defY = 0;
    private float defX = 0;

    private long lastClickedTimestamp;

    public PreviewImageView(Context context) {
        super(context);
        init();
    }


    public PreviewImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setImageContainer(View imageContainer) {
        this.imageContainer = imageContainer;
    }


    public void setActionsListener(ImageActionsListener actionsListener) {
        this.actionsListener = actionsListener;
    }

    public void returnToDefaultPos()
    {
        isZoomed = false;
        zoomIncrease = 1f;
        imageContainer.animate().x(defX).y(defY).setDuration(600).setInterpolator(new DecelerateInterpolator(5f)).start();
        imageContainer.animate().scaleX(1f).scaleY(1f).setDuration(600).setInterpolator(new DecelerateInterpolator(5f)).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                actionsListener.onExitZoom();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private boolean isDoubleClick()
    {
        if(System.currentTimeMillis() - lastClickedTimestamp < 200)
        {
            lastClickedTimestamp = System.currentTimeMillis();
            return true;
        }
        else
        {
            lastClickedTimestamp = System.currentTimeMillis();
            return  false;
        }
    }

    private void zoomUpdate()
    {
        if(zoomIncrease == 1f)
        {
            zoomIncrease = 2f;
        }
        else if(zoomIncrease == 2f)
        {
            zoomIncrease = 3f;
        }
        else
        {
            zoomIncrease = 1f;
        }
    }

    private void init() {

        actionsListener = new ImageActionsListener() {


            @Override
            public void onSlideDown() {

            }

            @Override
            public void onSlideUp() {

            }

            @Override
            public void onZoom(float increase) {

            }

            @Override
            public void onExitZoom() {

            }
        };

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                float deltaY = (downY - event.getY());
                float deltaX = (downX - event.getX());
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        downY = event.getY();
                        downX = event.getX();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        float k = 1f;

                        if(!isZoomed)
                        {
                            k = 1.5f;
                        }

                        float newY = (imageContainer.getY() - deltaY) / k;
                        float newX = (imageContainer.getX() - deltaX) / k;

                        if(isZoomed)
                        {
                            imageContainer.setX(newX);
                        }

                        imageContainer.setY(newY);

                        break;
                    case MotionEvent.ACTION_UP:

                        if(!isZoomed) {
                            if (Math.abs(downY - event.getRawY()) > getHeight() / 8) {
                                if(downY - event.getRawY() < 0)
                                {
                                    actionsListener.onSlideDown();

                                }
                                else
                                {
                                    actionsListener.onSlideUp();
                                }

                            } else {
                                returnToDefaultPos();
                            }
                        }
                        else
                        {
                            if(event.getRawY() < 0)
                            {
                                imageContainer.animate().y(0).setDuration(600).setInterpolator(new DecelerateInterpolator(5f)).start();
                            }
                            if (Math.abs(downY - event.getRawY()) > imageContainer.getHeight() / (zoomIncrease*zoomIncrease)) {
                                returnToDefaultPos();
                                isZoomed = false;
                            }
                            if (Math.abs(downX - event.getRawX()) > imageContainer.getWidth() / (zoomIncrease*zoomIncrease)) {
                                returnToDefaultPos();
                                isZoomed = false;
                            }

                        }



                        if(isDoubleClick())
                        {

                            isZoomed = !isZoomed;
                            if(zoomIncrease == 2f)
                            {
                                isZoomed = true;
                            }
                            if(isZoomed)
                            {
                                zoomUpdate();
                                imageContainer.animate().y((imageContainer.getHeight() / 2f) - event.getY()).x((imageContainer.getWidth() / 2f) - event.getX()).scaleY(zoomIncrease).scaleX(zoomIncrease).setDuration(600).setInterpolator(new DecelerateInterpolator(5f))
                                        .setListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                actionsListener.onZoom(zoomIncrease);
                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        }).start();
                            }
                            else
                            {

                              returnToDefaultPos();
                            }
                        }


                    case MotionEvent.ACTION_CANCEL:

                        break;
                }

                return true;

            }
        });

    }

    public boolean isZoomed() {
        return isZoomed;
    }

    public interface ImageActionsListener
    {
        /**
         *  Invokes on long image slide
         */


        void onSlideDown();
        void onSlideUp();


        void onZoom(float increase);
        void onExitZoom();
    }


}
