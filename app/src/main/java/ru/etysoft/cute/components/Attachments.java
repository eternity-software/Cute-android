package ru.etysoft.cute.components;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImagePreview;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cuteframework.methods.messages.Message;

public class Attachments extends RelativeLayout {


    private ImageView imageView;
    private ForwardedMessage forwardedMessage;
    private View rootView;


    public Attachments(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponent(context, attrs);
    }

    private void initComponent(Context context, AttributeSet attrs) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.attachments, this);

        imageView = findViewById(R.id.imageView);
        forwardedMessage = findViewById(R.id.forwardedMessage);
        imageView.setVisibility(VISIBLE);
        hideForwardedMessage();

    }

    public void hideForwardedMessage()
    {
        forwardedMessage.setVisibility(GONE);
    }

    public void hideImage()
    {
        imageView.setVisibility(GONE);
    }



    public void setForwardedMessageContent(Message message, Context context)
    {
        forwardedMessage.setVisibility(VISIBLE);
        forwardedMessage.setContent(message, context);
    }

    public void show() {
        rootView.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setFillAfter(false);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //and this
        fadeIn.setDuration(100);

        rootView.startAnimation(fadeIn);
    }

    public void hide(final Runnable runAfter) {

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setFillAfter(false);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(100);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runAfter.run();
                rootView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rootView.startAnimation(fadeOut);
    }

    public ForwardedMessage getForwardedMessage() {
        return forwardedMessage;
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public ImageView getImageView() {
        imageView.setVisibility(VISIBLE);
        return imageView;
    }
}
